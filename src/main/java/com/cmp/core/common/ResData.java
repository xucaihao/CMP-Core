package com.cmp.core.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 统一返回体
 */
public class ResData<T> {

    private static final Logger logger = LoggerFactory.getLogger(ResData.class);

    private int code;

    private T data;

    private String msg;

    public ResData() {
    }

    public ResData(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResData(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    /**
     * 构建成功返回体（带数据）
     *
     * @param res http响应
     * @param clz 返回数据类型
     * @param <T> T
     * @return 成功返回体（带数据）
     */
    @SuppressWarnings("unchecked")
    public static <T> ResData<T> build(ResponseEntity res, Class<T> clz) {
        return ResData.build(res.getStatusCodeValue(), (T) res.getBody());
    }

    /**
     * 构建成功返回体（不带数据）
     *
     * @param res http响应
     * @return 成功返回体（不带数据）
     */
    public static ResData build(ResponseEntity res) {
        return ResData.build(res.getStatusCodeValue(), null);
    }

    public static ResData build(int code, Object data, HttpServletRequest request) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        String method = e.getMethodName().contains("lambda")
                ? e.getMethodName().split("\\$")[1] : e.getMethodName();
        method = "null".equals(method)
                ? "[line num :" + e.getLineNumber() + "]" : method;
        final String log = e.getFileName().replace(".java", "") + "::" + method;
        logger.info("invoke: {}, response code: {}", log, code);
        return ResData.build(code, data);
    }

    /**
     * 构建成功返回体（带数据）
     *
     * @param code 返回体状态码
     * @param t    返回数据
     * @param <T>  T
     * @return 成功返回体（带数据）
     */
    public static <T> ResData<T> build(int code, T t) {
        return new ResData<>(code, t);
    }

    /**
     * 抛出适配层异常
     *
     * @param res http响应
     */
    public static <T> ResData<T> failure(ResponseEntity res) {
        throw new RestException(JsonUtil.objectToString(res.getBody()), res.getStatusCodeValue());
    }

    @SuppressWarnings("unchecked")
    public static ResData failure(int code, String msg) {
        return new ResData(code, msg);
    }

    public static ResData failure(Throwable e) {
        final StackTraceElement e1 = Thread.currentThread().getStackTrace()[2];
        String method = e1.getMethodName().contains("lambda")
                ? e1.getMethodName().split("\\$")[1] : e1.getMethodName();
        final String log = e1.getFileName().replace(".java", "") + "::" + method;
        logger.info("invoke: {}, error: {}", log, e);
        return dealThrowable(e);
    }

    private static ResData dealThrowable(Throwable e) {
        int code = BAD_REQUEST.value();
        String msg = "";
        if (null != e) {
            //当前线程中自定义异常
            if (e instanceof CoreException) {
                ErrorEnum errorEnum = ((CoreException) e).getErrorEnum();
                msg = errorEnum.toString();
            }
            //其他线程中自定义异常
            if (e.getCause() instanceof CoreException) {
                ErrorEnum errorEnum = ((CoreException) e.getCause()).getErrorEnum();
                msg = errorEnum.toString();
            }
            if (e.getCause() instanceof RestException) {
                code = ((RestException) e.getCause()).getCode();
                msg = e.getCause().getMessage();
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
        return ResData.failure(code, msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
