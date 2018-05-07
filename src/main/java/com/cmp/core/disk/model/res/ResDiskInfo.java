package com.cmp.core.disk.model.res;

public class ResDiskInfo {

    private String diskId;
    private String diskName;
    private String regionId;
    private String description;

    /**
     * 磁盘类型，
     */
    private String type;

    /**
     * 磁盘种类，
     */
    private String category;

    /**
     * 是否为加密磁盘
     * true: 是加密磁盘
     * false：不是加密磁盘
     */
    private Boolean encrypted;

    /**
     * 磁盘大小，单位 GB
     */
    private int size;

    /**
     * 磁盘状态。
     */
    private String status;

    /**
     * 磁盘挂载的实例 ID
     */
    private String instanceId;

    /**
     * 创建时间
     */
    private String creationTime;
    /**
     * 付费方式
     */
    private String diskChargeType;

    private Boolean portable;

    private String cloudId;

    private String cloudName;

    private String cloudType;

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getDiskChargeType() {
        return diskChargeType;
    }

    public void setDiskChargeType(String diskChargeType) {
        this.diskChargeType = diskChargeType;
    }

    public Boolean getPortable() {
        return portable;
    }

    public void setPortable(Boolean portable) {
        this.portable = portable;
    }

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
}
