package com.cmp.core.cloud.dao;

import com.cmp.core.cloud.model.CloudAdapterEntity;
import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;

import java.util.List;
import java.util.Map;

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
    void deleteCloudById(String cloudId);

    /**
     * 添加云
     *
     * @param cloud 云
     */
    void addCloud(Map<String, Object> cloud);

    /**
     * 更新云
     *
     * @param cloud 云
     */
    void updateCloud(Map<String, Object> cloud);

    /**
     * 获取所有云类型
     *
     * @return 云类型列表
     */
    List<CloudTypeEntity> describeCloudTypes();

    /**
     * 更新云类型
     *
     * @param cloudType 云类型
     */
    void updateCloudType(Map<String, Object> cloudType);

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

    /**
     * 更新适配组件路由地址
     *
     * @param cloudAdapter 适配组件路由地址
     */
    void updateCloudAdapter(Map<String, Object> cloudAdapter);
}
