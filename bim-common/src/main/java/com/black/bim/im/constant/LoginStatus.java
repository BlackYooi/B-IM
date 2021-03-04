package com.black.bim.im.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @description：登录状态
 * @author：8568
 */
public enum LoginStatus {

    // 登录成功
    SUCCESS(0, "Success", true),
    // 登录失败
    AUTH_FAILED(1, "账号或者密码错误", false),
    // 无授权码
    NO_TOKEN(2, "没有授权码", false),
    // 未知错误
    UNKNOW_ERROR(500, "未知错误", false),
    ;

    private Integer code;
    private String desc;
    private boolean isLogin;

    LoginStatus(Integer code, String desc, boolean isLogin) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public static LoginStatus getByCode(Integer code) {
        LoginStatus result = cached.get(code);
        result = result == null ? UNKNOW_ERROR : result;
        return result;
    }

    private static Map<Integer, LoginStatus> cached = new HashMap<>();

    static {
        for (LoginStatus l : LoginStatus.values()) {
            cached.put(l.getCode(), l);
        }
    }
}
