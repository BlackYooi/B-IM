package com.black.bim.distributed;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.zk.ZkClientFactory;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @description：
 * Im节点的zk协调客户端
 * 当由新bim加入时创建节点、同步bim的负载信息
 * @author：8568
 */
@Slf4j
public class BimWorker {

    /**
     * zk客户端
    */
    private CuratorFramework client = null;

    /**
     * zk相关的配置
    */
    private ZkConfig zkConfig;

    /**
     * 临时节点创建成功之后，返回的完整路径。例如：/im/nodes/0000000000，/im/nodes/0000000001等等
    */
    private String pathRegistered = null;

    /**
     * 服务节点的相关属性
    */
    private BimServerNodeInfo bimNode;

    private static BimWorker instance = null;

    public static synchronized BimWorker getInstance() {
        if (null == instance) {
            instance = new BimWorker();
            instance.zkConfig = BimConfigFactory.getConfig(ZkConfig.class);
            instance.client = ZkClientFactory.getClientSingleInstanceByConfig();
            instance.bimNode = new BimServerNodeInfo();
        }
        return instance;
    }

    public void init() {
        client.start();
        // 根据需要看是否创建父节点
        createParentIfNeeded();
        // 创建临时节点、节点上保存bimNode
        createNodeAndSaveBimNode();
        // 为bimNode设置id
        bimNode.setNodeId(parsingId());
    }

    private void createParentIfNeeded() {
        try {
            Stat stat = client.checkExists().forPath(zkConfig.getWorkerManagePath());
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(zkConfig.getWorkerManagePath());
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("创建worker父节点失败【%s】", e.getMessage()));
        }
    }

    private String createNodeAndSaveBimNode() {
        try {
            Gson gson = new Gson();
            byte[] bytes = gson.toJson(bimNode).getBytes("UTF-8");
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(zkConfig.getWorkerManagePath() + "/" + zkConfig.getWorkerPathPrefix(), bytes);
        } catch (Exception e) {
            throw new RuntimeException(String.format("获取bytes失败【%s】", e.getMessage()));
        }
        return pathRegistered;
    }

    private String parsingId() {
        return parsingId(pathRegistered);
    }

    /**
     * 根据zk临时节点全路径解析出id
     */
    public String parsingId(String fullPath) {
        String sid = null;
        int index = fullPath.lastIndexOf(zkConfig.getWorkerPathPrefix());
        if (index >= 0)
        {
            index += zkConfig.getWorkerPathPrefix().length();
            sid = index <= fullPath.length() ? fullPath.substring(index) : null;
        }
        if (null == sid)
        {
            throw new RuntimeException("节点ID获取失败");
        }
        return sid;
    }

    public static String getFullPathById(String id) {
        ZkConfig zc = BimConfigFactory.getConfig(ZkConfig.class);
        return zc.getWorkerManagePath() + "/" + zc.getWorkerPathPrefix() + id;
    }

    public static String getFullPathByChildPath(String childPath) {
        ZkConfig zc = BimConfigFactory.getConfig(ZkConfig.class);
        return zc.getWorkerManagePath() + "/" + childPath;
    }

    public void setBimNodeInfo(String host, Integer port) {
        bimNode.setHost(host);
        bimNode.setPort(port);
    }

    /**
     * 增加负载
    */
    public boolean incBalance() {
        while (true) {
            try {
                bimNode.incrementBalance();
                saveBimNode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 删除负载
    */
    public boolean desBalance() {
        while (true) {
            try {
                bimNode.decrementBalance();
                saveBimNode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private void saveBimNode() throws Exception {
        Gson gson = new Gson();
        byte[] bytes = gson.toJson(bimNode).getBytes("UTF-8");
        client.setData().forPath(pathRegistered, bytes);
    }

    public BimServerNodeInfo getLocalNode() {
        return bimNode;
    }
}
