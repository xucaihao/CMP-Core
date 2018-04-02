package com.cmp.core.common;

import com.cmp.core.cloud.entity.CloudEntity;
import com.cmp.core.user.modle.CmpUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface BaseHelper {


    /**
     * 聚合多线程list
     *
     * @param futures 多线程返回结果
     * @param <T>     泛型
     * @return 聚合后列表
     */
    <T> List<T> joinRes(List<CompletionStage<T>> futures);

    /**
     * 合并多个List为一个List
     *
     * @param lists 多个List
     * @param <T>   T
     * @return 合并后list
     */
    <T> List<T> aggregateList(final List<List<T>> lists);

    /**
     * 组装url参数
     *
     * @param api   api
     * @param cloud 云
     * @return 组装后url
     */
    CompletionStage<String> formatUrl(String api, CloudEntity cloud);

    /**
     * 绑定转发路由
     *
     * @param cloud 云
     * @return String
     */
    CompletionStage<String> bindAdapter(CloudEntity cloud);

    /**
     * 查询cmp用户
     *
     * @param request 请求
     * @return cmp用户
     */
    CompletionStage<CmpUser> getCmpUserEntity(HttpServletRequest request);

    /**
     * 获取云实体
     *
     * @param request 请求
     * @return 云实体
     */
    CompletionStage<CloudEntity> getCloudEntity(HttpServletRequest request);

    /**
     * 获取所有云实体
     *
     * @param request 请求
     * @return 所有云实体
     */
    CompletionStage<List<CloudEntity>> getAllCloudEntity(HttpServletRequest request);

    /**
     * 获取cloud用户id
     *
     * @param cmpUserId cmp用户id
     * @param cloud     云
     * @return cloud用户id
     */
    String getCloudUserId(String cmpUserId, CloudEntity cloud);

}
