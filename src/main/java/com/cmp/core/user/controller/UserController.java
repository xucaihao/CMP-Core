package com.cmp.core.user.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.cloud.modules.CloudService;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.user.model.CmpUser;
import com.cmp.core.user.model.Role;
import com.cmp.core.user.model.UserMappingEntity;
import com.cmp.core.user.model.req.ReqAddMapping;
import com.cmp.core.user.model.req.ReqModMapping;
import com.cmp.core.user.model.req.ReqUser;
import com.cmp.core.user.model.res.ResUser;
import com.cmp.core.user.model.res.ResUserMappings;
import com.cmp.core.user.model.res.ResUsers;
import com.cmp.core.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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
     * 获取登录用户信息
     *
     * @param request  http请求
     * @param response http响应
     * @throws IOException IOException
     */
    @RequestMapping(value = "/loginInformation", method = RequestMethod.POST)
    @ResponseBody
    public CompletionStage<JsonNode> describeLoginUser(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        CmpUser user = JsonUtil.stringToObject(body, CmpUser.class);
        return CompletableFuture.supplyAsync(() ->
                userService.describeLoginUser(user))
                .thenApply(resUser -> okFormat(OK.value(), new ResUser(resUser), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 查询用户列表
     *
     * @param response http响应
     * @return 用户列表
     */
    @RequestMapping("")
    @ResponseBody
    public CompletionStage<JsonNode> describeUsers(final HttpServletResponse response) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeCmpUsers())
                .thenApply(users -> okFormat(OK.value(), new ResUsers(users), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 根据id查询指定用户
     *
     * @param response http响应
     * @param userId   用户id
     * @return 指定用户
     */
    @RequestMapping("/{userId}")
    @ResponseBody
    public CompletionStage<JsonNode> describeUserAttribute(
            final HttpServletResponse response, @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserAttribute(userId))
                .thenApply(user -> okFormat(OK.value(), new ResUser(user), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 用户注册
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public CompletionStage<JsonNode> registerUser(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqUser user = JsonUtil.stringToObject(body, ReqUser.class);
        //校验请求体
        return checkRegisterUserBody(user).thenAccept(flag ->
                //转换请求体
                buildCmpUser(user, null).thenCompose(resUser ->
                        //添加数据库记录
                        userService.addUser(resUser)).thenAccept(registerFlag -> {
                    if (!registerFlag) {
                        throw new CoreException(ERR_REGISTER_USER);
                    }
                })).thenApply(x -> okFormat(CREATED.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 修改用户信息
     *
     * @param request  http请求
     * @param response http响应
     * @param userId   用户id
     * @return 操作结果
     * @throws IOException IOException
     */
    @PutMapping("/{userId}")
    @ResponseBody
    public CompletionStage<JsonNode> modifyUserAttribute(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable final String userId)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqUser reqUser = JsonUtil.stringToObject(body, ReqUser.class);
        //校验请求体
        return checkModUserBody(reqUser)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_UPDATE_USER_BODY);
                    }
                    List<CmpUser> users = userService.describeCmpUsers();
                    CmpUser cmpUser = users.stream().filter(vo ->
                            userId.equals(vo.getUserId()))
                            .findAny()
                            .orElseThrow(() -> new CoreException(ERR_CMP_USER_NOT_FOUND));
                    //请求体转换
                    return buildCmpUser(reqUser, cmpUser)
                            .thenCompose(resUser -> {
                                users.stream().filter(vo ->
                                        //校验重名
                                        resUser.getUserName().equals(vo.getUserName())
                                                && !resUser.getUserId().equals(vo.getUserId()))
                                        .findAny()
                                        .ifPresent(x -> {
                                            throw new CoreException(ERR_REPEATED_USER_NAME);
                                        });
                                //更新数据库
                                return userService.updateUser(resUser)
                                        .thenApply(updateFlag -> {
                                            if (!updateFlag) {
                                                throw new CoreException(ERR_UPDATE_USER);
                                            } else {
                                                return userService.describeUserAttribute(userId);
                                            }
                                        });
                            });
                }).thenApply(user -> okFormat(OK.value(), new ResUser(user), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 删除用户
     *
     * @param response http响应
     * @param userId   用户id
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    @ResponseBody
    public CompletionStage<JsonNode> deleteUser(
            final HttpServletResponse response,
            @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() -> userService.describeUserAttribute(userId))
                .thenCompose(cmpUser -> {
                            //删除用户映射关系
                            List<UserMappingEntity> mappings = userService.describeUserMappings(userId);
                            if (!CollectionUtils.isEmpty(mappings)) {
                                userService.delUserMappingsByUserId(userId)
                                        .thenAccept(mappingFlag -> {
                                            if (!mappingFlag) {
                                                throw new CoreException(ERR_DELETE_USER_MAPPING);
                                            }
                                        });
                            }
                            //删除用户
                            return userService.deleteUser(userId)
                                    .thenApply(userFlag -> {
                                        if (!userFlag) {
                                            throw new CoreException(ERR_DELETE_USER);
                                        }
                                        return null;
                                    });
                        }
                ).thenApply(res ->
                        okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 根据用户ID查询映射关系列表
     *
     * @param response http响应
     * @param userId   用户id
     * @return 映射关系列表
     */
    @RequestMapping("/{userId}/mappings")
    @ResponseBody
    public CompletionStage<JsonNode> describeMappingsByUserId(
            final HttpServletResponse response,
            @PathVariable final String userId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserMappings(userId))
                .thenApply(mappings -> okFormat(OK.value(), new ResUserMappings(mappings), response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 添加用户映射关系
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException IOException
     */
    @PostMapping("/mappings")
    @ResponseBody
    public CompletionStage<JsonNode> addUserMapping(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqAddMapping mapping = JsonUtil.stringToObject(body, ReqAddMapping.class);
        //校验请求体
        return checkAddMappingBody(mapping).thenCompose(flag ->
                //转换请求体
                buildAddUserMapping(mapping)
                        .thenCompose(userMapping ->
                                //云账号认证
                                authenticate(userMapping.getCloudId(), userMapping.getAuthInfo())
                                        .thenCompose(authenticateFlag ->
                                                //插入数据库记录
                                                userService.addUserMapping(userMapping))
                                        .thenApply(addFlag -> {
                                            if (!addFlag) {
                                                throw new CoreException(ERR_ADD_MAPPING);
                                            }
                                            return null;
                                        })))
                .thenApply(x -> okFormat(CREATED.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 更新用户映射关系（修改云账号密码）
     *
     * @param request   http请求
     * @param response  http响应
     * @param mappingId 映射关系id
     * @return 操作结果
     * @throws IOException IOException
     */
    @PutMapping("/mappings/{mappingId}")
    @ResponseBody
    public CompletionStage<JsonNode> updateUserMapping(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable final String mappingId)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqModMapping mapping = JsonUtil.stringToObject(body, ReqModMapping.class);
        //校验请求体
        return checkModMappingBody(mapping)
                .thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_UPDATE_MAPPING_BODY);
                    }
                    UserMappingEntity userMapping = userService.describeUserMappingById(mappingId);
                    //转换请求体
                    return buildUpdateUserMapping(mapping, userMapping).thenCompose(resUserMapping ->
                            //云账号认证
                            authenticate(resUserMapping.getCloudId(), resUserMapping.getAuthInfo())
                                    .thenCompose(authenticateFlag ->
                                            //更新数据库记录
                                            userService.updateUserMapping(resUserMapping))
                                    .thenApply(updateFlag -> {
                                        if (!updateFlag) {
                                            throw new CoreException(ERR_UPDATE_MAPPING);
                                        }
                                        return null;
                                    }));
                }).thenApply(x -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 根据id删除用户映射关系
     *
     * @param response  http响应
     * @param mappingId 映射关系id
     * @return 操作结果
     */
    @DeleteMapping("/mappings/{mappingId}")
    @ResponseBody
    public CompletionStage<JsonNode> deleteUserMappingById(
            final HttpServletResponse response, @PathVariable final String mappingId) {
        return CompletableFuture.supplyAsync(() ->
                userService.describeUserMappingById(mappingId))
                .thenCompose(mapping ->
                        userService.delUserMappingsById(mappingId)
                                .thenApply(mappingFlag -> {
                                    if (!mappingFlag) {
                                        throw new CoreException(ERR_DELETE_USER_MAPPING);
                                    }
                                    return null;
                                }))
                .thenApply(res -> okFormat(NO_CONTENT.value(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    /**
     * 云账号认证
     *
     * @param cloudId  云id
     * @param authInfo 云账号信息
     * @return 认证结果
     */
    private CompletionStage<Boolean> authenticate(String cloudId, String authInfo) {
        String url = "/authenticate";
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
                    boolean bodyFlag = (null != user.getRoleName()
                            && null != user.getUserName()
                            && null != user.getPassword());
                    if (!bodyFlag) {
                        throw new CoreException(ERR_REGISTER_USER_BODY);
                    }
                    boolean nameFlag = userService.describeCmpUsers().stream()
                            .anyMatch(u -> user.getUserName().equals(u.getUserName()));
                    if (nameFlag) {
                        throw new CoreException(ERR_REPEATED_USER_NAME);
                    }
                    return true;
                }

        );
    }

    private CompletionStage<Boolean> checkModUserBody(ReqUser user) {
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
            if ("manager".equals(user.getRoleName().toLowerCase())) {
                resUser.setRoleName(Role.MANAGER.toString());
            } else {
                resUser.setRoleName(Role.USER.toString());
            }
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
            String authInfo = "{\n" + "\"accessKey\" : \"" + mapping.getAccessKey() + "\",\n" +
                    "\"secret\" : \"" + mapping.getSecret() + "\"\n" + "}";
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
            String authInfo = "{\n" + "\"accessKey\" : \"" + mapping.getAccessKey() + "\",\n" +
                    "\"secret\" : \"" + mapping.getSecret() + "\"\n" + "}";
            resMapping.setAuthInfo(authInfo);
            return resMapping;
        });
    }
}
