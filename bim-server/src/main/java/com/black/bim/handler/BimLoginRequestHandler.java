package com.black.bim.handler;

import com.black.bim.entity.DefaultUserInfo;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.constant.LoginStatus;
import com.black.bim.im.handler.AbstractDefaultMsgHandler;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;


/**
 * @description：登录请求处理器
 * @author：8568
 */
@Slf4j
@ChannelHandler.Sharable
public class BimLoginRequestHandler extends AbstractDefaultMsgHandler {

    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        return HeadType.LOGIN_REQUEST.equals(message.getType());
    }

    @Override
    public void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        // 创建session
        BimServerLocalSession serverLocalSession = new BimServerLocalSession(ctx.channel());
        // 处理登录逻辑
        ctx.executor().submit(() -> login(serverLocalSession, message));
    }

    public boolean login(BimServerLocalSession session, DefaultMessage message) {
        boolean result = false;
        LoginRequest info = message.getLoginRequest();
        long seqNo = message.getSequence();
        UserInfo u = DefaultUserInfo.fromMsg(info);
        // 构造响应报文
        DefaultMessage msg = DefaultMessage.newBuilder()
                .setType(HeadType.LOGIN_RESPONSE)
                .setSessionId("-1")
                .setSequence(seqNo)
                .build();
        // 用户校验
        LoginStatus statue = checkUser(u);
        LoginResponse.Builder responseBody = buildLoginResponse(statue);
        // 回复客户端登录结果
        DefaultMessage.Builder responseMsg = DefaultMessage.newBuilder()
                .mergeFrom(msg)
                .setLoginResponse(responseBody);
        if (LoginStatus.SUCCESS.equals(statue)) {
            session.setUser(u);
            session.bind();
            result = true;
            session.writeAndFlush(responseMsg.setSessionId(session.getSessionId()).build());
            log.info("用户【{}】登录成功", u.getUid());
        } else {
            session.writeAndFlush(responseMsg.build());
            session.close();
            log.info("用户【{}】登录失败", u.getUid());
        }
        return result;
    }

    private LoginResponse.Builder buildLoginResponse(LoginStatus status) {
        LoginResponse.Builder response = LoginResponse.newBuilder()
                .setCode(status.getCode())
                .setInfo(status.getDesc())
                .setExpose(1);
        return response;
    }

    private LoginStatus checkUser(UserInfo user) {
        if (!user.getPassword().equals(user.getUserName())) {
            return LoginStatus.AUTH_FAILED;
        }
        // token
        //校验用户,比较耗时的操作,需要100 ms以上的时间 TODO
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验
        return LoginStatus.SUCCESS;

    }
}
