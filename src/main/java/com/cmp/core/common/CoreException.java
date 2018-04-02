package com.cmp.core.common;

import static com.cmp.core.common.ErrorEnum.ERR_DEFAULT_CODE;

public class CoreException extends RuntimeException {

    private ErrorEnum errorEnum;

    public CoreException(ErrorEnum errorEnum) {
        super(errorEnum.getDesc());
        this.errorEnum = errorEnum;
    }

    public static CoreException failure() {
        return new CoreException(ERR_DEFAULT_CODE);
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public void setErrorEnum(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }
}
