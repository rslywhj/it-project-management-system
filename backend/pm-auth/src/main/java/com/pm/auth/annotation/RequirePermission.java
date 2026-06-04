package com.pm.auth.annotation;

import java.lang.annotation.*;

/**
 * 功能权限注解
 * 用法: @RequirePermission("project:create")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 权限标识，格式：模块:操作
     * 如 project:create, requirement:update, task:delete
     */
    String value();

    /**
     * 是否检查数据权限（默认不检查）
     */
    boolean dataScope() default false;
}
