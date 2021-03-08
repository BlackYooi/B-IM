package com.black.bim.handler;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimServerConfig;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.im.protobuf.DefaultProtoMsg;
import com.black.bim.session.SessionManager;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import io.netty.channel.ChannelHandlerContext;
import io.vavr.control.Try;

import java.util.Optional;

/**
 * @description：
 * 权限校验前置拦截处理器
 * @author：8568
 */
public class BimCheckUserHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultProtoMsg.ProtoMsg.DefaultMessage message) {
        return true;
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultProtoMsg.ProtoMsg.DefaultMessage message) throws Exception {
        if (null == message.getType()) {
            processIllegalMsg(message, ctx);
        }
        switch (message.getType()) {
            case UNRECOGNIZED:
                processIllegalMsg(message, ctx);
                break;
            case LOGIN_REQUEST:
                topChanelRead(ctx, message);
                break;
            case MESSAGE_REQUEST:
                processMsgRq(message, ctx);
                break;
            case KEEPALIVE_REQUEST:
                topChanelRead(ctx, message);
                break;
            case KEEPALIVE_RESPONSE:
                topChanelRead(ctx, message);
                break;
            case MESSAGE_NOTIFICATION:
                processMsgNotify(message, ctx);
                break;
            default: processIllegalMsg(message, ctx);
        }
    }


    private void processMsgRq(DefaultProtoMsg.ProtoMsg.DefaultMessage message, ChannelHandlerContext ctx) {
        DefaultProtoMsg.ProtoMsg.MsgType msgType = message.getMessageRequest().getMsgType();
        if (null == msgType) {
            ctx.writeAndFlush(illegalMsgReturnMsg("位置请求类型"));
            ctx.channel().close();
        }
        String sessionId = null;
        switch (msgType) {
            case CHAT_MSG:
                sessionId = message.getSessionId();
                break;
            case CHAT_MSG_COMMISSION:
                sessionId = message.getMessageRequest().getAcceptSessionId();
                break;
        }
        Optional<BimServerLocalSession> localSessionById = SessionManager.getInstance().getLocalSessionById(sessionId);
        if (null == message.getSessionId() || !localSessionById.isPresent()) {
            ctx.writeAndFlush(illegalMsgReturnMsg("未知消息的发起者或者委托的接收者"));
            ctx.channel().close();
        } else {
            Try.run(() -> topChanelRead(ctx, message));
        }
    }

    private void processMsgNotify(DefaultProtoMsg.ProtoMsg.DefaultMessage message, ChannelHandlerContext ctx) {
        String nodeToken = message.getNotification().getNodeToken();
        BimServerConfig bimServerConfig = BimConfigFactory.getConfig(BimServerConfig.class);
        if (null == nodeToken || !bimServerConfig.getNodeToken().equals(nodeToken)) {
            ctx.writeAndFlush(illegalMsgReturnMsg("节点token不对"));
            ctx.channel().close();
        } else {
            Try.run(() -> topChanelRead(ctx, message));
        }

    }

    private void processIllegalMsg (DefaultProtoMsg.ProtoMsg.DefaultMessage message, ChannelHandlerContext ctx) {
        ctx.writeAndFlush(illegalMsgReturnMsg("error"));
        ctx.channel().close();
    }

    public DefaultProtoMsg.ProtoMsg.DefaultMessage illegalMsgReturnMsg(String des) {
        DefaultProtoMsg.ProtoMsg.MessageResponse messageResponse = DefaultProtoMsg.ProtoMsg.MessageResponse.newBuilder()
                .setCode(0)
                .setInfo("非法消息请求:" + des)
                .setResult(false)
                .setExpose(0)
                .setLastBlock(true)
                .build();
        DefaultProtoMsg.ProtoMsg.DefaultMessage message = DefaultProtoMsg.ProtoMsg.DefaultMessage.newBuilder()
                .setType(DefaultProtoMsg.ProtoMsg.HeadType.MESSAGE_RESPONSE)
                .setMessageResponse(messageResponse)
                .build();
        return message;
    }
}
