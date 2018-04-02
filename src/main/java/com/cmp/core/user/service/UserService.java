package com.cmp.core.user.service;

import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.UserMappingEntity;

import java.util.List;

public interface UserService {

    /**
     * 查询cmpUser列表
     *
     * @return cmpUser列表
     */
    List<CmpUser> describeCmpUsers();

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
     * @param cmpUserId cmpUserId
     * @param cloudId   云id
     * @return 映射关系
     */
    UserMappingEntity describeUserMapping(String cmpUserId, String cloudId);

    /**
     * 查询cmpUser在底层云上的authInfo
     *
     * @param cmpUserId cmpUserId
     * @param cloudId   云id
     * @return authInfo
     */
    String getUserAuthInfo(String cmpUserId, String cloudId);
}
