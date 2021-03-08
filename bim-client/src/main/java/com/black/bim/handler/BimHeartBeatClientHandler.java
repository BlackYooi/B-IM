package com.black.bim.handler;


import com.black.bim.client.BimClientSession;
import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.exception.ServerCanNotAvailableException;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.potobuf.DefaultClientMsgBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;

/**
 * @description：客户端心跳处理器
 * @author：8568
 */
public class BimHeartBeatClientHandler extends AbstractDefaultMsgHandler {

    BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return message.getType().equals(HeadType.KEEPALIVE_RESPONSE);
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        HeadType type = message.getType();
        if (type.equals(HeadType.KEEPALIVE_RESPONSE)) {
            // TODO
//            log.info("❤pong❤");
            return;
        } else {
            // 交到下一站处理
            super.channelRead(ctx, message);
        }
    }

    /**
     * 监听此处理器被添加事件、当被加入Pipeline时就开始发送心跳
    */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        BimClientSession session = BimClientSession.getSession(ctx);
        UserInfo user = session.getUser();
        DefaultMessage message = DefaultClientMsgBuilder.buildHearBeatMsg(session);
        // 发送心跳
        hearBeat(ctx, message);
    }

    private void hearBeat(ChannelHandlerContext ctx, DefaultMessage message) {
        ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()) {
                // TODO
//                log.info("❤ping❤");
                ChannelFuture channelFuture = ctx.writeAndFlush(message);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            throw new ServerCanNotAvailableException();
                        }
                    }
                });
            }
        }, commonConfig.getHeartBeatInterval(), commonConfig.getHeartBeatInterval(), TimeUnit.SECONDS);
    }
}
