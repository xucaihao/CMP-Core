package com.cmp.core.user.service;

import com.cmp.core.common.CoreException;
import com.cmp.core.common.ErrorEnum;
import com.cmp.core.user.dao.UserDao;
import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.UserMappingEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.cmp.core.common.ErrorEnum.ERR_USER_MAPPING_NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    /**
     * 查询cmpUser列表
     *
     * @return cmpUser列表
     */
    @Override
    public List<CmpUser> describeCmpUsers() {
        return userDao.describeCmpUsers();
    }

    /**
     * 查询cmpUser的映射关系列表
     *
     * @param cmpUserId cmpUserId
     * @return 映射关系列表
     */
    @Override
    public List<UserMappingEntity> describeUserMappings(String cmpUserId) {
        return userDao.describeUserMappings(cmpUserId);
    }

    /**
     * 查询cmpUser在指定云上的映射关系
     *
     * @param cmpUserId cmpUserId
     * @param cloudId   云id
     * @return 映射关系
     */
    @Override
    public UserMappingEntity describeUserMapping(String cmpUserId, String cloudId) {
        return userDao.describeUserMappings(cmpUserId)
                .stream()
                .filter(userMappingEntity ->
                        cloudId.equals(userMappingEntity.getCloudId()))
                .findAny()
                .orElseThrow(() -> new CoreException(ERR_USER_MAPPING_NOT_FOUND));
    }

    /**
     * 查询cmpUser在底层云上的authInfo
     *
     * @param cmpUserId cmpUserId
     * @param cloudId   云id
     * @return authInfo
     */
    @Override
    public String getUserAuthInfo(String cmpUserId, String cloudId) {
        return describeUserMapping(cmpUserId, cloudId)
                .getAuthInfo();
    }
}
