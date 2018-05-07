package com.cmp.core.instance.model.req;

public class ReqRebootInstance {

    private String instanceId;
    private String regionId;
    private boolean forceReboot;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public boolean isForceReboot() {
        return forceReboot;
    }

    public void setForceReboot(boolean forceReboot) {
        this.forceReboot = forceReboot;
    }
}
