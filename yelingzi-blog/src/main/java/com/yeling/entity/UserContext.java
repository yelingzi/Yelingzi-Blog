package com.yeling.entity;

import com.yeling.user.domian.entity.User;

public class UserContext {
    private static final ThreadLocal<User> USER_INFO = new ThreadLocal<>();

    public static void setUser(User user) {
        USER_INFO.set(user);
    }

    public static User getUser() {
        return USER_INFO.get();
    }

    public static void clear() {
        USER_INFO.remove();
    }
}
