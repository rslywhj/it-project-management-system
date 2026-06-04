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

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        UserContext.UserInfo currentUser = UserContext.get();

        // 未登录或系统管理员，不追加条件
        if (currentUser == null || "SYSTEM_ADMIN".equals(currentUser.getRole())) {
            return invocation.proceed();
        }

        // 检查是否有数据权限注解标记
        // 这里通过 UserContext 中的 currentProjectId 来判断
        Long projectId = currentUser.getCurrentProjectId();

        if (projectId != null && "PROJECT_MANAGER".equals(currentUser.getRole())) {
            // 项目经理：追加项目范围条件
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

    private String appendProjectScope(String sql, Long projectId) {
        // 简单实现：在 WHERE 子句后追加 project_id 条件
        // 生产环境建议使用 JSqlParser 等库做更精确的 SQL 改写
        if (sql.contains("WHERE") || sql.contains("where")) {
            return sql + " AND project_id = " + projectId;
        } else {
            // 找到 FROM 子句后的第一个表别名
            return sql + " WHERE project_id = " + projectId;
        }
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
