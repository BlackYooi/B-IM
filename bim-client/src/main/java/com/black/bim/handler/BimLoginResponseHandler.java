package com.black.bim.handler;

import com.black.bim.client.BimClientSession;
import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.im.constant.LoginStatus;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：客户登录响应处理器
 * @author：8568
 */
public class BimLoginResponseHandler extends AbstractDefaultMsgHandler {

    BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.LOGIN_RESPONSE.equals(message.getType());
    }

    @Override
    public void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        if (isLoginSuccess(message)) {
            ChannelPipeline pipeline = ctx.pipeline();
            // 保存会话
            BimClientSession.loginSuccess(ctx, message);
            // 在编码器后面添加心跳处理器
            pipeline.addAfter("encode", "heatBeat", new BimHeartBeatClientHandler(Long.valueOf(commonConfig.getHeartBeatInterval())));
            // 移除登录响应器
            pipeline.remove(this);
        } else {
            BimClientSession clientSession = (BimClientSession) ctx.channel().attr(BimClientSession.SESSION_KEY).get();
        }
    }

    private Boolean isLoginSuccess(DefaultMessage message) {
        LoginResponse loginResponse = message.getLoginResponse();
        LoginStatus status = LoginStatus.getByCode(loginResponse.getCode());
        if (status == LoginStatus.SUCCESS) {
            // 登录成功
            return true;
        } else {
            // 登录失败
            return false;
        }
    }
}
