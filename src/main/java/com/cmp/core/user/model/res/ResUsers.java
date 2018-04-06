package com.cmp.core.user.model.res;

import com.cmp.core.user.model.CmpUser;

import java.util.List;

public class ResUsers {

    private List<CmpUser> users;

    public ResUsers() {
    }

    public ResUsers(List<CmpUser> users) {
        this.users = users;
    }

    public List<CmpUser> getUsers() {
        return users;
    }

    public void setUsers(List<CmpUser> users) {
        this.users = users;
    }
}
