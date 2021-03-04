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
public class SessionCache implements Serializable {

    private static final long serialVersionUID = -403010884211394856L;

    /**
     * 用户的唯一标识
    */
    private String userUid;

    /**
     * session id
    */
    private String sessionId;

    /**
     * 所在节点
    */
    private BimServerNodeInfo imNode;

    public SessionCache() {
        userUid = "";
        sessionId = "";
        imNode = new BimServerNodeInfo("unKnown", 0);
    }

    public SessionCache(String sessionId, String userUid, BimServerNodeInfo imNode) {
        this.sessionId = sessionId;
        this.userUid = userUid;
        this.imNode = BimServerNodeInfo.newNodeInfoFromInfoWithoutBalance(imNode);
    }

}
