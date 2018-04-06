package com.cmp.core.common;

import com.cmp.core.cloud.model.CloudEntity;
import com.cmp.core.user.modle.CmpUser;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static com.cmp.core.common.ErrorEnum.ERR_CLOUD_ID_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private BaseHelper baseHelper;

    /**
     * 聚合多线程list
     *
     * @param futures 多线程返回结果
     * @param <T>     泛型
     * @return 聚合后列表
     */
    protected <T> List<T> joinRes(List<CompletionStage<T>> futures) {
        return baseHelper.joinRes(futures);
    }

    /**
     * 合并多个List为一个List
     *
     * @param lists 多个List
     * @param <T>   T
     * @return 合并后list
     */
    protected <T> List<T> aggregateList(final List<List<T>> lists) {
        return baseHelper.aggregateList(lists);
    }

    /**
     * 查询cmp用户
     *
     * @param request 请求
     * @return cmp用户
     */
    protected CompletionStage<CmpUser> getCmpUserEntity(HttpServletRequest request) {
        return baseHelper.getCmpUserEntity(request);
    }

    /**
     * 获取云实体
     *
     * @param request 请求
     * @return 云实体
     */
    protected CompletionStage<CloudEntity> getCloudEntity(HttpServletRequest request) {
        String cloudId = Optional.ofNullable(request.getHeader(HEADER_CLOUD_ID))
                .orElseThrow(() -> new CoreException(ERR_CLOUD_ID_NOT_FOUND));
        return baseHelper.getCloudEntity(request, cloudId);
    }

    /**
     * 获取云实体
     *
     * @param request 请求
     * @param cloudId 云平台id
     * @return 云实体
     */
    protected CompletionStage<CloudEntity> getCloudEntity(HttpServletRequest request, String cloudId) {
        return baseHelper.getCloudEntity(request, cloudId);
    }

    /**
     * 获取所有已对接云平台
     *
     * @param request 请求
     * @return 所有已对接云平台
     */
    protected CompletionStage<List<CloudEntity>> getAllCloudEntity(HttpServletRequest request) {
        return baseHelper.getAllCloudEntity(request);
    }

//    /**
//     * 通过CmpUserId获取cloud用户id
//     *
//     * @param cmpUserId cmp用户id
//     * @param cloud     云
//     * @return cloud用户id
//     */
//    protected String getCloudUserId(String cmpUserId, CloudEntity cloud) {
//        return baseHelper.getCloudUserId(cmpUserId, cloud);
//    }

    protected <T> CompletionStage<ResData<T>> httpGet(String url, Class<T> clz, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity<T> res = CoreWsClient.get(formattedUrl, cloud, clz);
                    logger.info("GET: {} , cloud: {} , cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res, clz);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected CompletionStage<ResData> httpDel(String url, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity res = CoreWsClient.delete(formattedUrl, cloud);
                    logger.info("DELETE: {} , cloud: {} , cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected <T> CompletionStage<ResData<T>> httpPost(String url, String body, Class<T> clz, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity<T> res = CoreWsClient.post(formattedUrl, body, cloud, clz);
                    logger.info("POST: {} , cloud: {} , body: {}, cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            body,
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res, clz);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected CompletionStage<ResData> httpPost(String url, String body, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity res = CoreWsClient.post(formattedUrl, body, cloud);
                    logger.info("POST: {} , cloud: {} , body: {}, cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            body,
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected <T> CompletionStage<ResData<T>> httpPut(String url, String body, Class<T> clz, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity<T> res = CoreWsClient.put(formattedUrl, body, cloud, clz);
                    logger.info("PUT: {} , cloud: {} , body: {}, cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            body,
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res, clz);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected CompletionStage<ResData> httpPut(String url, String body, CloudEntity cloud) {
        final long start = System.currentTimeMillis();
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    ResponseEntity res = CoreWsClient.put(formattedUrl, body, cloud);
                    logger.info("PUT: {} , cloud: {} , body: {}, cost time: {}",
                            formattedUrl,
                            cloud.getCloudName(),
                            body,
                            System.currentTimeMillis() - start);
                    if (BAD_REQUEST.value() > res.getStatusCodeValue()) {
                        return ResData.build(res);
                    } else {
                        return ResData.failure(res);
                    }
                });
    }

    protected static JsonNode okFormat(int code, Object data, HttpServletResponse response) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        String method = e.getMethodName().contains("lambda")
                ? e.getMethodName().split("\\$")[1] : e.getMethodName();
        method = "null".equals(method)
                ? "[line num :" + e.getLineNumber() + "]" : method;
        final String log = e.getFileName().replace(".java", "") + "::" + method;
        logger.info("invoke: {}, response code: {}", log, code);
        response.setStatus(code);
        if (null != data) {
            String dateStr = JsonUtil.objectToString(data);
            return JsonUtil.stringToObject(dateStr, JsonNode.class);
        } else {
            return JsonUtil.stringToObject("", JsonNode.class);
        }
    }

    protected static JsonNode badFormat(Throwable e, HttpServletResponse response) {
        final StackTraceElement e1 = Thread.currentThread().getStackTrace()[2];
        String method = e1.getMethodName().contains("lambda")
                ? e1.getMethodName().split("\\$")[1] : e1.getMethodName();
        final String log = e1.getFileName().replace(".java", "") + "::" + method;
        logger.info("invoke: {}, error: {}", log, e);
        return dealThrowable(e, response);
    }

    private static JsonNode dealThrowable(Throwable e, HttpServletResponse response) {
        int code = BAD_REQUEST.value();
        String msg = "";
        if (null != e) {
            //当前线程中自定义异常
            if (e instanceof CoreException) {
                ErrorEnum errorEnum = ((CoreException) e).getErrorEnum();
                msg = JsonUtil.objectToString(errorEnum);
            }
            //其他线程中自定义异常
            if (e.getCause() instanceof CoreException) {
                ErrorEnum errorEnum = ((CoreException) e.getCause()).getErrorEnum();
                msg = JsonUtil.objectToString(errorEnum);
            }
            if (e.getCause() instanceof RestException) {
                code = ((RestException) e.getCause()).getCode();
                msg = ((RestException) e.getCause()).getMessage();
            }
            if (e instanceof RestClientResponseException) {
                code = ((RestClientResponseException) e).getRawStatusCode();
                msg = ((RestClientResponseException) e).getResponseBodyAsString();
            }
            if (e.getCause() instanceof RestClientResponseException) {
                code = ((RestClientResponseException) e.getCause()).getRawStatusCode();
                msg = ((RestClientResponseException) e.getCause()).getResponseBodyAsString();
            }
            if (e instanceof HttpClientErrorException) {
                code = ((HttpClientErrorException) e).getRawStatusCode();
                msg = ((HttpClientErrorException) e).getResponseBodyAsString();
            }
            if (e.getCause() instanceof HttpClientErrorException) {
                code = ((HttpClientErrorException) e.getCause()).getRawStatusCode();
                msg = ((HttpClientErrorException) e.getCause()).getResponseBodyAsString();
            }
        }
        response.setStatus(code);
        return JsonUtil.stringToObject(msg, JsonNode.class);
    }

    protected <T> T dealException(Throwable e, CloudEntity cloud) {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        logger.error("find multi clouds result error, occur in :{}", trace[2].getFileName()
                + " lineNum: " + trace[2].getLineNumber());
        logger.error("find multi clouds result error, e :{}, cloud: {}", e.getCause(), cloud.getCloudName());
        return null;
    }

    protected static void addCloudInfo(Object obj, CloudEntity cloud) {
        if (null == obj) {
            return;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            for (Object o : list) {
                execInsertCloud(o, cloud);
            }
        } else {
            execInsertCloud(obj, cloud);
        }
    }

    private static void execInsertCloud(Object obj, CloudEntity cloud) {
        try {
            Class clz = obj.getClass();

            Field idFiled = clz.getDeclaredField("cloudId");
            idFiled.setAccessible(true);
            idFiled.set(obj, cloud.getCloudId());

            Field nameField = clz.getDeclaredField("cloudName");
            nameField.setAccessible(true);
            nameField.set(obj, cloud.getCloudName());

            Field typeField = clz.getDeclaredField("cloudType");
            typeField.setAccessible(true);
            typeField.set(obj, cloud.getCloudType());
        } catch (Exception e) {
            logger.error("addCloudInfo error -> obj: {}" + obj.getClass());
        }
    }

}
