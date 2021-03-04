package com.black.bim.session.sessionImpl;

import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.black.bim.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @description：
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

    /**
     * session中存储的session 变量属性值
     */
    private Map<String, Object> map = new HashMap<String, Object>();

    public BimServerLocalSession(Channel c) {
        channel = c;
        sessionId = buildNewSessionId();
    }

    /**
     * 用户登录成功时绑定会话
    */
    public BimServerLocalSession bind() {
        log.info(" ServerSession 绑定会话 " + channel.remoteAddress());
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

    @Override
    public synchronized void close() {
        try {
            ChannelFuture close = channel.close().sync();
            if (close.isSuccess()) {
                log.info("close success");
                return;
            }
        } catch (InterruptedException e) {
            log.error(String.format("关闭错误，原因【%s】", e.getMessage()));
        }
        log.error("关闭失败");
    }
}
