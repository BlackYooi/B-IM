package com.black.bim.session.sessionImpl;

import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.black.bim.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * 指的时存放在节点上的session
 * @author：8568
 */
@Slf4j
public class BimServerLocalSession extends ImSession {

    public static final AttributeKey<BimServerLocalSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");

    /**
     * 通道
     */
    protected Channel channel;

    /**
     * 登录过后的sessionId
     */
    protected String sessionId;

    public BimServerLocalSession(Channel c) {
        channel = c;
        sessionId = buildNewSessionId();
    }

    /**
     * 用户登录成功时绑定会话
    */
    public BimServerLocalSession bind() {
        channel.attr(BimServerLocalSession.SESSION_KEY).set(this);
        setLogin(true);
        SessionManager instance = SessionManager.getInstance();
        instance.addSession(this);
        return this;
    }

    /**
     * 写数据包给客户
     */
    @Override
    public void writeAndFlush(Object pkg) {
       channel.writeAndFlush(pkg);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public UserInfo getUser() {
        return user;
    }

    public synchronized void close() {
        SessionManager.getInstance().closeSession(sessionId, user.getUid());
    }

    @Override
    public String toString() {
        return String.format("\nsessionId: %s" +
                "\nchannel:%s", sessionId, channel.id());
    }
}
