package com.cmp.core.common;

import com.cmp.core.cloud.entity.CloudEntity;
import org.springframework.http.ResponseEntity;

public class CoreWsClient extends BaseClient {

    static public <T> ResponseEntity<T> get(String url, CloudEntity cloud, Class<T> clz) {
        return get(url, cloud, clz, null);
    }

    static ResponseEntity delete(String url, CloudEntity cloud) {
        return delete(url, cloud, null);
    }

    static <T> ResponseEntity<T> post(String url, String body, CloudEntity cloud, Class<T> clz) {
        return post(url, body, cloud, clz, null);
    }

    static <T> ResponseEntity<T> put(String url, String body, CloudEntity cloud, Class<T> clz) {
        return put(url, body, cloud, clz, null);
    }
}
