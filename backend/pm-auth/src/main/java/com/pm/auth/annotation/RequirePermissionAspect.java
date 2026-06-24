package com.pm.auth.annotation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.common.exception.BusinessException;
import com.pm.common.mapper.SysPermissionMapper;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 权限校验切面（带 Redis 缓存）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequirePermissionAspect {

    private final SysPermissionMapper sysPermissionMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /** Redis key 前缀：用户权限缓存 */
    private static final String PERMISSION_CACHE_PREFIX = "auth:permissions:";

    /** 缓存 TTL（分钟） */
    private static final long CACHE_TTL_MINUTES = 5;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        UserContext.UserInfo currentUser = UserContext.get();
        if (currentUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String requiredPermission = requirePermission.value();
        Long userId = currentUser.getUserId();

        // 从缓存或数据库获取用户的权限列表
        List<String> userPermissions = getUserPermissions(userId);

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

    /**
     * 获取用户权限列表（优先从 Redis 缓存获取）
     */
    private List<String> getUserPermissions(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;

        try {
            // 尝试从缓存获取
            String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                return objectMapper.readValue(cachedValue, new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to read permissions from cache for user {}: {}", userId, e.getMessage());
        }

        // 缓存未命中，从数据库查询
        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(userId);

        // 写入缓存
        try {
            String jsonValue = objectMapper.writeValueAsString(permissions);
            stringRedisTemplate.opsForValue().set(cacheKey, jsonValue, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Failed to write permissions to cache for user {}: {}", userId, e.getMessage());
        }

        return permissions;
    }

    /**
     * 清除指定用户的权限缓存（用于用户角色变更时调用）
     */
    public void evictUserPermissionCache(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        try {
            stringRedisTemplate.delete(cacheKey);
            log.info("Evicted permission cache for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to evict permission cache for user {}: {}", userId, e.getMessage());
        }
    }
}
