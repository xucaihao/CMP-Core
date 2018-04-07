package com.cmp.core.cloud.modules;

import com.cmp.core.cloud.dao.CloudDao;
import com.cmp.core.cloud.model.CloudAdapterEntity;
import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.ErrorEnum.ERR_CLOUD_ADAPTER_NOT_FOUND;
import static com.cmp.core.common.ErrorEnum.ERR_CLOUD_NOT_FOUND;

@Service
public class CloudServiceImpl implements CloudService {

    private static final Logger logger = LoggerFactory.getLogger(CloudServiceImpl.class);

    @Resource
    private CloudDao cloudDao;

    /**
     * 获取所有可对接云平台类型
     *
     * @return 可对接云平台类型列表
     */
    @Override
    public CompletionStage<List<CloudTypeEntity>> describeCloudTypes() {
        return CompletableFuture.supplyAsync(() ->
                cloudDao.describeCloudTypes());
    }

    /**
     * 更新云平台类型
     *
     * @param cloudType cloudType
     * @return 操作结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public CompletionStage<Boolean> updateCloudType(CloudTypeEntity cloudType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                cloudDao.updateCloudType(JsonUtil.stringToObject(JsonUtil.objectToString(cloudType), Map.class));
                return true;
            } catch (Exception e) {
                logger.error("updateCloudType in sql error: {}", e);
                return false;
            }
        });
    }

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

    /**
     * 添加云平台
     *
     * @param cloud cloud
     * @return 操作结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public CompletionStage<Boolean> addCloud(CloudEntity cloud) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                cloudDao.addCloud(JsonUtil.stringToObject(JsonUtil.objectToString(cloud), Map.class));
                return true;
            } catch (Exception e) {
                logger.error("addCloud in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 更新云平台
     *
     * @param cloud cloud
     * @return 操作结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public CompletionStage<Boolean> updateCloud(CloudEntity cloud) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                cloudDao.updateCloud(JsonUtil.stringToObject(JsonUtil.objectToString(cloud), Map.class));
                return true;
            } catch (Exception e) {
                logger.error("updateCloud in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 删除云平台
     *
     * @param cloudId 云平台id
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> deleteCloudById(String cloudId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                cloudDao.deleteCloudById(cloudId);
                return true;
            } catch (Exception e) {
                logger.error("deleteCloudById from sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 根据云类型查询云适配组件
     *
     * @param cloudType 云类型
     * @return 云适配组件
     */
    @Override
    public CompletionStage<CloudAdapterEntity> describeCloudAdapterByCloudType(String cloudType) {
        return CompletableFuture.supplyAsync(() ->
                Optional.ofNullable(cloudDao.describeCloudAdapterByCloudType(cloudType))
                        .orElseThrow(() -> new CoreException(ERR_CLOUD_ADAPTER_NOT_FOUND)));
    }
}
