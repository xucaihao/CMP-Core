package com.cmp.core.common;

import com.cmp.core.cloud.entity.CloudEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletionStage;

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

    protected <T> List<T> aggregateList(final List<List<T>> lists) {
        return baseHelper.aggregateList(lists);
    }

    protected CompletionStage<CloudEntity> describeCloudEntity(HttpServletRequest request) {
        return baseHelper.getCloudEntity(request);
    }

    protected <T> CompletionStage<ResponseEntity<T>> httpGet(String url, Class<T> clz, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("GET: {} cloud: {}", formattedUrl, cloud.getCloudName());
                    return CoreWsClient.get(formattedUrl, cloud, clz);
                });
    }

    protected CompletionStage<ResponseEntity> httpDel(String url, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("DELETE: {} cloud: {}", formattedUrl, cloud.getCloudName());
                    return CoreWsClient.delete(formattedUrl, cloud);
                });
    }

    protected <T> CompletionStage<ResponseEntity<T>> httpPost(String url, String body, Class<T> clz, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("POST: {} cloud: {} body: {}", formattedUrl, cloud.getCloudName(), body);
                    return CoreWsClient.post(formattedUrl, body, cloud, clz);
                });
    }

    protected CompletionStage<ResponseEntity> httpPost(String url, String body, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("POST: {} cloud: {} body: {}", formattedUrl, cloud.getCloudName(), body);
                    return CoreWsClient.post(formattedUrl, body, cloud, null);
                });
    }

    protected <T> CompletionStage<ResponseEntity<T>> httpPut(String url, String body, Class<T> clz, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("PUT: {} cloud: {} body: {}", formattedUrl, cloud.getCloudName(), body);
                    return CoreWsClient.put(formattedUrl, body, cloud, clz);
                });
    }

    protected CompletionStage<ResponseEntity> httpPut(String url, String body, CloudEntity cloud) {
        return baseHelper.formatUrl(url, cloud)
                .thenApply(formattedUrl -> {
                    logger.info("PUT: {} cloud: {} body: {}", formattedUrl, cloud.getCloudName(), body);
                    return CoreWsClient.put(formattedUrl, body, cloud, null);
                });
    }

    protected <T> T dealException(Throwable e, CloudEntity cloud) {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        logger.error("find multi clouds result error, occur in :{}", trace[2].getFileName()
                + " lineNum: " + trace[2].getLineNumber());
        logger.error("find multi clouds result error, e :{}, cloud: {}", e.getCause(), cloud.getCloudName());
        return null;
    }
}
