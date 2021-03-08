package com.black.bim.client;

import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.handler.DefaultClientChannelInitializer;
import com.black.bim.im.ImBaseClient;
import com.black.bim.zk.BimLoadBalance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;
import static com.black.bim.util.NotEmptyUtil.notEmptyOrThrow;

/**
 * @description：
 * @author：8568
 */
@Slf4j
@NoArgsConstructor
public class BimClient extends ImBaseClient {

    @Getter
    @Setter
    private NioEventLoopGroup g = null;

    @Getter
    @Setter
    private Bootstrap b;

    @Setter
    @Getter
    private ChannelInitializer<SocketChannel> channelInitializer = null;

    /**
     * 是否采用的默认的消息协议
    */
    private boolean isDefaultMsg = false;

    /**
     * 创建一个默认消息协议的客户端
    */
    public static BimClient defaultClient () {
        BimClient client = new BimClient(true);
        return client;
    }

    public BimClient(Boolean isDefaultMsg) {
        this.isDefaultMsg = isDefaultMsg;
        try {
            initClient();
        } catch (Exception e) {
        }
    }

    @Override
    protected boolean connectToServer() {
        try {
            ChannelFuture connect = b.connect().sync();
            if (connect.isSuccess()) {
                // 连接成功时
                BimClientSession bimClientSession = new BimClientSession(connect.channel());
                bimClientSession.setConnected(true);
                setSession(bimClientSession);
                log.info("成功连接到服务器");
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void sendMsg(DefaultMessage message, ChannelFutureListener f) {
        BimClientSession session = (BimClientSession) getSession();
        Channel channel = session.getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(f);
    }

    @Override
    public boolean sendMsgSync(DefaultMessage message) {
        boolean sendResult = false;
        BimClientSession session = (BimClientSession) getSession();
        Channel channel = session.getChannel();
        if (channel.isActive()) {
            try {
                channel.writeAndFlush(message).sync();
                sendResult = true;
            } catch (InterruptedException e) {
            }
        } else {
        }
        return sendResult;
    }

    @Override
    public void closeClient() {
        try {
            ((BimClientSession)session).close();
            g.shutdownGracefully();
        } catch (Exception e) {
        }
    }

    private void initClient() throws Exception {
        BimServerNodeInfo serverNodeInfo = BimLoadBalance.getServer();
        log.info("服务节点：" + serverNodeInfo);
        BimLoadBalance.closeZK();
        b = new Bootstrap();
        g = new NioEventLoopGroup();
        b.group(g);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.remoteAddress(serverNodeInfo.getHost(), serverNodeInfo.getPort());
        // 通道初始化
        if (!isDefaultMsg) {
            // 如果是用户自定义协议、检查是否传入了该协议的处理方式
            notEmptyOrThrow(channelInitializer);
            b.handler(channelInitializer);
        } else {
            // 默认通讯协议的处理方式
            b.handler(new DefaultClientChannelInitializer());
        }
    }
}
