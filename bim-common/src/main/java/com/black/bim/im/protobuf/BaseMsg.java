package com.black.bim.im.protobuf;

/**
 * @description：基础的通讯协议
 * @author：8568
 */
public interface BaseMsg {

    /**
     * Description: 必须有获取会话id的功能
     *
     * @param
     * @return: java.lang.String
    */
    String getSessionId();
}
