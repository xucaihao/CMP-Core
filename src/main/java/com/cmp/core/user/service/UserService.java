package com.cmp.core.user.service;

import com.cmp.core.user.model.CmpUser;
import com.cmp.core.user.model.UserMappingEntity;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface UserService {

    /**
     * 获取登录用户信息
     *
     * @param user 登录信息
     * @return 用户信息
     */
    CmpUser describeLoginUser(CmpUser user);

    /**
     * 查询cmpUser列表
     *
     * @return cmpUser列表
     */
    List<CmpUser> describeCmpUsers();

    /**
     * 查询指定cmpUser
     *
     * @param userId cmpUserId
     * @return cmpUser
     */
    CmpUser describeUserAttribute(String userId);

    /**
     * 添加用户记录
     *
     * @param user user
     * @return 操作结果
     */
    CompletionStage<Boolean> addUser(CmpUser user);

    /**
     * 更新用户记录
     *
     * @param user user
     * @return 操作结果
     */
    CompletionStage<Boolean> updateUser(CmpUser user);

    /**
     * 删除数据库用户记录
     *
     * @param userId 用户id
     * @return 操作结果
     */
    CompletionStage<Boolean> deleteUser(String userId);

    /**
     * 根据id查询映射关系
     *
     * @param mappingId 映射id
     * @return 指定的映射关系
     */
    UserMappingEntity describeUserMappingById(String mappingId);

    /**
     * 查询cmpUser的映射关系列表
     *
     * @param cmpUserId cmpUserId
     * @return 映射关系列表
     */
    List<UserMappingEntity> describeUserMappings(String cmpUserId);

    /**
     * 查询cmpUser在指定云上的映射关系
     *
     * @param userId  userId
     * @param cloudId 云id
     * @param type    userId类型(CMP/CLOUD)
     * @return 映射关系
     */
    UserMappingEntity describeUserMapping(String userId, String cloudId, String type);

    /**
     * 添加用户映射关系记录
     *
     * @param userMapping userMapping
     * @return 操作结果
     */
    CompletionStage<Boolean> addUserMapping(UserMappingEntity userMapping);

    /**
     * 修改用户映射关系记录
     *
     * @param userMapping userMapping
     * @return 操作结果
     */
    CompletionStage<Boolean> updateUserMapping(UserMappingEntity userMapping);

    /**
     * 根据云id删除用户映射关系
     *
     * @param cloudId 云id
     * @return 操作结果
     */
    CompletionStage<Boolean> delUserMappingsByCloudId(String cloudId);

    /**
     * 根据用户id删除用户映射关系
     *
     * @param userId 用户id
     * @return 操作结果
     */
    CompletionStage<Boolean> delUserMappingsByUserId(String userId);

    /**
     * 根据id删除用户映射关系
     *
     * @param mappingId 映射关系id
     * @return 操作结果
     */
    CompletionStage<Boolean> delUserMappingsById(String mappingId);

    /**
     * 查询cmpUser在底层云上的authInfo
     *
     * @param cmpUserId cmpUserId
     * @param cloudId   云id
     * @return authInfo
     */
    String getUserAuthInfo(String cmpUserId, String cloudId);
}
