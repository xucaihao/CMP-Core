package com.cmp.core.cloud.model.res;

import com.cmp.core.cloud.model.CloudEntity;

public class ResCloudEntity {

    private CloudEntity cloud;

    public ResCloudEntity() {
    }

    public ResCloudEntity(CloudEntity cloud) {
        this.cloud = cloud;
    }

    public CloudEntity getCloud() {
        return cloud;
    }

    public void setCloud(CloudEntity cloud) {
        this.cloud = cloud;
    }
}
