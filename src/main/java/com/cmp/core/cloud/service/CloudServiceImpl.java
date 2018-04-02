package com.cmp.core.cloud.service;

import com.cmp.core.cloud.dao.CloudDao;
import com.cmp.core.cloud.entity.CloudEntity;
import com.cmp.core.common.CoreException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.ErrorEnum.ERR_CLOUD_NOT_FOUND;

@Service
public class CloudServiceImpl implements CloudService {

    @Resource
    private CloudDao cloudDao;

    /**
     * 获取所有云实体
     *
     * @return 所有云实体
     */
    @Override
    public CompletionStage<List<CloudEntity>> describeClouds() {
        return CompletableFuture.supplyAsync(() ->
                cloudDao.describeClouds());
    }

    /**
     * 通过id查询指定云实体
     *
     * @param cloudId 云id
     * @return 云实体
     */
    @Override
    public CompletionStage<CloudEntity> describeCloudById(String cloudId) {
        return CompletableFuture.supplyAsync(() ->
                Optional.ofNullable(cloudDao.describeCloudById(cloudId))
                        .orElseThrow(() -> new CoreException(ERR_CLOUD_NOT_FOUND)));
    }

    /**
     * 通过name查询指定云实体
     *
     * @param cloudName 云名称
     * @return 云实体
     */
    @Override
    public CompletionStage<CloudEntity> describeCloudByName(String cloudName) {
        return CompletableFuture.supplyAsync(() ->
                Optional.ofNullable(cloudDao.describeCloudByName(cloudName))
                        .orElseThrow(() -> new CoreException(ERR_CLOUD_NOT_FOUND)));
    }


}
