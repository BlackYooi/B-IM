package com.black.bim.session.sessionImpl;

import com.black.bim.distributed.PeerSender;
import com.black.bim.distributed.WorkerRoute;
import com.black.bim.entity.DefaultUserInfo;
import com.black.bim.entity.UserInfo;
import com.black.bim.im.ImSession;
import com.black.bim.session.sessionEntity.SessionCacheEntity;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * 节点委托型session
 * @author 85689
 */
@Slf4j
public class BimServerNodeSession extends ImSession {

    /**
     * 远程会话通过sessionCache记录的节点信息进行通讯
    */
    private SessionCacheEntity sessionCache;

    public BimServerNodeSession(SessionCacheEntity sessionCache) {
        this.sessionCache = sessionCache;
        DefaultUserInfo u = new DefaultUserInfo();
        this.user = u;
    }

    @Override
    public void writeAndFlush(Object pkg) {
        PeerSender peerSender = WorkerRoute.getInstance().getPeerSender(sessionCache.getImNode().getNodeId());
        Try.run(() -> peerSender.writeAndFlush(pkg));
    }

    @Override
    public String getSessionId() {
        return sessionCache.getSessionId();
    }

    @Override
    public UserInfo getUser() {
        return user;
    }
}

