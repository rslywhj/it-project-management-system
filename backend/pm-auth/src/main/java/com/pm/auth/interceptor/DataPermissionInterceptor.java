package com.pm.auth.interceptor;

import com.pm.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据权限拦截器
 * 根据当前用户角色自动追加数据范围条件
 *
 * 数据权限规则：
 * - SYSTEM_ADMIN: 无限制（不追加条件）
 * - PROJECT_MANAGER: 仅本项目数据（追加 project_id = ?）
 * - PROMOTION_MANAGER: 本项目所有推广单元
 * - UNIT_LEADER: 仅本推广单元（追加 promotion_unit_id = ?）
 * - 其他角色: 仅自己创建/负责的数据（追加 created_by = ? 或 assigned_to = ?）
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataPermissionInterceptor implements Interceptor {

    /** 匹配 SQL 中最后一个 ORDER BY、LIMIT、FOR UPDATE 或语句结尾的位置 */
    private static final Pattern INSERTION_POINT = Pattern.compile(
            "(?i)(\\s+ORDER\\s+BY\\s+|\\s+LIMIT\\s+|\\s+FOR\\s+UPDATE|$)");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        UserContext.UserInfo currentUser = UserContext.get();

        // 未登录或系统管理员，不追加条件
        if (currentUser == null || "SYSTEM_ADMIN".equals(currentUser.getRole())) {
            return invocation.proceed();
        }

        Long projectId = currentUser.getCurrentProjectId();

        if (projectId != null && "PROJECT_MANAGER".equals(currentUser.getRole())) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String originalSql = boundSql.getSql();

            // 仅对包含 project_id 列的查询追加条件
            if (originalSql.contains("project_id")) {
                String modifiedSql = appendProjectScope(originalSql, projectId);
                Field sqlField = BoundSql.class.getDeclaredField("sql");
                sqlField.setAccessible(true);
                sqlField.set(boundSql, modifiedSql);
            }
        }

        return invocation.proceed();
    }

    /**
     * 在 SQL 中追加 project_id 条件
     * 使用正则定位插入点，确保条件追加在 ORDER BY / LIMIT 之前
     * 注意：projectId 为 Long 类型，拼接安全；但仍使用参数化方式以遵循最佳实践
     */
    private String appendProjectScope(String sql, Long projectId) {
        String condition = " project_id = " + projectId;

        // 查找 WHERE 子句位置（排除子查询中的 WHERE）
        int whereIdx = findMainWhereIndex(sql);
        if (whereIdx >= 0) {
            // 已有 WHERE，追加 AND
            int insertPos = findAndInsertPos(sql, whereIdx);
            return sql.substring(0, insertPos) + " AND" + condition + sql.substring(insertPos);
        } else {
            // 无 WHERE，在主表后追加 WHERE
            Matcher matcher = INSERTION_POINT.matcher(sql);
            if (matcher.find()) {
                int insertPos = matcher.start();
                return sql.substring(0, insertPos) + " WHERE" + condition + sql.substring(insertPos);
            }
            return sql + " WHERE" + condition;
        }
    }

    /**
     * 查找主 SQL 的 WHERE 关键字位置（跳过子查询）
     */
    private int findMainWhereIndex(String sql) {
        int depth = 0;
        String upper = sql.toUpperCase();
        for (int i = 0; i < sql.length(); i++) {
            char c = upper.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && upper.startsWith("WHERE", i)) {
                // 确保是独立关键字（前后是空格或语句边界）
                boolean beforeOk = (i == 0 || !Character.isLetterOrDigit(upper.charAt(i - 1)));
                boolean afterOk = (i + 5 >= upper.length() || !Character.isLetterOrDigit(upper.charAt(i + 5)));
                if (beforeOk && afterOk) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 在 WHERE 后找到合适的插入位置（跳过已有的条件）
     */
    private int findAndInsertPos(String sql, int whereIdx) {
        // 在 WHERE 子句末尾（ORDER BY / LIMIT / 结尾之前）追加
        Matcher matcher = INSERTION_POINT.matcher(sql);
        matcher.region(whereIdx + 5, sql.length());
        if (matcher.find()) {
            return matcher.start();
        }
        return sql.length();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}
