package com.cmp.core.user.modle.res;

import com.cmp.core.user.modle.UserMappingEntity;

import java.util.List;

public class ResUserMappings {

    private List<UserMappingEntity> userMappings;

    public ResUserMappings() {
    }

    public ResUserMappings(List<UserMappingEntity> userMappings) {
        this.userMappings = userMappings;
    }

    public List<UserMappingEntity> getUserMappings() {
        return userMappings;
    }

    public void setUserMappings(List<UserMappingEntity> userMappings) {
        this.userMappings = userMappings;
    }
}
