package com.black.bim.zk;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.im.exception.NoAvailableServerException;
import com.black.bim.util.Either;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Description:
     *
     * @param invalidNodes 无效的节点列表
     * @return: com.black.bim.entity.BimServerNodeInfo
    */
    public static BimServerNodeInfo getServer(List<BimServerNodeInfo> invalidNodes) throws Exception {
        // 取得负载最小的节点
        try {
            zkConfig = BimConfigFactory.getConfig(ZkConfig.class);
            client = ZkClientFactory.getClientSingleInstanceByConfig();
            Optional<BimServerNodeInfo> first = client.getChildren().forPath(zkConfig.getWorkerManagePath()).stream()
                    // 匹配成全路径
                    .map(BimLoadBalance::getPathRegistered)
                    // 获取bimNode
                    .map(Either.lift(client.getData()::forPath))
                    .filter(Either::isRight)
                    .map(Either::getRight)
                    .filter(Optional::isPresent)
                    .map(Optional<byte[]>::get)
                    .map(BimServerNodeInfo::creatByJsonByte)
                    .filter(i -> !invalidNodes.contains(i))
                    // 获取负载最小的节点
                    .sorted(BimLoadBalance::compareByBalance)
                    .findFirst();
            if (!first.isPresent()) {
                throw new NoAvailableServerException();
            }
            return first.get();
        } catch (Exception e) {
            client.close();
            throw new NoAvailableServerException();
        }
    }

    public static void closeZK() {
        client.close();
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
}
