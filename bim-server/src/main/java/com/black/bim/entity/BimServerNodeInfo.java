package com.black.bim.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @description：
 * Im节点的pojo
 * 保存IM Worker节点的基础信息如Netty服务IP、Netty服务端口，以及Netty的服务连接数
 * @author：8568
 */
@NoArgsConstructor
@Getter
@Setter
public class BimServerNodeInfo implements Serializable {

    private static final long serialVersionUID = 1769294897193666829L;

    /**
     * id, 由zookeeper生成
    */
    private String nodeId;

    /**
     * netty服务的连接数
    */
    private Integer balance = 0;

    /**
     * netty服务的ip
    */
    private String host;

    /**
     * netty服务的端口
    */
    private Integer port;

    public BimServerNodeInfo(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void incrementBalance()
    {
        balance++;
    }

    public void decrementBalance()
    {
        balance--;
    }

    @Override
    public String toString() {
        return "BimNode{" +
                "id=" + nodeId +
                ", balance=" + balance +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BimServerNodeInfo bimNode = (BimServerNodeInfo) o;
        return Objects.equals(host, bimNode.host) &&
                Objects.equals(port, bimNode.port);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nodeId, host, port);
    }

    public static BimServerNodeInfo creatByJsonByte(byte[] jsonByte) {
        try {
            String s = new String(jsonByte, "UTF-8");
            BimServerNodeInfo bimServerNodeInfo = JSONObject.parseObject(s, BimServerNodeInfo.class);
            return bimServerNodeInfo;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("无法从byte[]恢复成BimServerNodeInfo");
        }
    }

    public static BimServerNodeInfo newNodeInfoFromInfoWithoutBalance(BimServerNodeInfo nodeInfo) {
        BimServerNodeInfo n = new BimServerNodeInfo();
        n.setBalance(null);
        n.setNodeId(nodeInfo.getNodeId());
        n.setHost(nodeInfo.getHost());
        n.setPort(nodeInfo.getPort());
        return n;
    }
}
