package com.cmp.core.instance.model.res;

import java.util.List;

public class ResInstances {

    private List<ResInstanceInfo> instances;

    public ResInstances() {
    }

    public ResInstances(List<ResInstanceInfo> instances) {
        this.instances = instances;
    }

    public List<ResInstanceInfo> getInstances() {
        return instances;
    }

    public void setInstances(List<ResInstanceInfo> instances) {
        this.instances = instances;
    }
}
