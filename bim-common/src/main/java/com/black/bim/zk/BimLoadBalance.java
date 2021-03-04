package com.black.bim.zk;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.im.exception.NoAvailableServerException;
import com.black.bim.util.Either;
import org.apache.curator.framework.CuratorFramework;

import java.util.Optional;

/**
 * @description：
 * bim的负载均衡器
 * 选出最小负载的bim返回个新加入的用户
 * @author：8568
 */
public class BimLoadBalance {

    private static CuratorFramework client;
    private static ZkConfig zkConfig;

    public static BimServerNodeInfo getServer() throws Exception {
        // 取得负载最小的节点
        try {
            BimServerNodeInfo bimServerNodeInfo = client.getChildren().forPath(zkConfig.getWorkerManagePath()).stream()
                    // 匹配成全路径
                    .map(BimLoadBalance::getPathRegistered)
                    // 获取bimNode
                    .map(Either.lift(client.getData()::forPath))
                    .filter(Either::isRight)
                    .map(Either::getRight)
                    .filter(Optional::isPresent)
                    .map(Optional<byte[]>::get)
                    .map(BimServerNodeInfo::creatByJsonByte)
                    // 获取负载最小的节点
                    .sorted(BimLoadBalance::compareByBalance)
                    .findFirst()
                    .get();
            return bimServerNodeInfo;
        } catch (Exception e) {
            throw new NoAvailableServerException();
        }
    }

    /**
     * 根据子节点拼出全部路径
   */
    private static String getPathRegistered(String childPath) {
        return zkConfig.getWorkerManagePath() + "/" + childPath;
    }

    /**
     * 通过负载数比较两个节点
    */
    private static int compareByBalance(BimServerNodeInfo n1, BimServerNodeInfo n2) {
        return n1.getBalance() - n2.getBalance();
    }

    static {
        zkConfig = BimConfigFactory.getConfig(ZkConfig.class);
        client = ZkClientFactory.getClientSingleInstanceByConfig();
        client.start();
    }
}