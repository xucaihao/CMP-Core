package com.cmp.core.common;

import com.cmp.core.cloud.entity.CloudEntity;
import com.cmp.core.cloud.service.CloudService;
import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.UserMappingEntity;
import com.cmp.core.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.cmp.core.common.Constance.*;
import static com.cmp.core.common.ErrorEnum.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class BaseHelperImpl implements BaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(BaseHelperImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CloudService cloudService;

    /**
     * 获取用户token
     */
    private Function<HttpServletRequest, CompletionStage<String>> getToken = httpServletRequest ->
            CompletableFuture.supplyAsync(() -> Optional.ofNullable(httpServletRequest.getHeader(X_AUTH_TOKEN))
                    .orElseThrow(() -> new CoreException(ERR_SYS_TOKEN_NOT_FOUND)));

    /**
     * 聚合异步返回结果
     *
     * @param future future
     * @param <T>    泛型
     * @return 泛型
     */
    private <T> T getRes(CompletionStage<T> future) {
        try {
            if (null != future) {
                return future.toCompletableFuture().get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("BaseHelperImpl::getRes error..e :{}", e);
        }
        return null;
    }

    /**
     * 聚合多线程list
     *
     * @param futures 多线程返回结果
     * @return 聚合后列表
     */
    @Override
    public <T> List<T> joinRes(List<CompletionStage<T>> futures) {
        return futures.stream()
                .map(vo -> vo.exceptionally(e -> null))
                .map(this::getRes)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    /**
     * 合并多个List为一个List
     *
     * @param lists 多个List
     * @return 合并后list
     */
    @Override
    public <T> List<T> aggregateList(List<List<T>> lists) {
        int count = lists.stream().mapToInt(List::size).sum();
        final List<T> arrays = new ArrayList<>(count);
        lists.forEach(arrays::addAll);
        return arrays;
    }

    /**
     * 组装url参数
     *
     * @param api   api
     * @param cloud 云
     * @return 组装后url
     */
    @Override
    public CompletionStage<String> formatUrl(String api, CloudEntity cloud) {
        return null;
    }

    /**
     * 绑定转发路由
     *
     * @param cloud 云
     * @return String
     */
    @Override
    public CompletionStage<String> bindAdapter(CloudEntity cloud) {
        return null;
    }

    /**
     * 查询cmp用户
     *
     * @param request 请求
     * @return cmp用户
     */
    @Override
    public CompletionStage<CmpUser> getCmpUserEntity(HttpServletRequest request) {
        return getToken.apply(request)
                .thenApply(token ->
                        userService.describeCmpUsers()
                                .stream()
                                .filter(vo -> token.equals(vo.getToken()))
                                .findAny()
                                .orElseThrow(() -> new CoreException(ERR_USER_TOKEN_EXPIRED))
                );
    }

    /**
     * 获取云实体
     *
     * @param request 请求
     * @return 云实体
     */
    @Override
    public CompletionStage<CloudEntity> getCloudEntity(HttpServletRequest request) {
        String cloudId = Optional.ofNullable(request.getHeader(HEADER_CLOUD_ID))
                .orElseThrow(() -> new CoreException(ERR_CLOUD_ID_NOT_FOUND));
        return cloudService.describeCloudById(cloudId)
                .thenCombine(getCmpUserEntity(request), (cloud, cmpUser) -> {
                    UserMappingEntity userMappingEntity = userService.describeUserMapping(cmpUser.getUserId(), cloudId);
                    cloud.setAuthInfo(userMappingEntity.getAuthInfo());
                    return cloud;
                });
    }

    /**
     * 获取所有云实体
     *
     * @param request 请求
     * @return 所有云实体
     */
    @Override
    public CompletionStage<List<CloudEntity>> getAllCloudEntity(HttpServletRequest request) {
        return getCmpUserEntity(request)
                .thenCombine(cloudService.describeClouds(), (cmpUser, clouds) -> {
                            String cmpUserId = cmpUser.getUserId();
                            //将用户映射关系以cloudId作为key存入Map中
                            Map<String, UserMappingEntity> userMappingMap =
                                    userService.describeUserMappings(cmpUserId)
                                            .stream()
                                            .collect(toMap(UserMappingEntity::getCloudId, Function.identity()));
                            //筛选出请求用户存在映射关系的云列表
                            return clouds.stream().filter(cloud ->
                                    userMappingMap.containsKey(cloud.getCloudId()))
                                    .peek(cloud -> cloud.setAuthInfo(userMappingMap.get(cloud.getCloudId()).getAuthInfo()))
                                    .collect(toList());
                        }
                );
    }

    /**
     * 获取cloud用户id
     *
     * @param cmpUserId cmp用户id
     * @param cloud     云
     * @return cloud用户id
     */
    @Override
    public String getCloudUserId(String cmpUserId, CloudEntity cloud) {
        return userService.describeUserMapping(cmpUserId, cloud.getCloudId())
                .getCloudUserId();
    }
}
