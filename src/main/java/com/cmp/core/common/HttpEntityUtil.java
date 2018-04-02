package com.cmp.core.common;

import com.cmp.core.cloud.entity.CloudEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static com.cmp.core.common.Constance.HEADER_CLOUD_INFO;
import static com.cmp.core.common.Constance.HTTP_MSG_TYPE;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class HttpEntityUtil {

    private HttpHeaders headers;

    /**
     * 初始换http请求头部
     */
    public HttpEntityUtil() {
        headers.set(ACCEPT, HTTP_MSG_TYPE);
        headers.set(CONTENT_TYPE, HTTP_MSG_TYPE);
    }

    /**
     * 初始换http请求头部
     *
     * @param cloud 云
     */
    public HttpEntityUtil(CloudEntity cloud) {
        headers.set(ACCEPT, HTTP_MSG_TYPE);
        headers.set(CONTENT_TYPE, HTTP_MSG_TYPE);
        if (null != cloud) {
            headers.set(HEADER_CLOUD_INFO, cloud.toString());
        }
    }


    /**
     * 自定义http请求头部
     *
     * @param headerMap 消息头部参数
     * @return this
     */
    public HttpEntityUtil addHeaderAllValue(Map<String, String> headerMap) {
        headers.setAll(headerMap);
        return this;
    }

    /**
     * 构建HttpEntity
     *
     * @return HttpEntity
     */
    public HttpEntity<String> buildHttpEntity() {
        return new HttpEntity<String>(headers);
    }

    /**
     * 构建HttpEntity
     *
     * @param body 请求体
     * @return HttpEntity
     */
    public HttpEntity<String> buildHttpEntity(String body) {
        return new HttpEntity<String>(body, headers);
    }

    /**
     * 构建HttpEntity
     *
     * @param body 请求体
     * @return HttpEntity
     */
    public HttpEntity<MultiValueMap<String, Object>> buildHttpEntity(MultiValueMap<String, Object> body) {
        return new HttpEntity<MultiValueMap<String, Object>>(body, headers);
    }
}
