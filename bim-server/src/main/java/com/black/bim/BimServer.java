package com.black.bim;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.config.configPojo.BimServerConfig;
import com.black.bim.distributed.DistributeStarter;
import com.black.bim.handler.DefaultServerChannelInitializer;
import com.black.bim.im.ImBaseServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;

import java.net.InetSocketAddress;

import static com.black.bim.util.NotEmptyUtil.notEmptyOrThrow;


/**
 * @description：
 * @author：8568
 */
public class BimServer extends ImBaseServer {

    /**
     * 是否是默认的通讯协议
    */
    private boolean isDefaultMsg = false;

    /**
     * 该服务器的配置
    */
    private static BimServerConfig serverConfig = BimConfigFactory.getConfig(BimServerConfig.class);

    /**
     * 自定义流水线
    */
    @Setter
    private ChannelInitializer<SocketChannel> channelInitializer;

    /**
     * 服务器是否启动成功
    */
    private boolean isRunSuccess = false;

    private BimServer(boolean isDefaultMsg) {
        this.isDefaultMsg = isDefaultMsg;
    }

    /**
     * 一台机器只能开一个服务器、所以设置成单例。
     * 多开了反而会影响性能：
     * 服务能力相同、线程切换的开销更大
    */
    private static BimServer server = null;

    public synchronized static BimServer defaultServer() {
        if (null == server) {
            BimServer s = new BimServer(true);
            server = s;
            return s;
        }
        return server;
    }

    @Override
    public void run() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup boss = null;
        NioEventLoopGroup worker = null;
        try {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();
            // 初始化bootstrap
            initBootStrap(b, boss, worker);
            // 装配流水线
            if (!isDefaultMsg) {
                notEmptyOrThrow(channelInitializer);
                b.childHandler(channelInitializer);
            } else {
                b.childHandler(new DefaultServerChannelInitializer());
            }
            // 等待绑定成功
            ChannelFuture bindF = b.bind().sync();
            if (bindF.isSuccess() ) {
                isRunSuccess = true;
            }
            DistributeStarter.start();
            // 一直阻塞、直到发出关闭命令、本服务器不考虑关闭、所以会一直阻塞
            ChannelFuture channelFuture = bindF.channel().closeFuture();
            channelFuture.sync();  
        } finally {
            if (false == boss.isShutdown()) {
                boss.shutdownGracefully().syncUninterruptibly();
            }
            if (false == worker.isShutdown()) {
                worker.shutdownGracefully().syncUninterruptibly();
            }
        }
    }

    private void initBootStrap(ServerBootstrap b, NioEventLoopGroup boss, NioEventLoopGroup worker) {
        //1 设置reactor 线程
        b.group(boss, worker);
        //2 设置nio类型的channel
        b.channel(NioServerSocketChannel.class);
        //3 设置监听端口
        b.localAddress(new InetSocketAddress(serverConfig.getPort()));
        //4 设置通道选项
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR,
                PooledByteBufAllocator.DEFAULT);
    }

    @Override
    public boolean isRunning() {
        return isRunSuccess;
    }
}
