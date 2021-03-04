package com.black.bim.im;

import com.black.bim.entity.UserInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description：通讯框架的会话
 * @author：8568
 */
public abstract class ImSession implements Serializable {

    private static final long serialVersionUID = 48990157661414433L;

    @Setter
    protected UserInfo user;

    /**
     * 是否建立连接（不代表已经登录）
     */
    @Getter
    @Setter
    protected boolean isConnected = false;

    /**
     * 是否登录
     */
    @Setter
    @Getter
    protected boolean isLogin = false;

    /**
     * Description:
     * 发送消息
     * @param pkg
     * @return: void
     */
    public abstract void writeAndFlush(Object pkg);

    /**
     * Description:
     * 获取会话id
     * @param
     * @return: java.lang.String
     */
    public abstract String getSessionId();


    /**
     * 获取用户
     * @return  用户
     */
    public abstract UserInfo getUser();

    /**
     * Description: 关闭seesion
     *
     * @param
     * @return: void
    */
    public abstract void close();

    /**
     * sessionId生成
     */
    protected String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
}
