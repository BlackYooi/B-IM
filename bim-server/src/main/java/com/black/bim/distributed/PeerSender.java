package com.black.bim.distributed;

import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.handler.BimServerNodeHeartBeatHandler;
import com.black.bim.im.codec.DefaultMsgDecoder;
import com.black.bim.im.codec.DefaultMsgEncoder;
import com.black.bim.handler.ImExceptionHandler;
import com.black.bim.util.JsonUtil;
import com.black.bim.util.TimeUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.*;

/**
 * @description：
 * 封装节点间通信信息（会频繁出现节点消息转发事件）
 * 节点之间的通讯也用的是nio
 * @author：8568
 */
@Slf4j
public class PeerSender {

    @Getter
    private BimServerNodeInfo node;

    private Channel channel;

    private boolean connected = false;

    private Bootstrap b;

    private EventLoopGroup g;

    public PeerSender(BimServerNodeInfo targetNode) {
        b = new Bootstrap();
        g = new NioEventLoopGroup();
        node = targetNode;
    }

    private GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) ->
    {
        log.info("节点连接已经断开……{}", node.toString());
        channel = null;
        connected = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) ->
    {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("连接失败!在10s之后准备尝试重连!");
            eventLoop.schedule(() -> PeerSender.this.doConnect(), 10, TimeUnit.SECONDS);
            connected = false;
        } else {
            connected = true;
            log.info(new Date() + "节点连接成功:{}", node.toString());
            channel = f.channel();
            channel.closeFuture().addListener(closeListener);
            // 发送连接成功的通知
            String msgJsonBody = JsonUtil.GSON.toJson(BimWorker.getInstance().getLocalNode());
            MessageNotification.Builder builder = MessageNotification.newBuilder()
                    .setNotifyType(NotifyType.SESSION_ON)
                    .setSenderId(BimWorker.getInstance().getLocalNode().getNodeId())
                    .setJsonContent(msgJsonBody)
                    .setTimestamp(TimeUtil.getCurrentTimeStamp());
            sendNotifyMsg(builder);
        }
    };

    public void doConnect() {
        String host = node.getHost();
        int port = node.getPort();
        try {
            if (b != null && b.config().group() == null) {
                b.group(g);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                b.remoteAddress(host, port);
                // 设置通道初始化
                b.handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast("decoder", new DefaultMsgDecoder());
                                ch.pipeline().addLast("encoder", new DefaultMsgEncoder());
                                ch.pipeline().addLast("imNodeHeartBeatClientHandler", new BimServerNodeHeartBeatHandler());
                                ch.pipeline().addLast("exceptionHandler", new ImExceptionHandler());
                            }
                        }
                );
            }
            log.info(new Date() + "开始连接分布式节点:{}", node.toString());
            ChannelFuture f = b.connect();
            f.addListener(connectedListener);
        } catch (Exception e) {
            log.error("客户端连接失败!" + e.getMessage());
        }
    }

    /**
     * 发送通知消息
    */
    public void sendNotifyMsg(MessageNotification.Builder builder) {
        DefaultMessage msg = DefaultMessage.newBuilder()
                .setType(HeadType.MESSAGE_NOTIFICATION)
                .setNotification(builder)
                .build();
        writeAndFlush(msg);
    }

    public void stopConnecting()
    {
        g.shutdownGracefully();
        connected = false;
    }

    public void writeAndFlush(Object pkg)
    {
        if (connected == false)
        {
            log.error("分布式节点未连接:", node.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }

}
