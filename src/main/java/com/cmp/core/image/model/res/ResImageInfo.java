package com.cmp.core.image.model.res;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResImageInfo {

    private String imageId;

    private String imageName;

    private String regionId;

    private String creationTime;

    /**
     * 操作系统平台
     */
    private String platform;

    /**
     * 镜像系统类型
     */
    private String architecture;

    /**
     * 镜像大小
     */
    private int size;

    /**
     * 操作系统的显示名称
     */
    @JsonProperty("oSName")
    private String osName;

    @JsonProperty("isSupportCloudinit")
    private boolean isSupportCloudInit;

    /**
     * 镜像类型
     */
    private String imageOwnerAlias;

    private String description;

    /**
     * 镜像的状态
     */
    private String status;

    private String cloudId;

    private String cloudName;

    private String cloudType;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public boolean isSupportCloudInit() {
        return isSupportCloudInit;
    }

    public void setSupportCloudInit(boolean supportCloudInit) {
        isSupportCloudInit = supportCloudInit;
    }

    public String getImageOwnerAlias() {
        return imageOwnerAlias;
    }

    public void setImageOwnerAlias(String imageOwnerAlias) {
        this.imageOwnerAlias = imageOwnerAlias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
