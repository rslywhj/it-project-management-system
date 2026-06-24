package com.pm.auth.interceptor;

import com.pm.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据权限拦截器
 * 根据当前用户角色自动追加数据范围条件
 *
 * 数据权限规则（基于 sys_role.data_scope）：
 * - all (SYSTEM_ADMIN): 无限制（不追加条件）
 * - project (PROJECT_ADMIN/PROJECT_MANAGER/PROMOTION_MANAGER/PRODUCT_MANAGER/TESTER): 仅本项目数据
 * - promotion_unit (UNIT_LEADER): 仅本推广单元数据
 * - self (DEVELOPER/EXTERNAL_COLLABORATOR/GUEST): 仅自己创建/负责的数据
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
        if (currentUser == null || "SYSTEM_ADMIN".equals(currentUser.getRole())
                || "super_admin".equals(currentUser.getRole())) {
            return invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();

        String modifiedSql = applyDataPermission(originalSql, currentUser);
        if (modifiedSql != null) {
            Field sqlField = BoundSql.class.getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(boundSql, modifiedSql);
        }

        return invocation.proceed();
    }

    /**
     * 根据用户角色应用数据权限
     * @return 修改后的 SQL，或 null 表示不修改
     */
    private String applyDataPermission(String sql, UserContext.UserInfo user) {
        String role = user.getRole();
        Long projectId = user.getCurrentProjectId();

        // 项目级角色：project_admin, project_manager, promotion_manager, product_manager, tester
        if (isProjectScopeRole(role)) {
            if (projectId != null && sql.contains("project_id")) {
                return appendCondition(sql, "project_id = " + projectId);
            }
            return null;
        }

        // 推广单元级角色：promotion_unit_lead
        if ("promotion_unit_lead".equals(role) || "UNIT_LEADER".equals(role)) {
            // 优先使用推广单元ID，其次使用项目ID
            if (projectId != null && sql.contains("project_id")) {
                return appendCondition(sql, "project_id = " + projectId);
            }
            return null;
        }

        // 自身数据级角色：developer, external_collaborator, guest
        if (isSelfScopeRole(role)) {
            Long userId = user.getUserId();
            if (userId != null) {
                // 优先使用 created_by，其次 assigned_to
                if (sql.contains("created_by")) {
                    return appendCondition(sql, "created_by = " + userId);
                } else if (sql.contains("assigned_to")) {
                    return appendCondition(sql, "assigned_to = " + userId);
                } else if (sql.contains("author_id")) {
                    return appendCondition(sql, "author_id = " + userId);
                } else if (sql.contains("user_id")) {
                    return appendCondition(sql, "user_id = " + userId);
                }
            }
            return null;
        }

        // 未知角色，不追加条件（安全起见，可以记录警告）
        log.warn("Unknown role for data permission: {}", role);
        return null;
    }

    /**
     * 判断是否为项目级角色
     */
    private boolean isProjectScopeRole(String role) {
        return "project_admin".equals(role) || "PROJECT_ADMIN".equals(role)
                || "project_manager".equals(role) || "PROJECT_MANAGER".equals(role)
                || "promotion_manager".equals(role) || "PROMOTION_MANAGER".equals(role)
                || "product_manager".equals(role) || "PRODUCT_MANAGER".equals(role)
                || "tester".equals(role) || "TESTER".equals(role);
    }

    /**
     * 判断是否为自身数据级角色
     */
    private boolean isSelfScopeRole(String role) {
        return "developer".equals(role) || "DEVELOPER".equals(role)
                || "external_collaborator".equals(role) || "EXTERNAL_COLLABORATOR".equals(role)
                || "guest".equals(role) || "GUEST".equals(role);
    }

    /**
     * 在 SQL 中追加条件
     * 使用正则定位插入点，确保条件追加在 ORDER BY / LIMIT 之前
     */
    private String appendCondition(String sql, String condition) {
        // 查找 WHERE 子句位置（排除子查询中的 WHERE）
        int whereIdx = findMainWhereIndex(sql);
        if (whereIdx >= 0) {
            // 已有 WHERE，追加 AND
            int insertPos = findAndInsertPos(sql, whereIdx);
            return sql.substring(0, insertPos) + " AND " + condition + sql.substring(insertPos);
        } else {
            // 无 WHERE，在主表后追加 WHERE
            Matcher matcher = INSERTION_POINT.matcher(sql);
            if (matcher.find()) {
                int insertPos = matcher.start();
                return sql.substring(0, insertPos) + " WHERE " + condition + sql.substring(insertPos);
            }
            return sql + " WHERE " + condition;
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
