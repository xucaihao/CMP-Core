package com.cmp.core.user.modle.req;

public class ReqAddMapping {

    private String cmpUserId;

    private String cmpUserName;

    private String accessKey;

    private String secret;

    private String cloudId;

    public String getCmpUserId() {
        return cmpUserId;
    }

    public void setCmpUserId(String cmpUserId) {
        this.cmpUserId = cmpUserId;
    }

    public String getCmpUserName() {
        return cmpUserName;
    }

    public void setCmpUserName(String cmpUserName) {
        this.cmpUserName = cmpUserName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }
}
