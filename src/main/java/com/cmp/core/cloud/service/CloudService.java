package com.cmp.core.cloud.service;

import com.cmp.core.cloud.entity.CloudEntity;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CloudService {

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

}
