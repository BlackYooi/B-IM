package com.black.bim.handler;

import com.black.bim.im.ImSession;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;
import com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.MessageRequest;
import com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.MsgType;
import com.black.bim.session.SessionManager;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import com.black.bim.session.sessionImpl.BimServerNodeSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @description：
 * @author：8568
 */
@ChannelHandler.Sharable
@Slf4j
public class BimServerChatHandler extends AbstractDefaultMsgHandler {

    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.MESSAGE_REQUEST.equals(message.getType());
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage msg) throws Exception {
        delWithChatMsg(ctx, msg);
    }

    private void delWithChatMsg(ChannelHandlerContext ctx, DefaultMessage msg) throws Exception {
        // 发送消息
        switch (msg.getMessageRequest().getMsgType()) {
            case CHAT_MSG:
                sendMsg(msg);
                return;
            case CHAT_MSG_COMMISSION:
                sendCommissionMsg(msg);
                return;
            default: log.warn("未处理的消息类型{}", msg.getMessageRequest().toString());
        }
    }

    private void sendMsg(DefaultMessage msg) {
        MessageRequest messageRequest = msg.getMessageRequest();
        String toUser = messageRequest.getTo();
        List<ImSession> toSessions = sessionManager.getSessionByUserId(toUser);
        if (null == toSessions || toSessions.isEmpty()) {
            // 目标用户不在线
            log.warn("未处理的消息类型{}", msg.getMessageRequest().toString());
            // todo 推到消息中兴
            return;
        } else {
            MessageRequest.Builder body = MessageRequest.newBuilder()
                    .mergeFrom(msg.getMessageRequest())
                    .setMsgType(MsgType.CHAT_MSG_COMMISSION);
            for (ImSession session : toSessions) {
                if (session instanceof BimServerLocalSession) {
                    session.writeAndFlush(msg);
                }
                if (session instanceof BimServerNodeSession) {
                    body.setAcceptSessionId(session.getSessionId());
                    DefaultMessage newMsg = DefaultMessage.newBuilder().mergeFrom(msg)
                            .setMessageRequest(body)
                            .build();
                    session.writeAndFlush(newMsg);
                }
            }
        }
    }

    private void sendCommissionMsg(DefaultMessage msg) {
        String toSessionId = msg.getMessageRequest().getAcceptSessionId();
        Optional<BimServerLocalSession> localSessionById = sessionManager.getLocalSessionById(toSessionId);
        if (localSessionById.isPresent()) {
            localSessionById.get().writeAndFlush(msg);
        }
    }
}
