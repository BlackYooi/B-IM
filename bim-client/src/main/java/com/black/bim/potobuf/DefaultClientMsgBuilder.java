package com.black.bim.potobuf;


import com.black.bim.client.BimClientSession;
import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.black.bim.im.protobuf.DefaultMsgBuilder;
import com.google.gson.Gson;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * @author：8568
 */
public class DefaultClientMsgBuilder extends DefaultMsgBuilder {

    /**
     * 用户配置
     */
    private static BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);

    public static DefaultMessage buildHearBeatMsg(BimClientSession session) {
        DefaultMessage message = buildCommon(-1, HeadType.KEEPALIVE_REQUEST, session);
        MessageHeartBeat.Builder builder = MessageHeartBeat.newBuilder()
                .setSeq(0)
                .setJson("{\"from\":\"client\"}")
                .setUid(session.getUser().getUid());
        return message.toBuilder().setHeartBeat(builder).build();
    }
}
