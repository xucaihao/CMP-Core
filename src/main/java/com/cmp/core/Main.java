package com.cmp.core;

import com.cmp.core.common.ErrorEnum;
import com.cmp.core.common.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

public class Main {

    public static void main(String[] args) {
//        String s = (String) ErrorEnum.ERR_CLOUD_NOT_FOUND;
        A a = new A();
        a.setCode(200);
        a.setMsg("aa");
        a.setDes("aa");
        String s = JsonUtil.objectToString(a);
        JsonNode jsonNode = JsonUtil.stringToObject(s, JsonNode.class);
        return;
    }

    static class A {
        private int code;
        private String msg;
        private String des;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
