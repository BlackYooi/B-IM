package com.black.bim.handler;

import com.black.bim.im.ImSession;
import com.black.bim.im.exception.NotLoginException;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import com.black.bim.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
@ChannelHandler.Sharable
public class ChatHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.MESSAGE_REQUEST.equals(message.getType());
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage msg) throws Exception {
        delWithChatMsg(ctx, msg);
    }

    private void delWithChatMsg(ChannelHandlerContext ctx, DefaultMessage msg) throws Exception {
        // 是否登录
        BimServerLocalSession session = SessionManager.getInstance().getSession(ctx).orElse(null);
        if (null == session || !session.isLogin()) {
            throw new NotLoginException();
        }
        // 处理消息
        MessageRequest messageRequest = msg.getMessageRequest();
        String toUser = messageRequest.getTo();
        List<ImSession> toSessions = SessionManager.getInstance().getSessionByUserId(toUser);
        if (null == toSessions || toSessions.isEmpty()) {
            // todo 推到消息中兴
        } else {
            toSessions.forEach(s -> s.writeAndFlush(msg));
        }
    }
}
