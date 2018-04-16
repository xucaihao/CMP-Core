package com.cmp.core.common;

import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;
import com.cmp.core.cloud.modules.CloudService;
import com.cmp.core.user.model.CmpUser;
import com.cmp.core.user.model.UserMappingEntity;
import com.cmp.core.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        return formatUrl(api, cloud, null);
    }

    /**
     * 组装url参数
     *
     * @param api      api
     * @param cloud    云
     * @param paramMap 自定义参数
     * @return 组装后url
     */
    @Override
    public CompletionStage<String> formatUrl(String api, CloudEntity cloud, Map<String, String[]> paramMap) {
        return bindAdapter(cloud).thenApply(address -> {
            if (null == cloud.getRequest()) {
                return address + api;
            }
            final Map<String, String[]> params = (null == paramMap)
                    ? cloud.getRequest().getParameterMap() : paramMap;
            if (!params.isEmpty()) {
                final StringBuffer buffer = new StringBuffer();
                params.forEach((k, v) ->
                        buffer.append("&")
                                .append(k)
                                .append("=")
                                .append(String.join(",", params.get(k)))
                );
                if (buffer.length() > 0) {
                    return address + api + "?"
                            + buffer.toString().substring(1, buffer.toString().length());

                }
            }
            return address + api;
        });
    }

    /**
     * 绑定转发路由
     *
     * @param cloud 云
     * @return String
     */
    @Override
    public CompletionStage<String> bindAdapter(CloudEntity cloud) {
        return cloudService.describeCloudAdapterByCloudType(cloud.getCloudType())
                .thenApply(adapter ->
                        adapter.getAdapterProtocol()
                                + "://" + adapter.getAdapterIp()
                                + ":" + adapter.getAdapterPort()
                );
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
    public CompletionStage<CloudEntity> getCloudEntity(HttpServletRequest request, String cloudId) {
        CompletableFuture<CmpUser> cmpUserFuture = getCmpUserEntity(request).toCompletableFuture();
        CompletableFuture<CloudEntity> cloudFuture = cloudService.describeCloudById(cloudId).toCompletableFuture();
        CompletableFuture<List<CloudTypeEntity>> cloudTypesFuture = cloudService.describeCloudTypes().toCompletableFuture();
        return CompletableFuture.allOf(cmpUserFuture, cloudFuture, cloudTypesFuture)
                .thenApply(x -> {
                    try {
                        CmpUser cmpUser = cmpUserFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
                        CloudEntity cloud = cloudFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
                        cloudTypesFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                                .stream()
                                .filter(cloudType -> cloudType.getTypeValue().equals(cloud.getCloudType())
                                        && cloudType.isDisable())
                                .findAny()
                                .orElseThrow(() -> new CoreException(ERR_CLOUD_TYPE_NOT_FOUND));
                        switch (cmpUser.getRole()) {
                            case USER:
                                UserMappingEntity userMappingEntity =
                                        userService.describeUserMapping(cmpUser.getUserId(), cloudId, CMP);
                                cloud.setAuthInfo(userMappingEntity.getAuthInfo());
                                cloud.setRequest(request);
                                break;
                            case MANAGER:
                                break;
                        }
                        return cloud;
                    } catch (Exception e) {
                        ExceptionUtil.dealException(e);
                        return null;
                    }
                });
    }

    /**
     * 获取所有已对接云平台
     *
     * @param flag    根据用户映射筛选云
     * @param request 请求
     * @return 所有已对接云平台
     */
    @Override
    public CompletionStage<List<CloudEntity>> getAllCloudEntity(HttpServletRequest request, boolean flag) {
        CompletableFuture<CmpUser> cmpUserFuture = getCmpUserEntity(request).toCompletableFuture();
        CompletableFuture<List<CloudEntity>> cloudsFuture = cloudService.describeClouds().toCompletableFuture();
        CompletableFuture<List<CloudTypeEntity>> cloudTypesFuture = cloudService.describeCloudTypes().toCompletableFuture();
        return CompletableFuture.allOf(cmpUserFuture, cloudsFuture, cloudTypesFuture)
                .thenApply(x -> {
                    try {
                        CmpUser cmpUser = cmpUserFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
                        List<CloudEntity> clouds = cloudsFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
                        List<CloudTypeEntity> cloudTypes = cloudTypesFuture.get(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                                .stream()
                                .filter(CloudTypeEntity::isDisable)
                                .collect(toList());
                        if (CollectionUtils.isEmpty(cloudTypes)) {
                            return new ArrayList<CloudEntity>();
                        }
                        //将可对接云平台以类型作为key存入Map中
                        Map<String, CloudTypeEntity> cloudTypeMap = cloudTypes.stream()
                                .collect(toMap(CloudTypeEntity::getTypeValue, Function.identity()));
                        // 根据可对接云平台类型筛选云列表
                        clouds = clouds.stream()
                                .filter(cloud -> cloudTypeMap.containsKey(cloud.getCloudType()))
                                .collect(toList());
                        if (flag) {
                            String cmpUserId = cmpUser.getUserId();
                            //将用户映射关系以cloudId作为key存入Map中
                            Map<String, UserMappingEntity> userMappingMap =
                                    userService.describeUserMappings(cmpUserId)
                                            .stream()
                                            .collect(toMap(UserMappingEntity::getCloudId, Function.identity()));
                            //筛选出请求用户存在映射关系的云列表
                            clouds = clouds.stream()
                                    .filter(cloud -> userMappingMap.containsKey(cloud.getCloudId()))
                                    .peek(cloud -> {
                                        String authInfo = userMappingMap.get(cloud.getCloudId()).getAuthInfo();
                                        cloud.setAuthInfo(authInfo);
                                        cloud.setRequest(request);
                                    }).collect(toList());
                        }
                        return clouds;
                    } catch (Exception e) {
                        ExceptionUtil.dealException(e);
                        return null;
                    }
                });
    }

//    /**
//     * 通过CmpUserId获取cloud用户id
//     *
//     * @param cmpUserId cmp用户id
//     * @param cloud     云
//     * @return cloud用户id
//     */
//    @Override
//    public String getCloudUserId(String cmpUserId, CloudEntity cloud) {
//        return userService.describeUserMapping(cmpUserId, cloud.getCloudId(), CMP)
//                .getCloudUserId();
//    }
//
//    /**
//     * 通过cloudUserId获取cmpUserId
//     *
//     * @param cloudUserId cloudUserId
//     * @param cloud       云
//     * @return cmpUserId
//     */
//    @Override
//    public String getCmpUserId(String cloudUserId, CloudEntity cloud) {
//        return userService.describeUserMapping(cloudUserId, cloud.getCloudId(), CLOUD)
//                .getCmpUserId();
//    }
}
