package com.black.bim.im.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;

/**
 * @description：消息处理器抽象类；子类实现不同消息类型的处理逻辑、或者相同消息的不同处理逻辑
 * @author：8568
 */
public abstract class AbstractDefaultMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (notMsg(msg)) {
            super.channelRead(ctx, msg);
            return;
        }
        DefaultMessage message = (DefaultMessage) msg;
        if (msgCouldProcess(message)) {
            processMsg(ctx, message);
        } else {
            // 不符合子类胃口的消息类型直接给下一站处理
            super.channelRead(ctx, msg);
            return;
        }
    }

    private boolean notMsg(Object msg) {
        return null == msg || !(msg instanceof DefaultMessage);
    }

    /**
     * 返回消息类型是否是自己干兴趣的消息类型
     */
    protected abstract Boolean msgCouldProcess(DefaultMessage message);

    /**
     * Description: 不同消息类型的处理逻辑、或者相同消息的不同处理逻辑; 需要子类做具体实现
    */
    protected abstract void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception;
}
