package com.cmp.core.cloud.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CloudEntity extends BaseCloudEntity {

    @JsonProperty("cloud_id")
    private String cloudId;

    @JsonProperty("cloud_name")
    private String cloudName;

    @JsonProperty("cloud_type")
    private String cloudType;

    @JsonProperty("cloud_protocol")
    private String cloudProtocol;

    @JsonProperty("cloud_ip")
    private String cloudIp;

    @JsonProperty("cloud_port")
    private String cloudPort;

    private String status;

    private String description = "";

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CloudEntity{" +
                "cloudId='" + cloudId + '\'' +
                ", cloudName='" + cloudName + '\'' +
                ", cloudType='" + cloudType + '\'' +
                ", cloudProtocol='" + cloudProtocol + '\'' +
                ", cloudIp='" + cloudIp + '\'' +
                ", cloudPort='" + cloudPort + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
