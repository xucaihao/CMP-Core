package com.cmp.core.user.modle;

public enum Role {

    /**
     * 管理员
     */
    MANAGER("manager"),

    /**
     * 用户
     */
    USER("user");

    private String role;

    private Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
