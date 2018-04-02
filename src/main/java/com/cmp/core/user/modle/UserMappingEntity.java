package com.cmp.core.user.modle;

import java.sql.Timestamp;

/**
 * 用户映射实体
 *
 * @author xuhao
 */
public class UserMappingEntity {

    private String id;

    private String userName;

    private String cmpUserId;

    private String cloudUserId;

    private String cloudId;

    /**
     * 存储用户底层云用户信息（用户名，密码）
     */
    private String authInfo;

    private Timestamp createTime;

    private Timestamp updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCmpUserId() {
        return cmpUserId;
    }

    public void setCmpUserId(String cmpUserId) {
        this.cmpUserId = cmpUserId;
    }

    public String getCloudUserId() {
        return cloudUserId;
    }

    public void setCloudUserId(String cloudUserId) {
        this.cloudUserId = cloudUserId;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
