package com.gydx.response;

import com.alibaba.fastjson.JSONObject;

public class Response {

    private static JSONObject createRes(int code, String msg, Object data) {
        JSONObject res = new JSONObject();
        res.put("code", code);
        res.put("msg", msg);
        res.put("data", data);
        return res;
    }

    public static JSONObject success(String msg) {
        return success(msg, null);
    }

    public static JSONObject success(String msg, Object data) {
        return createRes(0, msg, data);
    }

    public static JSONObject fail(String msg) {
        return fail(msg, null);
    }

    public static JSONObject fail(String msg, Object data) {
        return createRes(1, msg, data);
    }

}
