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
        private String role;       // SYSTEM_ADMIN, PROJECT_MANAGER, MEMBER, etc.
        private Long orgId;        // 所属组织ID
        private Long currentProjectId; // 当前操作的项目ID（数据权限用）
    }
}
