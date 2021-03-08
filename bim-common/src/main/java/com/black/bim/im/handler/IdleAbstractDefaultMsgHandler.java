package com.black.bim.im.handler;

import com.black.bim.im.protobuf.DefaultProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @description：
 * @author：8568
 */
public abstract class IdleAbstractDefaultMsgHandler extends IdleStateHandler {

    public IdleAbstractDefaultMsgHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (notMsg(msg)) {
            super.channelRead(ctx, msg);
            return;
        }
        DefaultProtoMsg.ProtoMsg.DefaultMessage message = (DefaultProtoMsg.ProtoMsg.DefaultMessage) msg;
        if (msgCouldProcess(message)) {
            processMsg(ctx, message);
        } else {
            // 不符合子类胃口的消息类型直接给下一站处理
            super.channelRead(ctx, msg);
            return;
        }
    }

    /**
     * Description: 子类如果想转交消息、请调用这个方法、请勿调用 super.channelRead(ctx, message)
     *
     * @param ctx
     * @param msg
     * @return: void
     */
    protected void topChanelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    private boolean notMsg(Object msg) {
        return null == msg || !(msg instanceof DefaultProtoMsg.ProtoMsg.DefaultMessage);
    }

    /**
     * 返回消息类型是否是自己干兴趣的消息类型
     */
    protected abstract Boolean msgCouldProcess(DefaultProtoMsg.ProtoMsg.DefaultMessage message);

    /**
     * Description: 不同消息类型的处理逻辑、或者相同消息的不同处理逻辑; 需要子类做具体实现
     */
    protected abstract void processMsg(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) throws Exception;

}
