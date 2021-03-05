package com.black.bim.distributed;

import com.alibaba.fastjson.JSONObject;
import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.im.protobuf.DefaultProtoMsg;
import com.black.bim.util.Either;
import com.black.bim.zk.ZkClientFactory;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description：
 * 工作路由器
 * 维护了节点关系网
 * @author：8568
 */
@Slf4j
public class WorkerRoute {

    /**
     * zk客户端
    */
    private CuratorFramework client = null;

    private ZkConfig zkConfig = BimConfigFactory.getConfig(ZkConfig.class);

    /**
     * 节点间的通讯
     * String : id
     */
    private ConcurrentHashMap<String, PeerSender> workers = new ConcurrentHashMap<>();

    private static WorkerRoute instance;

    private WorkerRoute() {
    }

    public static synchronized WorkerRoute getInstance() {
        if (null == instance) {
            instance = new WorkerRoute();
            instance.client = ZkClientFactory.getClientSingleInstanceByConfig();
        }
        return instance;
    }

    public void init() {
        try {
            // 订阅节点的增加和删除事件
            CuratorCache curatorCache = CuratorCache.build(client, zkConfig.getWorkerManagePath());
            CuratorCacheListener listener = CuratorCacheListener
                    .builder()
                    .forPathChildrenCache(zkConfig.getWorkerManagePath(), client, new MyPathChildrenCacheListener())
                    .build();
            curatorCache.listenable().addListener(listener);
            curatorCache.start();
        } catch (Exception e) {
            throw new RuntimeException("工作路由器初始化失败");
        }
    }

    private class MyPathChildrenCacheListener implements PathChildrenCacheListener {
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            ChildData data = event.getData();
            switch (event.getType()) {
                case CHILD_ADDED:
                    log.info("CHILD_ADDED : " + data.getPath() + "  数据:" + data.getData());
                    processNodeAdded(data);
                    break;
                case CHILD_REMOVED:
                    log.info("CHILD_REMOVED : " + data.getPath() + "  数据:" + data.getData());
                    processNodeRemoved(data);
                    break;
                case CHILD_UPDATED:
                    log.info("CHILD_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                    break;
                default:
                    log.debug("[PathChildrenCache]节点数据为空, path={}", data == null ? "null" : data.getPath());
                    break;
            }
        }
    }

    private void processNodeAdded(ChildData data) {
        processNodeAdded(data.getPath(), data.getData());
    }

    private void processNodeAdded(String fullPath, byte[] nodeInfoData) {
        BimServerNodeInfo node = JSONObject.parseObject(nodeInfoData, BimServerNodeInfo.class);
        String nodeId = BimWorker.getInstance().parsingId(fullPath);
        if (nodeId.equals(BimWorker.getInstance().getLocalNode().getNodeId())) {
            return;
        }
        node.setNodeId(nodeId);
        log.info("[TreeCache]节点更新端口, path={}, data={}",
                fullPath, node);
        if (node.equals(BimWorker.getInstance().getLocalNode())) {
            log.info("[TreeCache]本地节点, path={}, data={}",
                    fullPath, node);
            return;
        }
        PeerSender peerSender = workers.get(nodeId);
        // 重复收到注册的事件
        if (null != peerSender && peerSender.getNode().equals(node)) {
            log.info("[TreeCache]节点重复增加, path={}, data={}",
                    fullPath, node);
        }
        if (null != peerSender) {
            // 关闭老的连接
            peerSender.stopConnecting();
        }
        // 创建一个消息转发器
        peerSender = new PeerSender(node);
        peerSender.doConnect();
        workers.put(nodeId, peerSender);
    }

    private void processNodeRemoved(ChildData data) {
        byte[] payload = data.getData();
        BimServerNodeInfo bimServerNodeInfo = JSONObject.parseObject(payload, BimServerNodeInfo.class);

        String nodeId = BimWorker.getInstance().parsingId(data.getPath());
        bimServerNodeInfo.setNodeId(nodeId);
        log.info("[TreeCache]节点删除, path={}, data={}",
                data.getPath(), bimServerNodeInfo);
        PeerSender peerSender = workers.get(bimServerNodeInfo.getNodeId());

        if (null != peerSender) {
            peerSender.stopConnecting();
            workers.remove(nodeId);
        }
    }

    /**
     * 发送广播消息、因为是服务节点之间的消息、所以消息类型是：通知
    */
    public void broadcast(DefaultProtoMsg.ProtoMsg.MessageNotification.Builder builder) throws Exception {
        // 不需要向自己发送消息
        String localId = BimWorker.getInstance().getLocalNode().getNodeId();
        for (String id : workers.keySet()) {
            if (id.equals(localId)) {
                continue;
            }
            PeerSender peerSender = workers.get(id);
            peerSender.sendNotifyMsg(builder);
        }
    }

    public PeerSender getPeerSender(String nodeId) {
        return workers.get(nodeId);
    }

    public void remove(BimServerNodeInfo node) {
        workers.remove(node.getNodeId());
    }
}
