package com.cmp.core.instance.model.req;

public class ReqModifyInstance {

    private String instanceId;
    private String regionId;

    /**
     * 修改名称
     */
    private String instanceName;

    /**
     * 修改密码（forceStop腾讯特有参数，表示强制关机）
     */
    private String password;
    private boolean forceStop;

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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isForceStop() {
        return forceStop;
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
    }
}
