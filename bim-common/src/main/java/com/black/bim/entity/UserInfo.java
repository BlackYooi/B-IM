package com.black.bim.entity;

/**
 * @description：用户
 * @author：8568
 */
public interface UserInfo {

    /**
     * Description: 用户唯一身份标识
     *
     * @param
     * @return: java.lang.String
    */
    public String getUid();

    /**
     * Description: 登录账号
     *
     * @param
     * @return: java.lang.String
    */
    public String getUserName();


    /**
     * Description: 登录密码
     *
     * @param
     * @return: java.lang.String
    */
    public String getPassword();
}
