package com.cmp.core.common;

public enum ErrorEnum {

    //##########公共错误code##########
    ERR_SYS_TOKEN_NOT_FOUND(1, "cloudmp.core.commom.tokennotfound.error", "请求头中token不存在"),
    ERR_CLOUD_NOT_FOUND(2, "cloudmp.core.common.cloudnotfound.error", "云实体不存在"),
    ERR_USER_MAPPING_NOT_FOUND(3, "cloudmp.core.common.usermappingnotfound.error", "没有找到用户映射关系"),
    ERR_USER_TOKEN_EXPIRED(4, "cloudmp.core.com.common.usertokenexpired.error", "用户token过期"),
    ERR_CLOUD_ID_NOT_FOUND(5, "cloudmp.core.commom.cloudidnotfound.error", "请求头中cloudId不存在"),
    ERR_DEFAULT_CODE(0, "cloudmp.core.cloud.unknown.error","未知错误");


    private int code;

    private String message;

    private String desc;

    ErrorEnum(int code, String message, String desc) {
        this.code = code;
        this.message = message;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "code=" + code +
                ", message='" + message + '\'' +
                ", desc='" + desc;
    }
}
