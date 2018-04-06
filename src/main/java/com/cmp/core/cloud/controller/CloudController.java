package com.cmp.core.cloud.controller;

import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.cloud.model.CloudTypeEntity;
import com.cmp.core.cloud.model.req.ReqCreCloud;
import com.cmp.core.cloud.model.req.ReqModCloud;
import com.cmp.core.cloud.model.req.ReqModCloudType;
import com.cmp.core.cloud.model.res.ResCloudEntity;
import com.cmp.core.cloud.model.res.ResCloudTypesEntity;
import com.cmp.core.cloud.model.res.ResCloudsEntity;
import com.cmp.core.cloud.modules.CloudService;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.PingUtil;
import com.cmp.core.common.ResData;
import com.cmp.core.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * @param request http请求
     * @return 可对接云平台类型列表
     */
    @RequestMapping("/types")
    @ResponseBody
    public CompletionStage<ResData> describeCloudTypes(final HttpServletRequest request) {
        return cloudService.describeCloudTypes()
                .thenApply(cloudTypes ->
                        ResData.build(OK.value(), new ResCloudTypesEntity(cloudTypes), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    @PutMapping("/types")
    @ResponseBody
    public CompletionStage<ResData> updateCloudType(final HttpServletRequest request, ReqModCloudType cloudType) {
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
                }).thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 获取所有已对接云平台
     *
     * @param request http请求
     * @return 已对接云平台列表
     */
    @RequestMapping("")
    @ResponseBody
    public CompletionStage<ResData> describeClouds(final HttpServletRequest request) {
        return getAllCloudEntity(request)
                .thenApply(clouds ->
                        ResData.build(OK.value(), new ResCloudsEntity(clouds), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 通过id查询指定云平台
     *
     * @param request http请求
     * @param cloudId 云平台id
     * @return 指定云平台
     */
    @RequestMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<ResData> describeCloudAttribute(
            final HttpServletRequest request, @PathVariable final String cloudId) {
        return getCloudEntity(request, cloudId)
                .thenApply(cloud ->
                        ResData.build(OK.value(), new ResCloudEntity(cloud), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    @PostMapping("")
    @ResponseBody
    public CompletionStage<ResData> createCloud(final HttpServletRequest request, ReqCreCloud cloud) {
        //校验请求体
        return checkCreCloudBody(cloud)
                .thenAccept(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_CREATE_CLOUD_BODY);
                    }
                    //验证cloud重名，私有云验证ip相同和网络通
                    checkCloud(cloud)
                            .thenCombine(convertCreCloudBody(cloud), (cloudFlag, convertCloud) -> {
                                if (cloudFlag) {
                                    //插入数据库记录
                                    cloudService.addCloud(convertCloud)
                                            .thenAccept(addFlag -> {
                                                if (!addFlag) {
                                                    throw new CoreException(ERR_ADD_CLOUD);
                                                }
                                            });
                                }
                                return null;
                            });
                }).thenApply(aVoid -> ResData.build(CREATED.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    @PutMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<ResData> modifyCloudAttribute(
            final HttpServletRequest request, @PathVariable final String cloudId, ReqModCloud cloud) {
        //校验请求体
        return checkModCloudBody(cloud)
                .thenCombine(cloudService.describeClouds(), (flag, clouds) -> {
                    if (!flag) {
                        throw new CoreException(ERR_MODIFY_CLOUD_BODY);
                    }
                    CloudEntity cloudEntity = clouds.stream().filter(vo ->
                            cloudId.equals(vo.getCloudId()))
                            .findAny()
                            .orElseThrow(() -> new CoreException(ERR_CLOUD_NOT_FOUND));
                    //请求体转换
                    convertModCloudBody(cloud, cloudEntity)
                            .thenAccept(resCloud -> {
                                clouds.stream().filter(vo ->
                                        //校验重名
                                        resCloud.getCloudName().equals(vo.getCloudName())
                                                && !resCloud.getCloudId().equals(vo.getCloudId()))
                                        .findAny()
                                        .ifPresent(x -> {
                                            throw new CoreException(ERR_REPEATED_CLOUD_NAME);
                                        });
                                //更新数据库
                                cloudService.updateCloud(resCloud)
                                        .thenAccept(updateFlag -> {
                                            if (!updateFlag) {
                                                throw new CoreException(ERR_UPDATE_CLOUD);
                                            }
                                        });
                            });
                    return null;
                }).thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    @DeleteMapping("/{cloudId}")
    @ResponseBody
    public CompletionStage<ResData> deleteCloud(
            final HttpServletRequest request, @PathVariable final String cloudId) {
        return cloudService.describeCloudById(cloudId)
                .thenAccept(cloud ->
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
                ).thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    private CompletionStage<Boolean> checkCreCloudBody(ReqCreCloud cloud) {
        return CompletableFuture.supplyAsync(() -> {
                    if (null != cloud.getVisibility()) {
                        switch (cloud.getVisibility()) {
                            case PUBLIC:
                                if (null != cloud.getCloudName()
                                        && null != cloud.getDescription()) {
                                    return true;
                                }
                                break;
                            case PRIVATE:
                                if (null != cloud.getCloudName()
                                        && null != cloud.getDescription()
                                        && null != cloud.getCloudProtocol()
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

    private CompletionStage<CloudEntity> convertCreCloudBody(ReqCreCloud reqCloud) {
        return CompletableFuture.supplyAsync(() -> {
            CloudEntity resCloud = new CloudEntity();
            resCloud.setCloudId(UUID.randomUUID().toString());
            resCloud.setCloudName(reqCloud.getCloudName());
            resCloud.setCloudType(reqCloud.getCloudType());
            resCloud.setVisibility(reqCloud.getVisibility());
            resCloud.setDescription(reqCloud.getDescription());
            resCloud.setStatus("active");
            switch (reqCloud.getVisibility()) {
                case PUBLIC:
                    break;
                case PRIVATE:
                    resCloud.setCloudProtocol(reqCloud.getCloudProtocol());
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

}
