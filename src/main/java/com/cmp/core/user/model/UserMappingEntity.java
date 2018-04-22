package com.cmp.core.user.model;

/**
 * 用户映射实体
 *
 * @author xuhao
 */
public class UserMappingEntity {

    private String id;

    private String cmpUserName;

    private String cmpUserId;

    private String accessKey;

    private String cloudId;

    private String cloudName;

    /**
     * 存储用户底层云用户信息（accessKey，secret）
     */
    private String authInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCmpUserId() {
        return cmpUserId;
    }

    public void setCmpUserId(String cmpUserId) {
        this.cmpUserId = cmpUserId;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }
}
