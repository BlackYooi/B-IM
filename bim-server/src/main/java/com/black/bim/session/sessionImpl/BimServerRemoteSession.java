package com.black.bim.session.sessionImpl;

import com.black.bim.distributed.PeerSender;
import com.black.bim.distributed.WorkerRoute;
import com.black.bim.entity.DefaultUserInfo;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.black.bim.session.sessionEntity.SessionCache;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 85689
 */
@Slf4j
public class BimServerRemoteSession extends ImSession {

    /**
     * 远程会话通过sessionCache记录的节点信息进行通讯
    */
    private SessionCache sessionCache;

    public BimServerRemoteSession(SessionCache sessionCache) {
        this.sessionCache = sessionCache;
        DefaultUserInfo u = new DefaultUserInfo();
        u.setUid(sessionCache.getUserUid());
        this.user = u;
    }

    @Override
    public void writeAndFlush(Object pkg) {
        PeerSender peerSender = WorkerRoute.getInstance().getPeerSender(sessionCache.getImNode().getNodeId());
        peerSender.writeAndFlush(pkg);
    }

    @Override
    public String getSessionId() {
        return sessionCache.getSessionId();
    }

    @Override
    public UserInfo getUser() {
        return user;
    }

    @Override
    public void close() {

    }
}

