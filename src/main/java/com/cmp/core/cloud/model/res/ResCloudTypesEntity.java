package com.cmp.core.cloud.model.res;

import com.cmp.core.cloud.model.CloudTypeEntity;

import java.util.List;

public class ResCloudTypesEntity {

    private List<CloudTypeEntity> cloudTypes;

    public ResCloudTypesEntity() {
    }

    public ResCloudTypesEntity(List<CloudTypeEntity> cloudTypes) {
        this.cloudTypes = cloudTypes;
    }

    public List<CloudTypeEntity> getCloudTypes() {
        return cloudTypes;
    }

    public void setCloudTypes(List<CloudTypeEntity> cloudTypes) {
        this.cloudTypes = cloudTypes;
    }
}
