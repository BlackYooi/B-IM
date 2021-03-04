package com.black.bim.handler;

import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.im.protobuf.DefaultProtoMsg;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * 通知消息的处理器
 * @author：8568
 */
@Slf4j
public class NotifyHandler extends AbstractDefaultMsgHandler {
    @Override
    protected Boolean msgCouldProcess(DefaultProtoMsg.ProtoMsg.DefaultMessage message) {
        return message.getType().equals(DefaultProtoMsg.ProtoMsg.HeadType.MESSAGE_NOTIFICATION);
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) throws Exception {
        log.info("通知消息{}", message.getNotification().toString());
    }
}
