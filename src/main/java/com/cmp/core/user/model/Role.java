package com.cmp.core.user.model;

public enum Role {

    /**
     * 管理员
     */
    MANAGER("MANAGER"),

    /**
     * 用户
     */
    USER("USER");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
    }
}
