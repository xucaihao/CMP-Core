package com.cmp.core.cloud.model.res;

import com.cmp.core.cloud.model.CloudEntity;

import java.util.List;

public class ResCloudsEntity {

    private List<CloudEntity> clouds;

    public ResCloudsEntity() {
    }

    public ResCloudsEntity(List<CloudEntity> clouds) {
        this.clouds = clouds;
    }

    public List<CloudEntity> getClouds() {
        return clouds;
    }

    public void setClouds(List<CloudEntity> clouds) {
        this.clouds = clouds;
    }
}

