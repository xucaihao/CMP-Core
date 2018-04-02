package com.cmp.core.cloud.dao;

import com.cmp.core.cloud.entity.CloudAdapterEntity;
import com.cmp.core.cloud.entity.CloudEntity;
import com.cmp.core.cloud.entity.CloudTypeEntity;

import java.util.List;

public interface CloudDao {

    /**
     * 获取所有云实体
     *
     * @return 云实体列表
     */
    List<CloudEntity> describeClouds();

    /**
     * 通过id查询指定云实体
     *
     * @param cloudId 云id
     * @return 云实体
     */
    CloudEntity describeCloudById(String cloudId);

    /**
     * 通过name查询指定云实体
     *
     * @param cloudName 云名称
     * @return 云实体
     */
    CloudEntity describeCloudByName(String cloudName);

    /**
     * 删除云
     *
     * @param cloudId 云id
     */
    void deleteCloud(String cloudId);

    /**
     * 添加云
     *
     * @param cloud 云
     */
    void addCloud(CloudEntity cloud);

    /**
     * 更新云
     *
     * @param cloud 云
     */
    void updateCloud(CloudEntity cloud);

    /**
     * 获取所有云类型
     *
     * @return 云类型列表
     */
    List<CloudTypeEntity> describeCloudTypes();

    /**
     * 获取所有云适配组件
     *
     * @return 云适配组件列表
     */
    List<CloudAdapterEntity> describeCloudAdapters();

    /**
     * 根据云类型查询云适配组件
     *
     * @param cloudType 云类型
     * @return 云适配组件
     */
    CloudAdapterEntity describeCloudAdapterByCloudType(String cloudType);
}
