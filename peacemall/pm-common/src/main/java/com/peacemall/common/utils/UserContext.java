package com.peacemall.common.utils;

public class UserContext {
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public static class UserInfo {
        private Long userId;
        private String userRole;

        public UserInfo(Long userId, String userRole) {
            this.userId = userId;
            this.userRole = userRole;
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public String getUserRole() {
            return userRole;
        }
    }

    public static void setUser(Long userId, String userRole) {
        tl.set(new UserInfo(userId, userRole));
    }

    public static Long getUserId() {
        UserInfo userInfo = tl.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public static String getUserRole() {
        UserInfo userInfo = tl.get();
        return userInfo != null ? userInfo.getUserRole() : null;
    }

    public static void removeUser() {
        tl.remove();
    }
}
