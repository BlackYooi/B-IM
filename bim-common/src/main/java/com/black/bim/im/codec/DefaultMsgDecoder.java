package com.black.bim.im.codec;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.config.configPojo.BimCommonConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：Protobuf解码器
 * 数据包编码：
 * 2、2、4、8 （字节）
 * 魔数、版本、数据长度、总占用
 * @author：8568
 */
public class DefaultMsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记当前readIndex的位置
        in.markReaderIndex();
        // 判断包头长度
        if (in.readableBytes() < 8) {
            return;
        }
        // 读取魔数
        short magic = in.readShort();
        BimCommonConfig config = BimConfigFactory.getConfig(BimCommonConfig.class);
        if (config.getMagicCode() != magic) {
            throw new IllegalArgumentException("客户端口令不对");
        }
        // 版本
        short version = in.readShort();
        // 消息长度
        int msgLength = in.readInt();
        if (msgLength < 0) {
            ctx.close();
        }
        if (msgLength > in.readableBytes()) {
            in.resetReaderIndex();
            return;
        }
        byte[] array;
        if (in.hasArray()) {
            // 堆缓冲
            ByteBuf slice = in.slice();
            array = slice.array();
        } else {
            // 直接内存
            array = new byte[msgLength];
            in.readBytes(array, 0, msgLength);
        }
        // 字节转化成对象
        DefaultMessage message = DefaultMessage.parseFrom(array);
        if (null != message) {
            out.add(message);
        }
    }
}
