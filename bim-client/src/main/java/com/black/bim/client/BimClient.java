package com.black.bim.client;

import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.entity.UserInfo;
import com.black.bim.handler.DefaultClientChannelInitializer;
import com.black.bim.im.ImBaseClient;
import com.black.bim.zk.BimLoadBalance;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.black.bim.im.protobuf.DefaultProtoMsg.ProtoMsg.DefaultMessage;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class BimClient extends ImBaseClient {

    private static BimClient clientWrapper;

    private BimClientBePacked instance;

    private UserInfo uCache;

    private BimClient() {
        instance = new BimClientBePacked();
    }

    public synchronized static BimClient defaultClient() {
        if (null == clientWrapper) {
            clientWrapper = new BimClient();
        }
        return clientWrapper;
    }

    @Override
    protected boolean connectToServer() {
        boolean b = instance.connectToServer();
        session = instance.getSession();
        return b;
    }

    @Override
    public boolean reConnect() {
        instance = new BimClientBePacked(true);
        return instance.login(uCache);
    }

    @Override
    public boolean login(UserInfo u) {
        uCache = u;
        return super.login(u);
    }

    @Override
    public void sendMsg(DefaultMessage message, ChannelFutureListener f) {
        instance.sendMsg(message, f);
    }

    @Override
    public boolean sendMsgSync(DefaultMessage message) {
        return instance.sendMsgSync(message);
    }

    @Override
    protected void closeClient() throws Exception {
        instance.closeClient();
    }

    /**
     * 被装饰的bim客户端
    */
    private class BimClientBePacked extends ImBaseClient {

        private NioEventLoopGroup g = null;

        private Bootstrap b;

        private ChannelInitializer<SocketChannel> channelInitializer = null;


        /**
         * 缓存的登录信息、当服务器下线是需要用到
         */
        private UserInfo userInfo;

        private BimClientBePacked() {
            try {
                initClient();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("初始化客户端失败{}", e.getMessage());
            }
        }

        private BimClientBePacked(boolean getServerNewZK) {
            try {
                initClient();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("初始化客户端失败{}", e.getMessage());
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
                    session = bimClientSession;
                    log.info("成功连接到服务器");
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("连接到服务器失败，{}", e.getMessage());
            }
            return false;
        }

        @Override
        @Deprecated
        public boolean reConnect() {
            return false;
        }

        @Override
        public boolean login(UserInfo u) {
            userInfo = u;
            return super.login(u);
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
            List<BimServerNodeInfo> invalidNodes = new ArrayList<>();
            BimServerNodeInfo serverNodeInfo = BimLoadBalance.getServer(invalidNodes);
            int reTryCount = 0;
            while (!isHostConnectable(serverNodeInfo) && reTryCount < 5) {
                invalidNodes.add(serverNodeInfo);
                Try.run(() -> TimeUnit.SECONDS.sleep(2));
                serverNodeInfo = BimLoadBalance.getServer(invalidNodes);
                reTryCount++;
            }
            if (!isHostConnectable(serverNodeInfo)) {
                throw new Exception("无可用服务");
            }
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
            // 默认通讯协议的处理方式
            b.handler(new DefaultClientChannelInitializer());
        }

        private boolean isHostConnectable(BimServerNodeInfo serverNodeInfo) {
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(serverNodeInfo.getHost(), serverNodeInfo.getPort()));
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }
}
