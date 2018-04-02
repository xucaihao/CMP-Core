package com.cmp.core.user.dao;

import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.UserMappingEntity;

import java.util.List;

public interface UserDao {

    List<CmpUser> describeCmpUsers();

    /**
     * 查询cmpUser的映射关系
     *
     * @param cmpUserId cmpUserId
     * @return 映射关系
     */
    List<UserMappingEntity> describeUserMappings(String cmpUserId);

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
     * 删除cmpUser与cloudUser映射关系
     *
     * @param cmpUserId cmpUserId
     */
    void deleteUserMapping(String cmpUserId);

}
