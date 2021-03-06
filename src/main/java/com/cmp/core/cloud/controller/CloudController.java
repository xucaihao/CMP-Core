package com.cmp.core.cloud.controller;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;
import com.cmp.core.cloud.model.req.ReqCreCloud;
import com.cmp.core.cloud.model.req.ReqModCloud;
import com.cmp.core.cloud.model.req.ReqModCloudAdapter;
import com.cmp.core.cloud.model.req.ReqModCloudType;
import com.cmp.core.cloud.model.res.ResCloudAdapters;
import com.cmp.core.cloud.model.res.ResCloudEntity;
import com.cmp.core.cloud.model.res.ResCloudTypesEntity;
import com.cmp.core.cloud.model.res.ResCloudsEntity;
import com.cmp.core.cloud.modules.CloudService;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.common.PingUtil;
import com.cmp.core.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.PRIVATE;
import static com.cmp.core.common.Constance.PUBLIC;
import static com.cmp.core.common.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/clouds")
public class CloudController extends BaseController {

    @Autowired
    private CloudService cloudService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有可对接云平台类型
     *
     * @param response http响应
     * @return 可对接云平台类型列表
     */
    @RequestMapping("/types")
    @ResponseBody
    public CompletionStage<JsonNode> describeCloudTypes(final HttpServletResponse response) {
        return cloudService.describeCloudTypes()
                .thenApply(cloudTypes ->
                        okFormat(OK.value(), new ResCloudTypesEntity(cloudTypes), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 更新云类型（置为可用、不可用）
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException IOException
     */
    @PutMapping("/types")
    @ResponseBody
    public CompletionStage<JsonNode> updateCloudType(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqModCloudType cloudType = JsonUtil.stringToObject(body, ReqModCloudType.class);
        //校验请求体
        return checkModCloudTypeBody(cloudType)
                .thenCombine(cloudService.describeCloudTypes(), (flag, cloudTypes) -> {
                    if (!flag) {
                        throw new CoreException(ERR_MODIFY_CLOUD_TYPE_BODY);
                    }
                    CloudTypeEntity cloudTypeEntity = cloudTypes.stream().filter(vo ->
                            cloudType.getId().equals(vo.getId()))
                            .findAny()
                            .orElseThrow(() -> new CoreException(ERR_CLOUD_TYPE_NOT_FOUND));
                    //请求体转换
                    convertModCloudTypeBody(cloudType, cloudTypeEntity)
                            .thenAccept(resCloudType -> {
                                //更新数据库
                                cloudService.updateCloudType(resCloudType)
                                        .thenAccept(updateFlag -> {
                                            if (!updateFlag) {
                                                throw new CoreException(ERR_UPDATE_CLOUD_TYPE);
                                            }
                                        });
                            });
                    return null;
                }).thenApply(res -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 获取所有已对接云平台
     *
     * @param request  http请求
     * @param response http响应
     * @return 已对接云平台列表
     */
    @RequestMapping("")
    @ResponseBody
    public CompletionStage<JsonNode> describeClouds(
            final HttpServletRequest request,
            final HttpServletResponse response) {
        return getAllCloudEntity(request, false)
                .thenApply(clouds -> okFormat(OK.value(), new ResCloudsEntity(clouds), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 通过id查询指定云平台
     *
     * @param request  http请求
     * @param response http响应
     * @param cloudId  云平台id
     * @return 指定云平台
     */
    @RequestMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<JsonNode> describeCloudAttribute(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable final String cloudId) {
        return getCloudEntity(request, cloudId)
                .thenApply(cloud -> okFormat(OK.value(), new ResCloudEntity(cloud), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 纳管云
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException IOException
     */
    @PostMapping("")
    @ResponseBody
    public CompletionStage<JsonNode> createCloud(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqCreCloud cloud = JsonUtil.stringToObject(body, ReqCreCloud.class);
        //校验请求体
        return checkCreCloudBody(cloud)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_CREATE_CLOUD_BODY);
                    }
                    //验证cloud重名，私有云验证ip相同和网络通
                    return checkCloud(cloud)
                            .thenCombine(convertCreCloudBody(cloud), (cloudFlag, convertCloud) -> {
                                if (cloudFlag) {
                                    //插入数据库记录
                                    return cloudService.addCloud(convertCloud)
                                            .thenCompose(addFlag -> {
                                                if (!addFlag) {
                                                    throw new CoreException(ERR_ADD_CLOUD);
                                                }
                                                return null;
                                            });
                                }
                                return null;
                            });
                }).thenApply(aVoid -> okFormat(CREATED.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 修改云平台
     *
     * @param request  http请求
     * @param response http响应
     * @param cloudId  云id
     * @return 操作结果
     * @throws IOException IOException
     */
    @PutMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<JsonNode> modifyCloudAttribute(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable final String cloudId)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqModCloud cloud = JsonUtil.stringToObject(body, ReqModCloud.class);
        //校验请求体
        return checkModCloudBody(cloud)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_MODIFY_CLOUD_BODY);
                    }
                    return cloudService.describeClouds()
                            .thenCompose(clouds -> {
                                CloudEntity cloudEntity = clouds.stream().filter(vo ->
                                        cloudId.equals(vo.getCloudId()))
                                        .findAny()
                                        .orElseThrow(() -> new CoreException(ERR_CLOUD_NOT_FOUND));
                                //请求体转换
                                return convertModCloudBody(cloud, cloudEntity)
                                        .thenCompose(resCloud -> {
                                            clouds.stream().filter(vo ->
                                                    //校验重名
                                                    resCloud.getCloudName().equals(vo.getCloudName())
                                                            && !resCloud.getCloudId().equals(vo.getCloudId()))
                                                    .findAny()
                                                    .ifPresent(x -> {
                                                        throw new CoreException(ERR_REPEATED_CLOUD_NAME);
                                                    });
                                            //更新数据库
                                            return cloudService.updateCloud(resCloud)
                                                    .thenApply(updateFlag -> {
                                                        if (!updateFlag) {
                                                            throw new CoreException(ERR_UPDATE_CLOUD);
                                                        }
                                                        return null;
                                                    });
                                        });
                            });
                }).thenApply(res -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 删除云
     *
     * @param response http响应
     * @param cloudId  云id
     * @return 操作结果
     */
    @DeleteMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<JsonNode> deleteCloud(
            final HttpServletResponse response,
            @PathVariable final String cloudId) {
        return cloudService.describeCloudById(cloudId)
                .thenCompose(cloud ->
                        //删除云、删除用户映射关系
                        cloudService.deleteCloudById(cloudId)
                                .thenCombine(userService.delUserMappingsByCloudId(cloudId),
                                        (cloudFlag, mappingFlag) -> {
                                            if (!cloudFlag) {
                                                throw new CoreException(ERR_DELETE_CLOUD);
                                            }
                                            if (!mappingFlag) {
                                                throw new CoreException(ERR_DELETE_USER_MAPPING);
                                            }
                                            return null;
                                        })
                ).thenApply(res -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 查询云适配组件列表
     *
     * @param response http响应
     * @return 云适配组件列表
     */
    @RequestMapping("/adapters")
    @ResponseBody
    public CompletionStage<JsonNode> describeCloudAdapters(final HttpServletResponse response) {
        return cloudService.describeCloudAdapters()
                .thenApply(cloudAdapters -> okFormat(OK.value(), new ResCloudAdapters(cloudAdapters), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 更新适配组件路由地址
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     */
    @PutMapping("/adapters/update")
    @ResponseBody
    public CompletionStage<JsonNode> updateCloudAdapter(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqModCloudAdapter reqBody = JsonUtil.stringToObject(body, ReqModCloudAdapter.class);
        //校验请求体
        return checkModCloudAdapterBody(reqBody)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_MODIFY_CLOUD_ADAPTER_BODY);
                    }
                    //判断适配组件
                    return checkCloudAdapter(reqBody)
                            .thenCompose(cloudAdapters ->
                                    //更新数据库
                                    cloudService.updateCloudAdapter(reqBody)
                                            .thenApply(updateFlag -> {
                                                if (!updateFlag) {
                                                    throw new CoreException(ERR_UPDATE_CLOUD_ADAPTER);
                                                }
                                                return null;
                                            })
                            );
                }).thenApply(res -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    private CompletionStage<Boolean> checkCreCloudBody(ReqCreCloud cloud) {
        return CompletableFuture.supplyAsync(() -> {
                    if (null != cloud.getVisibility()) {
                        switch (cloud.getVisibility()) {
                            case PUBLIC:
                                if (null != cloud.getCloudName()) {
                                    return true;
                                }
                                break;
                            case PRIVATE:
                                if (null != cloud.getCloudName()
                                        && null != cloud.getCloudIp()
                                        && null != cloud.getCloudPort()) {
                                    return true;
                                }
                                break;
                        }
                    }
                    return false;
                }
        );
    }

    private CompletionStage<Boolean> checkModCloudBody(ReqModCloud cloud) {
        return CompletableFuture.supplyAsync(() ->
                (null != cloud.getCloudName()
                        && null != cloud.getDescription())
        );
    }

    private CompletionStage<Boolean> checkModCloudTypeBody(ReqModCloudType cloudType) {
        return CompletableFuture.supplyAsync(() ->
                (null != cloudType.getId())
        );
    }

    private CompletionStage<Boolean> checkModCloudAdapterBody(ReqModCloudAdapter reqBody) {
        return CompletableFuture.supplyAsync(() ->
                (null != reqBody.getCloudType()
                        && null != reqBody.getAdapterIp()
                        && null != reqBody.getAdapterPort())
        );
    }

    private CompletionStage<CloudEntity> convertCreCloudBody(ReqCreCloud reqCloud) {
        return CompletableFuture.supplyAsync(() -> {
            CloudEntity resCloud = new CloudEntity();
            resCloud.setCloudId(UUID.randomUUID().toString());
            resCloud.setCloudName(reqCloud.getCloudName());
            resCloud.setCloudType(reqCloud.getCloudType());
            resCloud.setVisibility(reqCloud.getVisibility());
            resCloud.setDescription("");
            if (null != reqCloud.getDescription()) {
                resCloud.setDescription(reqCloud.getDescription());
            }
            resCloud.setStatus("active");
            switch (reqCloud.getVisibility()) {
                case PUBLIC:
                    resCloud.setCloudProtocol("default");
                    resCloud.setCloudIp("default");
                    resCloud.setCloudPort("default");
                    break;
                case PRIVATE:
                    resCloud.setCloudProtocol("http");
                    resCloud.setCloudIp(reqCloud.getCloudIp());
                    resCloud.setCloudPort(reqCloud.getCloudPort());
                    break;
            }
            return resCloud;
        });
    }

    private CompletionStage<CloudEntity> convertModCloudBody(ReqModCloud reqCloud, CloudEntity cloud) {
        return CompletableFuture.supplyAsync(() -> {
            CloudEntity resCloud = new CloudEntity();
            resCloud.setCloudName(reqCloud.getCloudName());
            resCloud.setDescription(reqCloud.getDescription());
            resCloud.setCloudType(cloud.getCloudType());
            resCloud.setCloudId(cloud.getCloudId());
            resCloud.setVisibility(cloud.getVisibility());
            resCloud.setStatus(cloud.getStatus());
            resCloud.setCloudProtocol(cloud.getCloudProtocol());
            resCloud.setCloudIp(cloud.getCloudIp());
            resCloud.setCloudPort(cloud.getCloudPort());
            return resCloud;
        });
    }

    private CompletionStage<CloudTypeEntity> convertModCloudTypeBody(
            ReqModCloudType reqCloudType, CloudTypeEntity cloudType) {
        return CompletableFuture.supplyAsync(() -> {
            CloudTypeEntity resCloudType = new CloudTypeEntity();
            resCloudType.setDisable(reqCloudType.isDisable());
            resCloudType.setId(cloudType.getId());
            resCloudType.setTypeName(cloudType.getTypeName());
            resCloudType.setTypeValue(cloudType.getTypeValue());
            return resCloudType;
        });
    }

    private CompletionStage<Boolean> checkCloud(ReqCreCloud cloud) {
        //验证cloud重名，私有云验证ip相同和网络通
        return cloudService.describeClouds()
                .thenApply(clouds -> {
                    clouds.stream().filter(vo ->
                            cloud.getCloudName().equals(vo.getCloudName()))
                            .findAny()
                            .ifPresent(x -> {
                                throw new CoreException(ERR_REPEATED_CLOUD_NAME);
                            });
                    if (PRIVATE.equals(cloud.getVisibility())) {
                        clouds.stream().filter(vo ->
                                cloud.getCloudIp().equals(vo.getCloudIp()))
                                .findAny()
                                .ifPresent(x -> {
                                    throw new CoreException(ERR_REPEATED_CLOUD_IP);
                                });
                        if (!PingUtil.ping(cloud.getCloudIp(), Integer.valueOf(cloud.getCloudPort()))) {
                            throw new CoreException(ERR_CLOUD_CONNECT_FAIL);
                        }
                    }
                    return true;
                });
    }

    private CompletionStage<Boolean> checkCloudAdapter(ReqModCloudAdapter adapter) {
        return cloudService.describeCloudAdapters()
                .thenApply(adapters -> {
                    //判断适配组件是否存在
                    adapters.stream().filter(vo ->
                            adapter.getCloudType().equals(vo.getCloudType()))
                            .findAny()
                            .orElseThrow(() -> new CoreException(ERR_CLOUD_ADAPTER_NOT_FOUND));
                    //判断适配组件路由地址是否被占用
                    adapters.stream().filter(vo ->
                            !adapter.getCloudType().equals(vo.getCloudType())
                                    && adapter.getAdapterIp().equals(vo.getAdapterIp())
                                    && adapter.getAdapterPort().equals(vo.getAdapterPort()))
                            .findAny()
                            .ifPresent(x -> {
                                throw new CoreException(ERR_REPEATED_CLOUD_ADAPTER_IP);
                            });
                    //判断路由地址是否能够连接
                    if (!PingUtil.ping(adapter.getAdapterIp(), Integer.valueOf(adapter.getAdapterPort()))) {
                        throw new CoreException(ERR_CLOUD_ADAPTER_CONNECT_FAIL);
                    }
                    return true;
                });
    }

}
