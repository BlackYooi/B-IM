package com.black.bim.handler;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.im.handler.IdleAbstractDefaultMsgHandler;
import com.black.bim.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class BimHearBeatServerHandler extends IdleAbstractDefaultMsgHandler {

    private static BimCommonConfig config = BimConfigFactory.getConfig(BimCommonConfig.class);

    /**
     * 记录未收到服务心跳响应的次数
     */
    private AtomicInteger noResponseTime = new AtomicInteger(0);

    public BimHearBeatServerHandler(long heartBeatInterval) {
        super(heartBeatInterval, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.KEEPALIVE_REQUEST.equals(message.getType());
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        // 回写
        ctx.executor().submit(() -> {
            if (ctx.channel().isActive()) {
                DefaultMessage responseMsg = DefaultMessage.newBuilder()
                        .mergeFrom(message)
                        .setType(HeadType.KEEPALIVE_RESPONSE)
                        .build();
                ctx.writeAndFlush(responseMsg);
            }
        });
        // 必须交给下一站、心跳才有效、不然一直读超时
        topChanelRead(ctx, message);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        switch (evt.state()) {
            case READER_IDLE:
                whileNoPong(ctx);
                break;
            default:break;
        }
    }

    private void whileNoPong(ChannelHandlerContext ctx) {
        log.error("未收到心跳请求， 次数{}", noResponseTime.incrementAndGet());
        if (noResponseTime.get() >= 3) {
            log.error("连续三次未收到心跳请求、关闭连接");
            noResponseTime.set(0);
            SessionManager.getInstance().closeSession(ctx);
        }
    }
}
