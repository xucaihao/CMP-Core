package com.cmp.core.common;

public enum ErrorEnum {

    //##########公共错误code(0——50)##########
    ERR_SYS_TOKEN_NOT_FOUND(1, "cloudmp.core.common.tokenNotFoundError", "请求头中token不存在"),
    ERR_USER_TOKEN_EXPIRED(2, "cloudmp.core.com.common.usertokenexpired.error", "用户token过期"),
    ERR_CLOUD_ID_NOT_FOUND(3, "cloudmp.core.common.cloudidnotfound.error", "请求头中cloudId不存在"),

    //##########cloud错误code(51——100)##########
    ERR_CLOUD_NOT_FOUND(51, "cloudmp.core.cloud.cloudnotfound.error", "云实体不存在"),
    ERR_CLOUD_ADAPTER_NOT_FOUND(52, "cloudmp.core.cloud.cloudadapternotfound.error", "没有找到云适配组件"),
    ERR_CLOUD_TYPE_NOT_FOUND(53, "cloudmp.core.cloud.cloudtypenotfound.error", "没有找到可对接云平台类型"),
    ERR_CREATE_CLOUD_BODY(54, "cloudmp.core.cloud.createCloudBodyError", "纳管云请求体错误"),
    ERR_REPEATED_CLOUD_NAME(55, "cloudmp.core.cloud.repeatedCloudNameError", "云名称重复"),
    ERR_REPEATED_CLOUD_IP(56, "cloudmp.core.cloud.repeatedCloudIpError", "云ip重复"),
    ERR_CLOUD_CONNECT_FAIL(57, "cloudmp.core.cloud.connectCloudFailError", "云连接失败"),
    ERR_MODIFY_CLOUD_BODY(58, "cloudmp.core.cloud.modifyCloudBodyError", "修改云请求体错误"),
    ERR_UPDATE_CLOUD(59, "cloudmp.core.cloud.updateCloudError", "更新云失败"),
    ERR_DELETE_CLOUD(60, "cloudmp.core.cloud.deleteCloudError", "删除云失败"),
    ERR_ADD_CLOUD(61, "cloudmp.core.cloud.addCloudError", "添加云失败"),
    ERR_MODIFY_CLOUD_TYPE_BODY(62, "cloudmp.core.cloud.modifyCloudTypeBodyError", "修改云类型请求体错误"),
    ERR_UPDATE_CLOUD_TYPE(63, "cloudmp.core.cloud.updateCloudTypeError", "更新云类型失败"),
    ERR_MODIFY_CLOUD_ADAPTER_BODY(64, "cloudmp.core.cloud.modifyCloudAdapterBodyError", "修改云适配组件请求体错误"),
    ERR_REPEATED_CLOUD_ADAPTER_IP(65, "cloudmp.core.cloud.repeatedCloudAdapterIpError.error", "云适配组件路由地址重复"),
    ERR_CLOUD_ADAPTER_CONNECT_FAIL(66, "cloudmp.core.cloud.connectCloudAdapterFailError", "云连接失败"),
    ERR_UPDATE_CLOUD_ADAPTER(67, "cloudmp.core.cloud.updateCloudAdapterError", "更新云适配器失败"),

    //##########user错误code(101——150)##########
    ERR_CMP_USER_NOT_FOUND(101, "cloudmp.core.user.cmpUserNotFoundError", "没有找到用户"),
    ERR_USER_MAPPING_NOT_FOUND(102, "cloudmp.core.user.userMappingNotFoundError", "没有找到用户映射关系"),
    ERR_DELETE_USER_MAPPING(103, "cloudmp.core.user.deleteUserMappingError", "删除用户映射关系失败"),
    ERR_REGISTER_USER_BODY(104, "cloudmp.core.user.registerUserBodyError", "注册用户请求体错误"),
    ERR_REPEATED_USER_NAME(105, "cloudmp.core.user.repeatedUserNameError", "用户名重复"),
    ERR_REGISTER_USER(106, "cloudmp.core.user.registerUserError", "注册用户失败"),
    ERR_ADD_MAPPING_BODY(107, "cloudmp.core.user.addUserMappingBodyError", "添加用户映射关系请求体错误"),
    ERR_REPEATED_MAPPING(108, "cloudmp.core.user.repeatedUserMappingError", "该用户在此云上已添加用户映射"),
    ERR_ADD_MAPPING(109, "cloudmp.core.user.addUserMappingError", "添加用户映射失败"),
    ERR_USER_AUTHENTICATE(110, "cloudmp.core.user.cloudUserAuthenticateError", "云账号认证失败"),
    ERR_UPDATE_MAPPING_BODY(111, "cloudmp.core.user.updateUserMappingBodyError", "修改用户映射关系请求体错误"),
    ERR_UPDATE_MAPPING(112, "cloudmp.core.user.updateUserMappingError", "修改用户映射失败"),
    ERR_UPDATE_USER_BODY(113, "cloudmp.core.user.updateUserBodyError", "修改用户请求体错误"),
    ERR_UPDATE_USER(114, "cloudmp.core.user.updateUserError", "修改用户失败"),
    ERR_DELETE_USER(115, "cloudmp.core.user.deleteUserError", "删除用户失败"),

    //##########instance错误code(151——200)##########
    ERR_CLOSE_INSTANCE_BODY(151, "cloudmp.core.instance.closeInstanceBodyError", "关闭主机请求体错误"),
    ERR_START_INSTANCE_BODY(152, "cloudmp.core.instance.startInstanceBodyError", "启动主机请求体错误"),
    ERR_MODIFY_INSTANCE_NAME_BODY(153, "cloudmp.core.instance.modifyInstanceNameBodyError", "修改主机名称请求体错误"),

    ERR_DEFAULT_CODE(0, "cloudmp.core.cloud.unknownError", "未知错误");


    private int errorCode;

    private String message;

    private String desc;

    ErrorEnum(int errorCode, String message, String desc) {
        this.errorCode = errorCode;
        this.message = message;
        this.desc = desc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"errorCode\" : \"" + errorCode + "\",\n" +
                "\"msg\" : \"" + message + "\",\n" +
                "\"des\" : \"" + desc + "\"\n" +
                "}";
    }
}
