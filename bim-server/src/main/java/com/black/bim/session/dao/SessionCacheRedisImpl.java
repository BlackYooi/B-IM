package com.black.bim.session.dao;

import com.black.bim.config.configPojo.RedisConfig;
import com.black.bim.session.sessionEntity.SessionCache;
import com.black.bim.redis.BimRedis;
import com.black.bim.util.JsonUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @description：
 * @author：8568
 */
public class SessionCacheRedisImpl implements SessionCacheDAO {

    /**
     * 缓存的前缀
     */
    public static final String REDIS_PREFIX = "SessionCache:";

    private RedisConfig redisConfig;

    private JedisPool jedisPool;

    public SessionCacheRedisImpl(BimRedis bimRedis) {
        this.redisConfig = bimRedis.getBimRedisConfig();
        this.jedisPool = bimRedis.getJedisPool();
    }

    @Override
    public void save(SessionCache s) {
        String key = addPrefix(s.getSessionId());
        String value = JsonUtil.objectToJson(s);
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.expire(key, redisConfig.getUserCacheKeepTime());
    }

    @Override
    public SessionCache get(String sessionId) {
        String key = addPrefix(sessionId);
        Jedis jedis = jedisPool.getResource();
        String s = jedis.get(key);
        if (null == s || s.isEmpty()) {
            return null;
        }
        return JsonUtil.jsonStringToObject(s, SessionCache.class);
    }

    @Override
    public void remove(String sessionId) {
        String key = addPrefix(sessionId);
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
    }

    private String addPrefix(String sourceKey) {
        return REDIS_PREFIX + sourceKey;
    }
}
