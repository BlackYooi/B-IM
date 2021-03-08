package com.black.bim.im;


import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.entity.UserInfo;
import com.google.gson.Gson;
import io.netty.channel.ChannelFutureListener;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.black.bim.im.protobuf.DefaultMsgBuilder.buildCommon;
import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public abstract class ImBaseClient implements AutoCloseable {

    /**
     * 保存了连接的会话
    */
    @Getter
    @Setter
    protected ImSession session;

    /**
     * Description: 连接到服务器
     *
     * @param
     * @return: void
    */
    protected abstract boolean connectToServer();

    /**
     * Description: 同步登录服务器
     *
     * @param u 登录信息
     * @return: boolean
    */
    public boolean login(UserInfo u) {
        boolean result = false;
        if (connectToServer()) {
            result = doLogin(u);
        }
        if (false == result) {
            Try.run(() -> close());
        }
        return result;
    }

    private boolean doLogin(UserInfo u) {
        boolean loginSuccess = false;
        session.setUser(u);
        DefaultMessage loginMsg = ofLoginMsg(session);
        if (sendMsgSync(loginMsg)) {
            int i = 0;
            // 等待收到的登录响应包改变session状态
            do {
                if (session.isLogin()) {
                    log.info("登录成功");
                    return true;
                }
                Try.run(() -> TimeUnit.SECONDS.sleep(1));
                i ++;
            } while (i < 5);
        }
        log.info("登录失败");
        return loginSuccess;
    }

    /**
     * Description: 异步向服务器发送消息、必须实现自己的回调逻辑
     *
     * @param message
     * @return:
    */
    public abstract void sendMsg(DefaultMessage message, ChannelFutureListener f);

    /**
     * Description: 同步方式发送消息给服务器
     *
     * @param message
     * @return: java.lang.Boolean
    */
    public abstract boolean sendMsgSync(DefaultMessage message);

    /**
     * Description: 关闭客户端
     *
     * @param
     * @return: void
    */
    protected abstract void closeClient() throws Exception;

    @Override
    public void close() throws Exception {
        closeClient();
    }

    private DefaultMessage ofLoginMsg(ImSession session) {
        BimCommonConfig config = BimConfigFactory.getConfig(BimCommonConfig.class);
        DefaultMessage message = buildCommon(-1, HeadType.LOGIN_REQUEST, session);
        UserInfo u = session.getUser();
        LoginRequest black = LoginRequest.newBuilder()
                .setAppVersion(String.valueOf(config.getVersionNumber()))
                .setUid(u.getUid())
                .setJson(new Gson().toJson(u))
                .build();
        return message.toBuilder().setLoginRequest(black).build();
    }
}
