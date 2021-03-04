package com.black.bim.potobuf;


import com.black.bim.client.BimClientSession;
import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.google.gson.Gson;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
public class DefaultMsgBuilder {

    /**
     * 用户配置
     */
    private static BimClientConfig clientConfigure = BimConfigFactory.getConfig(BimClientConfig.class);
    private static BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);

    public static DefaultMessage buildHearBeatMsg(BimClientSession session) {
        DefaultMessage message = buildCommon(-1, HeadType.KEEPALIVE_REQUEST, session);
        MessageHeartBeat.Builder builder = MessageHeartBeat.newBuilder()
                .setSeq(0)
                .setJson("{\"from\":\"client\"}")
                .setUid(session.getUser().getUid());
        return message.toBuilder().setHeartBeat(builder).build();
    }

    public static DefaultMessage ofLoginMsg(ImSession session) {
        DefaultMessage message = buildCommon(-1, HeadType.LOGIN_REQUEST, session);
        UserInfo u = session.getUser();
        LoginRequest black = LoginRequest.newBuilder()
                .setAppVersion(String.valueOf(commonConfig.getVersionNumber()))
                .setUid(u.getUid())
                .setJson(new Gson().toJson(u))
                .build();
        return message.toBuilder().setLoginRequest(black).build();
    }

    /**
     * 构建基础消息部分
     */
    public static DefaultMessage buildCommon(long seqId, HeadType headType, ImSession session) {
        DefaultMessage.Builder builder = DefaultMessage.newBuilder()
                .setType(headType)
                .setSessionId(session.getSessionId())
                .setSequence(seqId);
        return builder.buildPartial();
    }
}
