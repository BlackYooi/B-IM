package com.black.bim.handler;

import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import io.netty.channel.ChannelHandlerContext;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;

/**
 * @description：im异常处理器
 * @author：8568
 */
public class BimClientExceptionHandler extends AbstractDefaultMsgHandler {

    /**
     * 异常处理、这里是日志打印
    */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    /**
     * 处理需要丢弃的包、不处理的话日志会一直打印这些未被处理的包
     * 对心跳包忽略、其他包保留netty的警告日志
    */
    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return message.getType().equals(HeadType.KEEPALIVE_REQUEST)
                || message.getType().equals(HeadType.KEEPALIVE_RESPONSE);
    }

    /**
     * 处理需要丢弃的包的处理方式就是 不处理 0.0！
    */
    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        return;
    }
}