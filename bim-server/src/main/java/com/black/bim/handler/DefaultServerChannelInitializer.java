package com.black.bim.handler;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.im.codec.DefaultMsgDecoder;
import com.black.bim.im.codec.DefaultMsgEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @description：
 * @author：8568
 */
public class DefaultServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    BimLoginRequestHandler loginRequestHandler = new BimLoginRequestHandler();
    BimServerChatHandler chatHandler = new BimServerChatHandler();
    BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 解码器
        ch.pipeline().addLast("decoder", new DefaultMsgDecoder());
        // 编码器
        ch.pipeline().addLast("encode", new DefaultMsgEncoder());
        // 心跳
        ch.pipeline().addLast("heartBeat", new BimHearBeatServerHandler(Long.valueOf(commonConfig.getHeartBeatInterval())));
        // 权限验证
        ch.pipeline().addLast("check", new BimCheckUserHandler());
        // 处理登录
        ch.pipeline().addLast("login", loginRequestHandler);
        // 通知消息处理器
        ch.pipeline().addLast("notify", new BimNotifyHandler());
        // 消息处理
        ch.pipeline().addLast("chat", this.chatHandler);
        // 异常处理
        ch.pipeline().addLast("exception", new BimServerExceptionHandler());
    }
}
