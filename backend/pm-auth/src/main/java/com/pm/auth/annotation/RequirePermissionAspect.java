package com.pm.auth.annotation;

import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限校验切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequirePermissionAspect {

    private final SysPermissionMapper sysPermissionMapper;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String requiredPermission = requirePermission.value();

        // 从数据库查询用户的权限列表
        List<String> userPermissions = sysPermissionMapper.selectPermissionCodesByUserId(currentUser.getUserId());

        // 检查是否有通配符权限（如 * 或 requirement:*）
        boolean hasPermission = userPermissions.contains("*")
                || userPermissions.contains(requiredPermission)
                || userPermissions.contains(requiredPermission.split(":")[0] + ":*");

        if (!hasPermission) {
            log.warn("User {} with role {} denied permission: {}", currentUser.getUsername(), currentUser.getRole(), requiredPermission);
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return joinPoint.proceed();
    }
}
