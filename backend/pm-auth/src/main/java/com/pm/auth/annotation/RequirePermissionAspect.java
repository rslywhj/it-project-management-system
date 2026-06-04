package com.pm.auth.annotation;

import com.pm.common.exception.BusinessException;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 权限校验切面
 */
@Slf4j
@Aspect
@Component
public class RequirePermissionAspect {

    /**
     * 角色-权限映射（后续从数据库加载，此处硬编码用于初期开发）
     */
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // 系统管理员：全部权限
        ROLE_PERMISSIONS.put("SYSTEM_ADMIN", Set.of("*"));

        // 项目管理员：项目管理 + 成员管理
        ROLE_PERMISSIONS.put("PROJECT_ADMIN", Set.of(
                "project:create", "project:update", "project:delete",
                "project:member:add", "project:member:update", "project:member:remove",
                "requirement:*", "task:*", "milestone:*"
        ));

        // 项目经理：项目内全部操作
        ROLE_PERMISSIONS.put("PROJECT_MANAGER", Set.of(
                "project:update", "project:member:add", "project:member:update",
                "requirement:*", "task:*", "milestone:*", "delivery:*"
        ));

        // 推广管理员
        ROLE_PERMISSIONS.put("PROMOTION_MANAGER", Set.of(
                "promotion:*", "requirement:view", "task:view"
        ));

        // 产品经理
        ROLE_PERMISSIONS.put("PRODUCT_MANAGER", Set.of(
                "requirement:create", "requirement:update", "requirement:review"
        ));

        // 开发人员
        ROLE_PERMISSIONS.put("DEVELOPER", Set.of(
                "task:update", "task:view", "requirement:view"
        ));

        // 测试人员
        ROLE_PERMISSIONS.put("TESTER", Set.of(
                "task:view", "requirement:view", "testcase:*"
        ));

        // 访客
        ROLE_PERMISSIONS.put("GUEST", Set.of(
                "project:view", "requirement:view", "task:view"
        ));
    }

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String role = currentUser.getRole();
        String requiredPermission = requirePermission.value();

        Set<String> permissions = ROLE_PERMISSIONS.getOrDefault(role, Set.of());

        // 检查是否有通配符权限（如 requirement:* 匹配 requirement:create）
        boolean hasPermission = permissions.contains("*")
                || permissions.contains(requiredPermission)
                || permissions.contains(requiredPermission.split(":")[0] + ":*");

        if (!hasPermission) {
            log.warn("User {} with role {} denied permission: {}", currentUser.getUsername(), role, requiredPermission);
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return joinPoint.proceed();
    }
}
