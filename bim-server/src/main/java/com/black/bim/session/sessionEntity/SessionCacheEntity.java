package com.black.bim.session.sessionEntity;

import com.black.bim.entity.BimServerNodeInfo;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
/**
 * @author 85689
 * 记录了 用户、会话、所在节点信息
 */
@Data
@Builder
public class SessionCacheEntity implements Serializable {

    private static final long serialVersionUID = -403010884211394856L;

    /**
     * session id
    */
    private String sessionId;

    /**
     * 所在节点
    */
    private BimServerNodeInfo imNode;

    public SessionCacheEntity() {
        sessionId = "";
        imNode = new BimServerNodeInfo("unKnown", 0);
    }

    public SessionCacheEntity(String sessionId, BimServerNodeInfo imNode) {
        this.sessionId = sessionId;
        this.imNode = BimServerNodeInfo.newNodeInfoFromInfoWithoutBalance(imNode);
    }

}
