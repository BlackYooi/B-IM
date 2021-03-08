package com.black.bim.im.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.BiConsumer;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.HeadType;

/**
 * @description：处理消息请求、处理方式：日志展示
 * @author：8568
 */
@Slf4j
public class DefaultMsgHandler extends AbstractDefaultMsgHandler {

    protected BiConsumer<ChannelHandlerContext, DefaultMessage> consumer = null;
    protected List<HeadType> couldProcessMsgList = null;

    public DefaultMsgHandler setConsumer(BiConsumer<ChannelHandlerContext, DefaultMessage> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void setCouldProcessMsgList(List<HeadType> couldProcessMsgList) {
        this.couldProcessMsgList = couldProcessMsgList;
    }

    public void addCouldProcessMsg(HeadType couldProcessMsg) {
        this.couldProcessMsgList.add(couldProcessMsg);
    }

    public void delCouldProcessMsg(HeadType couldProcessMsg) {
        this.couldProcessMsgList.remove(couldProcessMsg);
    }

    /**
     * 默认可以处理所有消息类型
    */
    @Override
    protected Boolean msgCouldProcess(DefaultMessage message) {
        if (null == couldProcessMsgList) {
            return true;
        }
        for (HeadType t : couldProcessMsgList) {
            if (message.getType().equals(t)) {
                return true;
            }
        }
        return true;
    }

    @Override
    protected void processMsg(ChannelHandlerContext ctx, DefaultMessage message) throws Exception {
        if (null == consumer) {
            // 如果没有处理消息的方式、就直接打印
            consumer = this::defaultProcessMsg;
        }
        consumer.accept(ctx, message);
    }

    /**
     * 当未指定消费者时、控制台打印
    */
    private void defaultProcessMsg(ChannelHandlerContext ctx, DefaultMessage message) {
        log.info(String.format("收到消息、内容是【%s】", message.toString()));
    }
}
