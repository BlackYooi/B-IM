package com.black.bim.redis;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.RedisConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static com.black.bim.util.FunctionUtil.consumeIfValueNotNull;

/**
 * @author 85689
 */
@Data
public class BimRedis {

    private JedisPoolConfig jedisPoolConfig;

    private JedisPool jedisPool;

    private RedisConfig bimRedisConfig;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static BimRedis instance;

    /**
     * jedis 连接池
     *
     * @return jedis 连接池
     */
    private JedisPoolConfig poolConfig(RedisConfig redisConfig) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        consumeIfValueNotNull(poolConfig::setMaxTotal, redisConfig.getMaxTotal());
        consumeIfValueNotNull(poolConfig::setMaxIdle, redisConfig.getMaxIdle());
        consumeIfValueNotNull(poolConfig::setMaxWaitMillis, redisConfig.getMaxWaitMillis());
        consumeIfValueNotNull(poolConfig::setTestOnBorrow, redisConfig.getTestOnBorrow());
        consumeIfValueNotNull(poolConfig::setMinEvictableIdleTimeMillis, redisConfig.getMinEvictableIdleTimeMillis());
        consumeIfValueNotNull(poolConfig::setSoftMinEvictableIdleTimeMillis, redisConfig.getSoftMinEvictableIdleTimeMillis());
        consumeIfValueNotNull(poolConfig::setTimeBetweenEvictionRunsMillis, redisConfig.getTimeBetweenEvictionRunsMillis());
        consumeIfValueNotNull(poolConfig::setNumTestsPerEvictionRun, redisConfig.getNumTestsPerEvictionRun());
        consumeIfValueNotNull(poolConfig::setBlockWhenExhausted, redisConfig.getBlockWhenExhausted());
        consumeIfValueNotNull(poolConfig::setTestWhileIdle, redisConfig.getTestWhileIdle());
        return poolConfig;
    }

    private JedisPool createJedisPool(RedisConfig bimRedisConfig) {
        return new JedisPool(poolConfig(bimRedisConfig), bimRedisConfig.getHost(), bimRedisConfig.getPort(), 10000);
    }

    public static BimRedis getInstance() {
        if (null == instance) {
            instance = new BimRedis();
            RedisConfig redisConfig = BimConfigFactory.getConfig(RedisConfig.class);
            instance.setBimRedisConfig(redisConfig);
            instance.setJedisPool(instance.createJedisPool(redisConfig));
            instance.setJedisPoolConfig(instance.poolConfig(redisConfig));
        }
        return instance;
    }

}