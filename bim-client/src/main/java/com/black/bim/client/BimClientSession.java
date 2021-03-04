package com.black.bim.client;

import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：客户端的会话
 * @author：8568
 */
@Slf4j
public class BimClientSession extends ImSession {

    public static final AttributeKey<ImSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /**
     * 变量属性值
    */
    private Map<String, Object> map = new HashMap<>();

    @Getter
    private Channel channel;

    @Setter
    private String sessionId;

    /**
     * 绑定通道的构造函数
    */
    public BimClientSession(Channel channel) {
        this.channel = channel;
        this.sessionId = String.valueOf("-1");
        channel.attr(BimClientSession.SESSION_KEY).set(this);
    }

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

    /**
     * 登录成功设置sessionId
    */
    public static void loginSuccess(ChannelHandlerContext ctx, DefaultMessage message) {
        Channel channel = ctx.channel();
        BimClientSession clientSession = (BimClientSession) channel.attr(BimClientSession.SESSION_KEY).get();
        clientSession.setSessionId(message.getSessionId());
        clientSession.setLogin(true);
    }

    /**
     * 获取会话
    */
    public static BimClientSession getSession(ChannelHandlerContext ctx) {
        return (BimClientSession) ctx.channel().attr(BimClientSession.SESSION_KEY).get();
    }

    /**
     * 关闭
    */
    @Override
    public void close() {
        isConnected = false;
        super.setLogin(false);
        ChannelFuture close = channel.close();
        close.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (close.isSuccess()) {
                    log.info("close success");
                }
            }
        });
    }
}
