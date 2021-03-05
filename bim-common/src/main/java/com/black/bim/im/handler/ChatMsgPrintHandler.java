package com.black.bim.im.handler;

import com.black.bim.im.protobuf.DefaultProtoMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * @description：
 * @author：8568
 */
public class ChatMsgPrintHandler extends DefaultMsgHandler{

    List<DefaultProtoMsg.ProtoMsg.HeadType> list = new ArrayList<>();

    public ChatMsgPrintHandler() {
        list.add(DefaultProtoMsg.ProtoMsg.HeadType.MESSAGE_REQUEST);
        list.add(DefaultProtoMsg.ProtoMsg.HeadType.MESSAGE_RESPONSE);
        setCouldProcessMsgList(list);
    }
}
