package com.black.bim.session;

import com.black.bim.distributed.BimWorker;
import com.black.bim.entity.BimServerNodeInfo;
import com.black.bim.im.ImSession;
import com.black.bim.session.sessionImpl.BimServerLocalSession;
import com.black.bim.session.sessionImpl.BimServerRemoteSession;
import com.black.bim.session.dao.SessionCacheDAO;
import com.black.bim.session.dao.SessionCacheRedisImpl;
import com.black.bim.session.dao.UserCacheDAO;
import com.black.bim.session.dao.UserCacheRedisImpl;
import com.black.bim.session.sessionEntity.SessionCache;
import com.black.bim.session.sessionEntity.UserCache;
import com.black.bim.redis.BimRedis;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class SessionManager {

    private UserCacheDAO userCacheDao;

    private SessionCacheDAO sessionCacheDao;

    private static SessionManager instance;

    /**
     * 会话清单: 含本地会话、自己的远程会话
    */
    private ConcurrentHashMap<String, ImSession> sessionMap = new ConcurrentHashMap();

    private SessionManager() {}

    public synchronized static SessionManager getInstance() {
        BimRedis bimRedis = BimRedis.getInstance();
        if (null == instance) {
            instance = new SessionManager();
            instance.userCacheDao = new UserCacheRedisImpl(bimRedis);
            instance.sessionCacheDao = new SessionCacheRedisImpl(bimRedis);
        }
        return instance;
    }

    public void addSession(BimServerLocalSession localSession) {
        // 保存到会话清单
        sessionMap.put(localSession.getSessionId(), localSession);
        // 保存到redis
        BimServerNodeInfo localNode = BimWorker.getInstance().getLocalNode();
        SessionCache sessionCache = new SessionCache(localSession.getSessionId(),
                localSession.getUser().getUid(),
                BimServerNodeInfo.newNodeInfoFromInfoWithoutBalance(localNode));
        sessionCacheDao.save(sessionCache);
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
            log.info("用户：{} 下线了? 没有在缓存中找到记录 ", userUid);
            return null;
        }
        Map<String, SessionCache> allSession = userCache.getMap();
        if (null == allSession || allSession.size() == 0) {
            log.info("用户：{} 下线了? 没有在缓存中找到记录 ", userUid);
        }
        List<ImSession> sessions = new LinkedList<>();
        allSession.values().stream().forEach( sessionCache -> {
            String sessionId = sessionCache.getSessionId();
            // 取得本地session
            ImSession imSession = sessionMap.get(sessionId);
            if (imSession == null) {
                sessionMap.put(sessionId, imSession);
                imSession = new BimServerRemoteSession(sessionCache);
            }
            sessions.add(imSession);
        });
        return sessions;
    }

    public Optional<BimServerLocalSession> getSessionById(String sessionId) {
        try {
            ImSession imSession = sessionMap.get(sessionId);
            if (null != imSession && imSession instanceof BimServerLocalSession) {
                BimServerLocalSession serverLocalSession = (BimServerLocalSession) imSession;
                return Optional.of(serverLocalSession);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<BimServerLocalSession> getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        BimServerLocalSession serverLocalSession = channel.attr(BimServerLocalSession.SESSION_KEY).get();
        return Optional.ofNullable(serverLocalSession);
    }

    /**
     * 关闭连接
    */
    public void closeSession(ChannelHandlerContext ctx) {
        ImSession session = ctx.channel().attr(BimServerLocalSession.SESSION_KEY).get();
        if (null == session || !session.isLogin()) {
            log.error("session is null or isValid");
            return;
        }
        session.close();
        //删除本地的会话和远程会话
        this.removeSession(session);

    }

    public void removeSession(ImSession session) {
        // 如果本地缓存中没有、证明无此用户、直接返回
        if (!sessionMap.containsKey(session.getSessionId())) {
            return;
        }
        // 删除会话
        String sessionId = session.getSessionId();
        sessionMap.remove(sessionId);
        sessionCacheDao.remove(sessionId);
        userCacheDao.removeSession(session.getUser().getUid(), sessionId);
        // 减小节点负载
        BimWorker.getInstance().desBalance();
    }
}
