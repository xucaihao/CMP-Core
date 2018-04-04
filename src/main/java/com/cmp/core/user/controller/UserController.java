package com.cmp.core.user.controller;

import com.cmp.core.cloud.modules.CloudService;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.ResData;
import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.Role;
import com.cmp.core.user.modle.UserMappingEntity;
import com.cmp.core.user.modle.req.ReqAddMapping;
import com.cmp.core.user.modle.req.ReqModMapping;
import com.cmp.core.user.modle.req.ReqUser;
import com.cmp.core.user.modle.res.ResUser;
import com.cmp.core.user.modle.res.ResUserMappings;
import com.cmp.core.user.modle.res.ResUsers;
import com.cmp.core.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.CMP_V1;
import static com.cmp.core.common.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private CloudService cloudService;

    @Autowired
    private UserService userService;

    /**
     * 查询用户列表
     *
     * @param request http请求
     * @return 用户列表
     */
    @RequestMapping("")
    @ResponseBody
    public CompletionStage<ResData> describeUsers(final HttpServletRequest request) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeCmpUsers())
                .thenApply(users ->
                        ResData.build(OK.value(), new ResUsers(users), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 根据id查询指定用户
     *
     * @param request http请求
     * @param userId  用户id
     * @return 指定用户
     */
    @RequestMapping("/{userId}")
    @ResponseBody
    public CompletionStage<ResData> describeUserAttribute(
            final HttpServletRequest request, @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserAttribute(userId))
                .thenApply(user ->
                        ResData.build(OK.value(), new ResUser(user), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 用户注册
     *
     * @param request http请求
     * @param user    请求体
     * @return 操作结果
     */
    @PostMapping("")
    @ResponseBody
    public CompletionStage<ResData> registerUser(
            final HttpServletRequest request, final ReqUser user) {
        //校验请求体
        return checkRegisterUserBody(user).thenAccept(flag ->
                //转换请求体
                buildCmpUser(user, null).thenCompose(resUser ->
                        //查询数据库记录
                        userService.addUser(resUser)).thenAccept(registerFlag -> {
                    if (!registerFlag) {
                        throw new CoreException(ERR_REGISTER_USER);
                    }
                })).thenApply(x -> ResData.build(CREATED.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 修改用户信息
     *
     * @param request http请求
     * @param userId  用户id
     * @param reqUser 请求体
     * @return 操作结果
     */
    @PutMapping("/{userId}")
    @ResponseBody
    public CompletionStage<ResData> modifyUserAttribute(
            final HttpServletRequest request, @PathVariable final String userId, ReqUser reqUser) {
        //校验请求体
        return checkModUserBody(reqUser, userId)
                .thenAccept(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_UPDATE_USER_BODY);
                    }
                    List<CmpUser> users = userService.describeCmpUsers();
                    CmpUser cmpUser = users.stream().filter(vo ->
                            userId.equals(vo.getUserId()))
                            .findAny()
                            .orElseThrow(() -> new CoreException(ERR_CMP_USER_NOT_FOUND));
                    //请求体转换
                    buildCmpUser(reqUser, cmpUser)
                            .thenAccept(resUser -> {
                                users.stream().filter(vo ->
                                        //校验重名
                                        resUser.getUserName().equals(vo.getUserName())
                                                && !resUser.getUserId().equals(vo.getUserId()))
                                        .findAny()
                                        .ifPresent(x -> {
                                            throw new CoreException(ERR_REPEATED_USER_NAME);
                                        });
                                //更新数据库
                                userService.updateUser(resUser)
                                        .thenAccept(updateFlag -> {
                                            if (!updateFlag) {
                                                throw new CoreException(ERR_UPDATE_USER);
                                            }
                                        });
                            });
                }).thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 删除用户
     *
     * @param request http请求
     * @param userId  用户id
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    @ResponseBody
    public CompletionStage<ResData> deleteUser(
            final HttpServletRequest request, @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() -> userService.describeUserAttribute(userId))
                .thenAccept(cmpUser ->
                        //删除用户、删除用户映射关系
                        userService.deleteUser(userId)
                                .thenCombine(userService.delUserMappingsByUserId(userId),
                                        (userFlag, mappingFlag) -> {
                                            if (!userFlag) {
                                                throw new CoreException(ERR_DELETE_USER);
                                            }
                                            if (!mappingFlag) {
                                                throw new CoreException(ERR_DELETE_USER_MAPPING);
                                            }
                                            return null;
                                        })
                ).thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 根据用户ID查询映射关系列表
     *
     * @param request http请求
     * @param userId  用户id
     * @return 映射关系列表
     */
    @RequestMapping("/{userId}/mappings")
    @ResponseBody
    public CompletionStage<ResData> describeMappingsByUserId(
            final HttpServletRequest request, @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserMappings(userId))
                .thenApply(mappings ->
                        ResData.build(OK.value(), new ResUserMappings(mappings), request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 添加用户映射关系
     *
     * @param request http请求
     * @param mapping 请求体
     * @return 操作结果
     */
    @PostMapping("/mappings")
    @ResponseBody
    public CompletionStage<ResData> addUserMapping(
            final HttpServletRequest request, final ReqAddMapping mapping) {
        //校验请求体
        return checkAddMappingBody(mapping).thenAccept(flag ->
                //转换请求体
                buildAddUserMapping(mapping)
                        .thenCompose(userMapping ->
                                //云账号认证
                                authenticate(userMapping.getCloudId(), userMapping.getAuthInfo())
                                        .thenCompose(authenticateFlag ->
                                                //插入数据库记录
                                                userService.addUserMapping(userMapping))
                                        .thenAccept(addFlag -> {
                                            if (!addFlag) {
                                                throw new CoreException(ERR_ADD_MAPPING);
                                            }
                                        })))
                .thenApply(x -> ResData.build(CREATED.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 更新用户映射关系（修改云账号密码）
     *
     * @param request   http请求
     * @param mappingId 映射关系id
     * @param mapping   请求体
     * @return 操作结果
     */
    @PutMapping("/mappings/{mappingId}")
    @ResponseBody
    public CompletionStage<ResData> updateUserMapping(
            final HttpServletRequest request, @PathVariable final String mappingId, final ReqModMapping mapping) {
        //校验请求体
        return checkModMappingBody(mapping)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_UPDATE_MAPPING_BODY);
                    }
                    UserMappingEntity userMapping = userService.describeUserMappingById(mappingId);
                    //转换请求体
                    return buildUpdateUserMapping(mapping, userMapping).thenAccept(resUserMapping ->
                            //云账号认证
                            authenticate(resUserMapping.getCloudId(), resUserMapping.getAuthInfo())
                                    .thenCompose(authenticateFlag ->
                                            //更新数据库记录
                                            userService.updateUserMapping(resUserMapping))
                                    .thenAccept(updateFlag -> {
                                        if (!updateFlag) {
                                            throw new CoreException(ERR_UPDATE_MAPPING);
                                        }
                                    }));
                }).thenApply(x -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 根据id删除用户映射关系
     *
     * @param request   http请求
     * @param mappingId 映射关系id
     * @return 操作结果
     */
    @DeleteMapping("/mappings/{mappingId}")
    @ResponseBody
    public CompletionStage<ResData> deleteUserMappingById(
            final HttpServletRequest request, @PathVariable final String mappingId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserMappingById(mappingId))
                .thenAccept(mapping ->
                        userService.delUserMappingsById(mappingId)
                                .thenAccept(mappingFlag -> {
                                    if (!mappingFlag) {
                                        throw new CoreException(ERR_DELETE_USER_MAPPING);
                                    }
                                }))
                .thenApply(res -> ResData.build(NO_CONTENT.value(), null, request))
                .exceptionally(e -> ResData.failure(e, request));
    }

    /**
     * 云账号认证
     *
     * @param cloudId  云id
     * @param authInfo 云账号信息
     * @return 认证结果
     */
    private CompletionStage<Boolean> authenticate(String cloudId, String authInfo) {
        String url = CMP_V1 + "/authenticate";
        return cloudService.describeCloudById(cloudId)
                .thenCompose(cloud -> {
                            cloud.setAuthInfo(authInfo);
                            return httpPost(url, null, cloud)
                                    .thenApply(res -> true)
                                    .exceptionally(e -> {
                                        logger.error("401: authenticate in cloud {} failed..", cloud.getCloudName());
                                        throw new CoreException(ERR_USER_AUTHENTICATE);
                                    });
                        }
                );
    }

    /**
     * 校验注册用户请求体
     *
     * @param user 请求体
     * @return 校验结果
     */
    private CompletionStage<Boolean> checkRegisterUserBody(ReqUser user) {
        return CompletableFuture.supplyAsync(() -> {
                    boolean bodyFlag = (null != user.getUserName()
                            && null != user.getPassword()
                            && null != user.getPhone()
                            && null != user.getPhone());
                    if (!bodyFlag) {
                        throw new CoreException(ERR_REGISTER_USER_BODY);
                    }
                    boolean nameFlag = userService.describeCmpUsers().stream()
                            .anyMatch(u -> user.getUserName().equals(u.getUserName()));
                    if (!nameFlag) {
                        throw new CoreException(ERR_REPEATED_USER_NAME);
                    }
                    return true;
                }

        );
    }

    private CompletionStage<Boolean> checkModUserBody(ReqUser user, String userId) {
        return CompletableFuture.supplyAsync(() ->
                (null != user.getUserName()
                        && null != user.getPassword()
                        && null != user.getPhone()
                        && null != user.getPhone())
        );
    }

    /**
     * 校验添加用户映射请求体
     *
     * @param mapping 请求体
     * @return 校验结果
     */
    private CompletionStage<Boolean> checkAddMappingBody(ReqAddMapping mapping) {
        return CompletableFuture.supplyAsync(() -> {
                    boolean bodyFlag = (null != mapping.getCmpUserId()
                            && null != mapping.getCloudId()
                            && null != mapping.getCmpUserName()
                            && null != mapping.getAccessKey()
                            && null != mapping.getSecret());
                    if (!bodyFlag) {
                        throw new CoreException(ERR_ADD_MAPPING_BODY);
                    }
                    userService.describeUserMappings(mapping.getCmpUserId()).stream()
                            .filter(userMappingEntity -> mapping.getCloudId().equals(userMappingEntity.getCloudId()))
                            .findAny()
                            .ifPresent(x -> {
                                throw new CoreException(ERR_REPEATED_MAPPING);
                            });
                    return true;
                }

        );
    }

    private CompletionStage<Boolean> checkModMappingBody(ReqModMapping mapping) {
        return CompletableFuture.supplyAsync(() ->
                (null != mapping.getSecret()
                        && null != mapping.getAccessKey())
        );
    }

    /**
     * 生成cmpUser实体
     *
     * @param user 请求体
     * @return cmpUser实体
     */
    private CompletionStage<CmpUser> buildCmpUser(ReqUser user, CmpUser cmpUser) {
        return CompletableFuture.supplyAsync(() -> {
            CmpUser resUser = new CmpUser();
            if (null == cmpUser) {
                resUser.setUserId(UUID.randomUUID().toString());
            } else {
                resUser.setUserId(cmpUser.getUserId());
            }
            resUser.setRoleName(Role.USER.toString());
            resUser.setUserName(user.getUserName());
            resUser.setPassword(user.getPassword());
            String token = "name: " + user.getUserName() + " password: " + user.getPassword();
            resUser.setToken(token);
            resUser.setPhone(user.getPhone());
            resUser.setEmail(user.getEmail());
            return resUser;
        });
    }

    /**
     * 生成userMappingEntity
     *
     * @param mapping 请求体
     * @return userMappingEntity
     */
    private CompletionStage<UserMappingEntity> buildAddUserMapping(ReqAddMapping mapping) {
        return CompletableFuture.supplyAsync(() -> {
            UserMappingEntity resMapping = new UserMappingEntity();
            resMapping.setId(UUID.randomUUID().toString());
            resMapping.setCmpUserId(mapping.getCmpUserId());
            resMapping.setCmpUserName(mapping.getCmpUserName());
            resMapping.setCloudId(mapping.getCloudId());
            resMapping.setAccessKey(mapping.getAccessKey());
            String authInfo = "accessKey: " + mapping.getAccessKey()
                    + " secret: " + mapping.getSecret();
            resMapping.setAuthInfo(authInfo);
            return resMapping;
        });
    }

    /**
     * 生成userMappingEntity
     *
     * @param mapping     请求体
     * @param userMapping userMapping
     * @return userMappingEntity
     */
    private CompletionStage<UserMappingEntity> buildUpdateUserMapping(
            ReqModMapping mapping, UserMappingEntity userMapping) {
        return CompletableFuture.supplyAsync(() -> {
            UserMappingEntity resMapping = new UserMappingEntity();
            resMapping.setId(userMapping.getId());
            resMapping.setCmpUserId(userMapping.getCmpUserId());
            resMapping.setCmpUserName(userMapping.getCmpUserName());
            resMapping.setCloudId(userMapping.getCloudId());
            resMapping.setAccessKey(mapping.getAccessKey());
            String authInfo = "accessKey: " + mapping.getAccessKey()
                    + " secret: " + mapping.getSecret();
            resMapping.setAuthInfo(authInfo);
            return resMapping;
        });
    }
}
