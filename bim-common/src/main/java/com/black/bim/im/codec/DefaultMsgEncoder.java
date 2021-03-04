package com.black.bim.im.codec;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：编码器
 * @author：8568
 */
public class DefaultMsgEncoder extends MessageToByteEncoder<DefaultMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DefaultMessage msg, ByteBuf out) throws Exception {
        BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);
        // 魔数
        out.writeShort(commonConfig.getMagicCode());
        // 版本号
        out.writeShort(commonConfig.getVersionNumber());
        byte[] bytes = msg.toByteArray();
        int msgLength = bytes.length;
        // 内容长度
        out.writeInt(msgLength);
        // 内容
        out.writeBytes(bytes);
    }
}
