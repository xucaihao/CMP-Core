package com.cmp.core.user.dao;

import com.cmp.core.user.model.CmpUser;
import com.cmp.core.user.model.UserMappingEntity;

import java.util.List;
import java.util.Map;

public interface UserDao {

    /**
     * 查询cmpUser列表
     *
     * @return cmpUser列表
     */
    List<CmpUser> describeCmpUsers();

    /**
     * 查询指定cmpUser
     *
     * @param cmpUserId cmpUserId
     * @return cmpUser
     */
    CmpUser describeUserById(String cmpUserId);

    /**
     * 查询指定cmpUser
     *
     * @param userName userName
     * @return cmpUser
     */
    CmpUser describeUserByName(String userName);

    /**
     * 添加用户记录
     *
     * @param user user
     */
    void addUser(Map<String, Object> user);

    /**
     * 更新用户记录
     *
     * @param user user
     */
    void updateUser(Map<String, Object> user);

    /**
     * 删除数据库用户记录
     *
     * @param userId 用户id
     */
    void deleteUser(String userId);

    List<UserMappingEntity> describeUserMappings();

    /**
     * 根据id查询映射关系
     *
     * @param mappingId 映射id
     * @return 指定的映射关系
     */
    UserMappingEntity describeUserMappingById(String mappingId);

    /**
     * 查询cmpUser的映射关系
     *
     * @param cmpUserId cmpUserId
     * @return 映射关系
     */
    List<UserMappingEntity> describeUserMappingsByCmpUserId(String cmpUserId);

    /**
     * 查询cmpUser的映射关系
     *
     * @param cloudUserId cloudUserId
     * @return 映射关系
     */
    List<UserMappingEntity> describeUserMappingsByCloudUserId(String cloudUserId);

    /**
     * 添加cmpUser与cloudUser映射关系
     *
     * @param userMappingEntity 映射关系
     */
    void addUserMapping(UserMappingEntity userMappingEntity);

    /**
     * 更新cmpUser与cloudUser映射关系
     *
     * @param userMappingEntity 映射关系
     */
    void updateUserMapping(UserMappingEntity userMappingEntity);

    /**
     * 根据用户id删除用户映射关系
     *
     * @param cmpUserId cmpUserId
     */
    void delUserMappingsByCmpUserId(String cmpUserId);

    /**
     * 根据id删除用户映射关系
     *
     * @param mappingId 映射关系id
     */
    void delUserMappingsById(String mappingId);

    /**
     * 据云id删除用户映射关系
     *
     * @param cloudId 云id
     */
    void delUserMappingsByCloudId(String cloudId);

}
