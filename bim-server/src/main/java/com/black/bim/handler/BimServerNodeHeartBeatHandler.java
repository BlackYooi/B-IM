package com.black.bim.handler;

import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.im.protobuf.DefaultProtoMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * @description：
 * @deprecated TODO 待完成
 * @author：8568
 */
@Deprecated
public class BimServerNodeHeartBeatHandler extends AbstractDefaultMsgHandler {
    @Override
    protected Boolean msgCouldProcess(DefaultProtoMsg.ProtoMsg.DefaultMessage message) {
        return null;
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) throws Exception {

    }
}
