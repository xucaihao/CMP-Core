package com.cmp.core.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转json字符串
     *
     * @param obj 对象
     * @return json字符串
     */
    public static <T> String objectToString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ?
                    (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Parse object to String error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * string转object
     *
     * @param str json字符串
     * @param clz T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToObject(String str, Class<T> clz) {
        if (StringUtils.isEmpty(str) || clz == null) {
            return null;
        } else {
            try {
                return clz.equals(String.class) ? (T) str : objectMapper.readValue(str, clz);
            } catch (IOException e) {
                logger.error("Parse String to Object error: {}", e);
                return null;
            }
        }
    }

    /**
     * string转object
     *
     * @param str           json字符串
     * @param typeReference 被转对象引用类型
     * @param <T>           T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToGenericObject(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            System.out.println("Parse String to Object error");
            e.printStackTrace();
            return null;
        }
    }

}
