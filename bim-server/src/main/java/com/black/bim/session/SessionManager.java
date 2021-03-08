package com.black.bim.session;

import com.black.bim.distributed.BimWorker;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.im.ImSession;
import com.black.bim.redis.BimRedis;
import com.black.bim.session.dao.UserCacheDAO;
import com.black.bim.session.dao.UserCacheRedisImpl;
import com.black.bim.session.sessionEntity.SessionCacheEntity;
import com.black.bim.session.sessionEntity.UserCache;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import com.black.bim.session.sessionImpl.BimServerNodeSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.vavr.control.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description：
 * @author：8568
 */
public class SessionManager {

    private UserCacheDAO userCacheDao;

    private static SessionManager instance;

    /**
     * 本地会话清单: 含本地会话、自己的远程会话
    */
    private ConcurrentHashMap<String, BimServerLocalSession> sessionMap = new ConcurrentHashMap();

    private SessionManager() {}

    public synchronized static SessionManager getInstance() {
        BimRedis bimRedis = BimRedis.getInstance();
        if (null == instance) {
            instance = new SessionManager();
            instance.userCacheDao = new UserCacheRedisImpl(bimRedis);
        }
        return instance;
    }

    public void addSession(BimServerLocalSession localSession) {
        if (null == localSession
                || null == localSession.getUser()
                || null == localSession.getUser().getUid()) {
            throw new RuntimeException("session参数不能为空");
        }
        // 保存到会话清单
        sessionMap.put(localSession.getSessionId(), localSession);
        // 保存到redis
        BimServerNodeInfo localNode = BimWorker.getInstance().getLocalNode();
        SessionCacheEntity sessionCache = new SessionCacheEntity(
                localSession.getSessionId(),
                BimServerNodeInfo.newNodeInfoFromInfoWithoutBalance(localNode));
        // 增加用户的session 信息到用户缓存
        if (null == userCacheDao.get(localSession.getUser().getUid())) {
            // 新增会话
            UserCache userCache = new UserCache(localSession.getUser().getUid());
            userCache.addSession(sessionCache);
            userCacheDao.save(userCache);
        } else {
            // 添加会话
            userCacheDao.addSession(localSession.getUser().getUid(), sessionCache);
        }
        // 更新负载数
        BimWorker.getInstance().incBalance();
        // TODO？ 通知其它服务器？
    }

    public List<ImSession> getSessionByUserId(String userUid) {
        UserCache userCache = userCacheDao.get(userUid);
        if (null == userCache) {
            return null;
        }
        List<SessionCacheEntity> allSession = userCache.getSessions();
        if (null == allSession || allSession.size() == 0) {
        }
        List<ImSession> sessions = new LinkedList<>();
        allSession.stream().forEach( sessionCache -> {
            String sessionId = sessionCache.getSessionId();
            // 取得本地session
            ImSession imSession = sessionMap.get(sessionId);
            // 如果无本地session、创建节点委托型session
            if (imSession == null) {
                imSession = new BimServerNodeSession(sessionCache);
            }
            sessions.add(imSession);
        });
        return sessions;
    }

    public Optional<BimServerLocalSession> getLocalSessionById(String sessionId) {
        BimServerLocalSession bimServerLocalSession = sessionMap.get(sessionId);
        return Optional.ofNullable(bimServerLocalSession);
    }

    public Optional<BimServerLocalSession> getChannelSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        BimServerLocalSession serverLocalSession = channel.attr(BimServerLocalSession.SESSION_KEY).get();
        return Optional.ofNullable(serverLocalSession);
    }

    /**
     * 关闭连接
    */
    public void closeSession(ChannelHandlerContext ctx) {
        ImSession session = ctx.channel().attr(BimServerLocalSession.SESSION_KEY).get();
        Try.run(() -> {
            // 删除本地的会话和远程会话
            closeSession(session);
            // 关闭通道
            ctx.channel().closeFuture().sync();
        });

    }

    public void closeSession(ImSession session) {
        closeSession(session.getSessionId(), session.getUser().getUid());
    }

    public void closeSession(String sessionId, String userId) {
        // 如果本地缓存中没有、证明无此用户、直接返回
        if (!sessionMap.containsKey(sessionId)) {
            return;
        }
        // 删除会话
        sessionMap.remove(sessionId);
        userCacheDao.removeSession(userId, sessionId);
        // 减小节点负载
        BimWorker.getInstance().desBalance();
    }
}
