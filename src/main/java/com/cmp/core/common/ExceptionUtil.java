package com.cmp.core.common;

public class ExceptionUtil {

    protected static void dealException(Throwable e) {
        //当前线程中自定义异常
        if (e instanceof CoreException) {
            throw (CoreException) e;
        }
        //其他线程中自定义异常
        if (e.getCause() instanceof CoreException) {
            throw (CoreException) e.getCause();
        }
        if (e instanceof RestException) {
            throw (RestException) e;
        }
        if (e.getCause() instanceof RestException) {
            throw (RestException) e.getCause();
        }
        throw (RuntimeException) e;
    }


}
