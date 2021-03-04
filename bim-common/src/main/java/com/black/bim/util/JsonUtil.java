package com.black.bim.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

/**
 * @description：
 * @author：8568
 */
public class JsonUtil {
    public static final Gson GSON = new Gson();

    public static String objectToJson(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T jsonStringToObject(String json, Class<T> targetObject) {
        return JSONObject.parseObject(json, targetObject);
    }
}
