package com.black.bim.im.protobuf;

import com.black.bim.im.ImSession;

/**
 * @description：
 * @author：8568
 */
public class DefaultMsgBuilder {

    /**
     * 构建基础消息部分
     */
    public static DefaultProtoMsg.ProtoMsg.DefaultMessage buildCommon(long seqId, DefaultProtoMsg.ProtoMsg.HeadType headType, ImSession session) {
        DefaultProtoMsg.ProtoMsg.DefaultMessage.Builder builder = DefaultProtoMsg.ProtoMsg.DefaultMessage.newBuilder()
                .setType(headType)
                .setSessionId(session.getSessionId())
                .setSequence(seqId);
        return builder.buildPartial();
    }
}
