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
     * 用户信息、即时连接断开也不会清空用户信息
    */
    protected UserInfo userInfo;

    /**
     * Description: 连接到服务器
     *
     * @param
     * @return: void
    */
    protected abstract boolean connectToServer();

    /**
     * Description: 断线时调用此方法可以重连
     *
     * @param
     * @return: boolean
    */
    public abstract boolean reConnect();

    /**
     * Description: 同步登录服务器
     *
     * @param u 登录信息
     * @return: boolean
    */
    public boolean login(UserInfo u) {
        boolean result = false;
        userInfo = u;
        if (connectToServer()) {
            session.setUser(u);
            result = doLogin(ofLoginMsg(session));
        }
        if (false == result) {
            log.info("登录失败");
            Try.run(() -> close());
        }
        log.info("登录成功");
        return result;
    }

    /**
     * 该方法用于断线重连、仅在内部调用、userInfo来自与上一次登录时设置的用户信息
    */
    protected boolean login() {
        log.info("开始重新登录");
        return login(userInfo);
    }

    private boolean doLogin(DefaultMessage loginMsg) {
        boolean loginSuccess = false;
        if (sendMsgSync(loginMsg)) {
            int i = 0;
            // 等待收到的登录响应包改变session状态
            do {
                if (session.isLogin()) {
                    return true;
                }
                Try.run(() -> TimeUnit.SECONDS.sleep(1));
                i ++;
            } while (i < 5);
        }
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

    private DefaultMessage ofLoginMsg(String token) {
        BimCommonConfig config = BimConfigFactory.getConfig(BimCommonConfig.class);
        DefaultMessage message = buildCommon(-1, HeadType.LOGIN_REQUEST, null);
        LoginRequest black = LoginRequest.newBuilder()
                .setAppVersion(String.valueOf(config.getVersionNumber()))
                .setToken(token)
                .build();
        return message.toBuilder().setLoginRequest(black).build();
    }
}
