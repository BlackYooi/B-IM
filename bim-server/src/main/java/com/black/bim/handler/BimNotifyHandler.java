package com.black.bim.handler;

import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.im.protobuf.DefaultProtoMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * @description：
 * 通知消息的处理器
 * @author：8568
 */
public class BimNotifyHandler extends AbstractDefaultMsgHandler {
    @Override
    protected Boolean msgCouldProcess(DefaultProtoMsg.ProtoMsg.DefaultMessage message) {
        return message.getType().equals(DefaultProtoMsg.ProtoMsg.HeadType.MESSAGE_NOTIFICATION);
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) throws Exception {
    }
}
