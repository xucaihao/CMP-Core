package com.cmp.core.cloud.model.req;

public class ReqModCloudAdapter {

    private String cloudType;

    private String adapterIp;

    private String adapterPort;

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public String getAdapterIp() {
        return adapterIp;
    }

    public void setAdapterIp(String adapterIp) {
        this.adapterIp = adapterIp;
    }

    public String getAdapterPort() {
        return adapterPort;
    }

    public void setAdapterPort(String adapterPort) {
        this.adapterPort = adapterPort;
    }
}
