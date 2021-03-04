package com.black.bim.entity;

import com.alibaba.fastjson.JSON;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * UserInfo的默认实现
 * @author：8568
 */
public class DefaultUserInfo implements UserInfo {

    private String uid = "";
    private String userName = "";
    private String password = "";

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public static UserInfo fromMsg(LoginRequest message) {
        String other = message.getJson();
        UserInfo u = JSON.parseObject(other, DefaultUserInfo.class);
        return u;
    }

    public DefaultUserInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public DefaultUserInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public DefaultUserInfo setPassword(String password) {
        this.password = password;
        return this;
    }
}
