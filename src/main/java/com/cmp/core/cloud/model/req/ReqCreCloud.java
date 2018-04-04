package com.cmp.core.cloud.model.req;

public class ReqCreCloud {

    private String cloudName;

    private String cloudType;

    private String visibility;

    private String cloudProtocol;

    private String cloudIp;

    private String cloudPort;

    private String description;

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public String getCloudProtocol() {
        return cloudProtocol;
    }

    public void setCloudProtocol(String cloudProtocol) {
        this.cloudProtocol = cloudProtocol;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getCloudIp() {
        return cloudIp;
    }

    public void setCloudIp(String cloudIp) {
        this.cloudIp = cloudIp;
    }

    public String getCloudPort() {
        return cloudPort;
    }

    public void setCloudPort(String cloudPort) {
        this.cloudPort = cloudPort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
