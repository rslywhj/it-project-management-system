package com.pm.common.util;

import lombok.Data;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> CURRENT_USER = new ThreadLocal<>();

    public static void set(UserInfo userInfo) {
        CURRENT_USER.set(userInfo);
    }

    public static UserInfo get() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        UserInfo info = get();
        return info != null ? info.getUserId() : null;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String realName;   // 真实姓名
        private String role;       // super_admin, project_manager, developer, etc.
        private String[] roles;    // 角色列表（兼容前端）
        private String[] permissions; // 权限码列表
        private Long orgId;        // 所属组织ID
        private Long currentProjectId; // 当前操作的项目ID（数据权限用）
    }
}
