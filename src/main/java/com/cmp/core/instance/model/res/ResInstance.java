package com.cmp.core.instance.model.res;

public class ResInstance {

    private ResInstanceInfo instance;

    public ResInstance() {
    }

    public ResInstance(ResInstanceInfo instance) {
        this.instance = instance;
    }

    public ResInstanceInfo getInstance() {
        return instance;
    }

    public void setInstance(ResInstanceInfo instance) {
        this.instance = instance;
    }
}
