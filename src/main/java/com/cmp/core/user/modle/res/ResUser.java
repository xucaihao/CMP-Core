package com.cmp.core.user.modle.res;

import com.cmp.core.user.modle.CmpUser;

public class ResUser {

    private CmpUser user;

    public ResUser() {
    }

    public ResUser(CmpUser user) {
        this.user = user;
    }

    public CmpUser getUser() {
        return user;
    }

    public void setUser(CmpUser user) {
        this.user = user;
    }
}
