package com.cmp.core.user.service;

import com.cmp.core.common.Constance;
import com.cmp.core.common.CoreException;
import com.cmp.core.user.dao.UserDao;
import com.cmp.core.user.modle.CmpUser;
import com.cmp.core.user.modle.Role;
import com.cmp.core.user.modle.UserMappingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.CLOUD;
import static com.cmp.core.common.Constance.CMP;
import static com.cmp.core.common.ErrorEnum.ERR_CMP_USER_NOT_FOUND;
import static com.cmp.core.common.ErrorEnum.ERR_USER_MAPPING_NOT_FOUND;
import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao userDao;

    /**
     * 获取登录用户信息
     *
     * @param user 登录信息
     * @return 用户信息
     */
    @Override
    public CmpUser describeLoginUser(CmpUser user) {
        String userName = user.getUserName();
        String password = user.getPassword();
        CmpUser cmpUser = Optional.ofNullable(userDao.describeUserByName(userName))
                .filter(u -> password.equals(u.getPassword()))
                .orElseThrow(() -> new CoreException(ERR_CMP_USER_NOT_FOUND));
        switch (cmpUser.getRoleName()) {
            case Constance.USER:
                cmpUser.setRole(Role.USER);
                return cmpUser;
            case Constance.MANAGER:
                cmpUser.setRole(Role.MANAGER);
                return cmpUser;
            default:
                return null;
        }
    }

    /**
     * 查询cmpUser列表
     *
     * @return cmpUser列表
     */
    @Override
    public List<CmpUser> describeCmpUsers() {
        return userDao.describeCmpUsers().stream()
                .peek(cmpUser -> {
                    switch (cmpUser.getRoleName()) {
                        case Constance.USER:
                            cmpUser.setRole(Role.USER);
                            break;
                        case Constance.MANAGER:
                            cmpUser.setRole(Role.MANAGER);
                            break;
                        default:
                            break;
                    }
                }).collect(toList());
    }

    /**
     * 查询指定cmpUser
     *
     * @param userId cmpUserId
     * @return cmpUser
     */
    @Override
    public CmpUser describeUserAttribute(String userId) {
        CmpUser cmpUser = Optional.ofNullable(userDao.describeUserById(userId))
                .orElseThrow(() -> new CoreException(ERR_CMP_USER_NOT_FOUND));
        switch (cmpUser.getRoleName()) {
            case Constance.USER:
                cmpUser.setRole(Role.USER);
                return cmpUser;
            case Constance.MANAGER:
                cmpUser.setRole(Role.MANAGER);
                return cmpUser;
            default:
                return null;
        }
    }

    /**
     * 添加用户记录
     *
     * @param user user
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> addUser(CmpUser user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.addUser(user);
                return true;
            } catch (Exception e) {
                logger.error("addUser in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 更新用户记录
     *
     * @param user user
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> updateUser(CmpUser user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.updateUser(user);
                return true;
            } catch (Exception e) {
                logger.error("updateUser in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 删除数据库用户记录
     *
     * @param userId 用户id
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> deleteUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.deleteUser(userId);
                return true;
            } catch (Exception e) {
                logger.error("deleteUser in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 根据id查询映射关系
     *
     * @param mappingId 映射id
     * @return 指定的映射关系
     */
    @Override
    public UserMappingEntity describeUserMappingById(String mappingId) {
        return Optional.ofNullable(userDao.describeUserMappingById(mappingId))
                .orElseThrow(() -> new CoreException(ERR_USER_MAPPING_NOT_FOUND));
    }

    /**
     * 查询cmpUser的映射关系列表
     *
     * @param cmpUserId cmpUserId
     * @return 映射关系列表
     */
    @Override
    public List<UserMappingEntity> describeUserMappings(String cmpUserId) {
        return userDao.describeUserMappingsByCmpUserId(cmpUserId);
    }

    /**
     * 查询cmpUser在指定云上的映射关系
     *
     * @param userId  userId
     * @param cloudId 云id
     * @param type    userId类型(CMP/CLOUD)
     * @return 映射关系
     */
    @Override
    public UserMappingEntity describeUserMapping(String userId, String cloudId, String type) {
        List<UserMappingEntity> userMappingEntities = new ArrayList<>(16);
        switch (type) {
            case CMP:
                userMappingEntities = userDao.describeUserMappingsByCmpUserId(userId);
                break;
            case CLOUD:
                userMappingEntities = userDao.describeUserMappingsByCloudUserId(userId);
        }
        return userMappingEntities.stream().filter(userMappingEntity ->
                cloudId.equals(userMappingEntity.getCloudId()))
                .findAny().orElseThrow(() -> new CoreException(ERR_USER_MAPPING_NOT_FOUND));
    }

    /**
     * 添加用户映射关系记录
     *
     * @param userMapping userMapping
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> addUserMapping(UserMappingEntity userMapping) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.addUserMapping(userMapping);
                return true;
            } catch (Exception e) {
                logger.error("addUserMapping in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 修改用户映射关系记录
     *
     * @param userMapping userMapping
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> updateUserMapping(UserMappingEntity userMapping) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.updateUserMapping(userMapping);
                return true;
            } catch (Exception e) {
                logger.error("updateUserMapping in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 据云id删除用户映射关系
     *
     * @param cloudId 云id
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> delUserMappingsByCloudId(String cloudId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.delUserMappingsByCloudId(cloudId);
                return true;
            } catch (Exception e) {
                logger.error("delUserMappingsByCloudId in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 根据用户id删除用户映射关系
     *
     * @param userId 用户id
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> delUserMappingsByUserId(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.delUserMappingsByCmpUserId(userId);
                return true;
            } catch (Exception e) {
                logger.error("delUserMappingsByUserId in sql error: {}", e);
                return false;
            }
        });
    }

    /**
     * 根据id删除用户映射关系
     *
     * @param mappingId 映射关系id
     * @return 操作结果
     */
    @Override
    public CompletionStage<Boolean> delUserMappingsById(String mappingId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.delUserMappingsById(mappingId);
                return true;
            } catch (Exception e) {
                logger.error("delUserMappingsById in sql error: {}", e);
                return false;
            }
        });
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
        return describeUserMapping(cmpUserId, cloudId, CMP)
                .getAuthInfo();
    }
}
