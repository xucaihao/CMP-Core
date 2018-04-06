package com.cmp.core.common;

import com.cmp.core.cloud.model.CloudEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {

    /**
     * rest服务
     */
    private static final RestTemplate restTemplate = new RestTemplate();

    static <T> ResponseEntity<T> get(String url, CloudEntity cloud, Class<T> clz, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity();
        return restTemplate.exchange(url, HttpMethod.GET, entity, clz);
    }

    static ResponseEntity delete(String url, CloudEntity cloud, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity();
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    static <T> ResponseEntity<T> post(String url, String body, CloudEntity cloud, Class<T> clz, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity(body);
        return restTemplate.exchange(url, HttpMethod.POST, entity, clz);
    }

    static ResponseEntity post(String url, String body, CloudEntity cloud, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity(body);
        return restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    static <T> ResponseEntity<T> put(String url, String body, CloudEntity cloud, Class<T> clz, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity(body);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, clz);
    }

    static ResponseEntity put(String url, String body, CloudEntity cloud, Map<String, String> headerMap) {
        HttpEntityUtil httpEntityUtil = new HttpEntityUtil(cloud);
        if (null != headerMap) {
            httpEntityUtil = httpEntityUtil.addHeaderAllValue(headerMap);
        }
        HttpEntity<String> entity = httpEntityUtil.buildHttpEntity(body);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }
}
