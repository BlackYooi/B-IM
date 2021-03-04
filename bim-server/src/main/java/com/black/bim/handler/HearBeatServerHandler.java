package com.black.bim.handler;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.config.configPojo.BimServerConfig;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import com.black.bim.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class HearBeatServerHandler extends IdleStateHandler {

    private static BimCommonConfig config = BimConfigFactory.getConfig(BimCommonConfig.class);

    public HearBeatServerHandler() {
        super(config.getHeartBeatInterval(), 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof DefaultMessage)) {
            super.channelRead(ctx, msg);
            return;
        }
        DefaultMessage m = (DefaultMessage) msg;
        HeadType msgType = m.getType();
        if (HeadType.KEEPALIVE_REQUEST.equals(msgType)) {
            // 回写
            ctx.executor().submit(() -> {
                if (ctx.channel().isActive()) {
                    DefaultMessage responseMsg = m.toBuilder()
                            .setType(HeadType.KEEPALIVE_RESPONSE)
                            .build();
                    ctx.writeAndFlush(responseMsg);
                }
            });
        }
        // 必须交给下一站、心跳才有效、不然一直读超时
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        switch (evt.state()) {
            case READER_IDLE:
//                log.error(config.getHeartBeatInterval() + "秒内未读到数据，关闭连接"); TODO fix
//                Optional<BimServerLocalSession> localSession = SessionManager.getInstance().getSession(ctx);
//                if (localSession.isPresent()) {
//                    localSession.get().close();
//                }
                break;
            default:break;
        }
    }
}
