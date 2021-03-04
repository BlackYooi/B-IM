package com.black.bim.distributed;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.util.IOUtil;

/**
 * @description：
 * 各个分布式服务的启动类
 * @author：8568
 */
public class DistributeStarter {
    public static void start() {
        // 读取配置
        BimCommonConfig commonConfig = BimConfigFactory.getConfig(BimCommonConfig.class);
        // 设置节点
        BimWorker.getInstance().setBimNodeInfo(IOUtil.getHostAddress(), commonConfig.getPort());
        // 初始化
        BimWorker.getInstance().init();
        WorkerRoute.getInstance().init();
    }
}
