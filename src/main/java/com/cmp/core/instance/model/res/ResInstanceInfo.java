package com.cmp.core.instance.model.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResInstanceInfo {

    private String instanceId;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 实例状态
     */
    private String status;

    /**
     * 地域
     */
    private String regionId;

    /**
     * 可用区id
     */
    private String zoneId;

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 到期时间
     */
    private String expiredTime;

    /**
     * 实例机型
     */
    private String instanceType;

    /**
     * 操作系统名称
     */
    @JsonProperty("osname")
    private String osName;

    /**
     * 镜像ID
     */
    private String imageId;

    /**
     * 内存容量，单位：MB。
     */
    private int memory;

    /**
     * CPU核数，单位：核。
     */
    private int cpu;

    /**
     * 计费模式
     */
    private String instanceChargeType;

    /**
     * 网络计费类型
     * PayByTraffic：按流量计费
     * PayByBandwidth：按带宽计费
     */
    private String internetChargeType;

    /**
     * 私网Ip列表
     */
    private List<String> innerIpAddress;

    /**
     * 公网ip列表
     */
    private List<String> publicIpAddress;

    /**
     * 安全组id列表
     */
    private List<String> securityGroupIds;

    private String cloudId;

    private String cloudName;

    private String cloudType;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getInstanceChargeType() {
        return instanceChargeType;
    }

    public void setInstanceChargeType(String instanceChargeType) {
        this.instanceChargeType = instanceChargeType;
    }

    public String getInternetChargeType() {
        return internetChargeType;
    }

    public void setInternetChargeType(String internetChargeType) {
        this.internetChargeType = internetChargeType;
    }

    public List<String> getInnerIpAddress() {
        return innerIpAddress;
    }

    public void setInnerIpAddress(List<String> innerIpAddress) {
        this.innerIpAddress = innerIpAddress;
    }

    public List<String> getPublicIpAddress() {
        return publicIpAddress;
    }

    public void setPublicIpAddress(List<String> publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public List<String> getSecurityGroupIds() {
        return securityGroupIds;
    }

    public void setSecurityGroupIds(List<String> securityGroupIds) {
        this.securityGroupIds = securityGroupIds;
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
