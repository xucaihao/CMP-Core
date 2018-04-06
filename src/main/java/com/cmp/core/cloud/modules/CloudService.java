package com.cmp.core.cloud.modules;

import com.cmp.core.cloud.model.CloudAdapterEntity;
import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CloudService {

    /**
     * 获取所有可对接云平台类型
     *
     * @return 可对接云平台类型列表
     */
    CompletionStage<List<CloudTypeEntity>> describeCloudTypes();

    /**
     * 更新云平台类型
     *
     * @param cloudType cloudType
     * @return 操作结果
     */
    CompletionStage<Boolean> updateCloudType(CloudTypeEntity cloudType);

    /**
     * 获取所有云实体
     *
     * @return 所有云实体
     */
    CompletionStage<List<CloudEntity>> describeClouds();

    /**
     * 通过id查询指定云实体
     *
     * @param cloudId 云id
     * @return 云实体
     */
    CompletionStage<CloudEntity> describeCloudById(String cloudId);

    /**
     * 通过name查询指定云实体
     *
     * @param cloudName 云名称
     * @return 云实体
     */
    CompletionStage<CloudEntity> describeCloudByName(String cloudName);

    /**
     * 添加云平台
     *
     * @param cloud cloud
     * @return 操作结果
     */
    CompletionStage<Boolean> addCloud(CloudEntity cloud);

    /**
     * 更新云平台
     *
     * @param cloud cloud
     * @return 操作结果
     */
    CompletionStage<Boolean> updateCloud(CloudEntity cloud);

    /**
     * 删除云平台
     *
     * @param cloudId 云平台id
     * @return 操作结果
     */
    CompletionStage<Boolean> deleteCloudById(String cloudId);

    /**
     * 根据云类型查询云适配组件
     *
     * @param cloudType 云类型
     * @return 云适配组件
     */
    CompletionStage<CloudAdapterEntity> describeCloudAdapterByCloudType(String cloudType);

}
