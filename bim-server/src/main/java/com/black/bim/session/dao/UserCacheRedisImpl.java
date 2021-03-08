package com.black.bim.session.dao;

import com.black.bim.config.configPojo.RedisConfig;
import com.black.bim.session.sessionEntity.SessionCacheEntity;
import com.black.bim.session.sessionEntity.UserCache;
import com.black.bim.redis.BimRedis;
import com.black.bim.util.JsonUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @description：
 * @author：8568
 */
public class UserCacheRedisImpl implements UserCacheDAO {

    /**
     * 缓存的前缀
    */
    public static final String REDIS_PREFIX = "UserCache:";

    private RedisConfig redisConfig;

    private JedisPool jedisPool;

    public UserCacheRedisImpl(BimRedis bimRedis) {
        this.redisConfig = bimRedis.getBimRedisConfig();
        this.jedisPool = bimRedis.getJedisPool();
    }

    @Override
    public void save(UserCache s) {
        String key = addPrefix(s.getUserUid());
        String value = JsonUtil.objectToJson(s);
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.expire(key, redisConfig.getUserCacheKeepTime());
        jedis.close();
    }

    @Override
    public UserCache get(String userUid) {
        String key = addPrefix(userUid);
        Jedis jedis = jedisPool.getResource();
        String s = jedis.get(key);
        if (null == s || s.isEmpty()) {
            return null;
        }
        jedis.close();
        return JsonUtil.GSON.fromJson(s, UserCache.class);
    }

    @Override
    public void addSession(String userUid, SessionCacheEntity session) {
        UserCache userCache = get(userUid);
        if (null == userCache) {
            return;
        }
        userCache.addSession(session);
        String value = JsonUtil.objectToJson(userCache);
        Jedis jedis = jedisPool.getResource();
        String key = addPrefix(userUid);
        jedis.set(key, value);
        jedis.expire(key, redisConfig.getUserCacheKeepTime());
        jedis.close();
     }

    @Override
    public void removeSession(String userUid, String sessionId) {
        UserCache userCache = get(userUid);
        if (null == userCache) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        String key = addPrefix(userUid);
        userCache.removeSession(sessionId);
        if (null == userCache.getSessions() || userCache.getSessions().isEmpty()) {
            jedis.del(key);
        } else {
            String value = JsonUtil.objectToJson(userCache);
            jedis.set(key, value);
        }
        jedis.close();
    }

    private String addPrefix(String sourceKey) {
        return REDIS_PREFIX + sourceKey;
    }
}
