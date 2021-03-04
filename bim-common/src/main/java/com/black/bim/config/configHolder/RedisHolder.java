package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.RedisConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static com.black.bim.util.PackingTypeUtil.parseIntOrNull;
import static com.black.bim.util.PackingTypeUtil.parseLongOrNull;
import static com.black.bim.util.PackingTypeUtil.parseBooleanOrNull;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class RedisHolder {

    private static RedisConfig instance;

    public synchronized static RedisConfig getInstance() {
        if (null == instance) {
            instance = new RedisConfig();
            Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_REDIS.getPropertiesName());
            instance.setMaxTotal(parseIntOrNull(properties.getProperty("maxTotal")));
            instance.setMaxIdle(parseIntOrNull(properties.getProperty("maxIdle")));
            instance.setMaxWaitMillis(parseLongOrNull(properties.getProperty("maxWaitMillis")));
            instance.setTestOnBorrow(parseBooleanOrNull(properties.getProperty("testOnBorrow")));
            instance.setHost((String) properties.get("host"));
            instance.setPort(parseIntOrNull(properties.getProperty("port")));
            instance.setConnTimeout(parseIntOrNull(properties.getProperty("connTimeout")));
            instance.setReadTimeout(parseIntOrNull(properties.getProperty("readTimeout")));
            instance.setPassword(properties.getProperty("password"));
            instance.setDatabase(parseIntOrNull(properties.getProperty("database")));
            instance.setMinEvictableIdleTimeMillis(parseLongOrNull(properties.getProperty("minEvictableIdleTimeMillis")));
            instance.setSoftMinEvictableIdleTimeMillis(parseLongOrNull(properties.getProperty("softMinEvictableIdleTimeMillis")));
            instance.setTimeBetweenEvictionRunsMillis(parseLongOrNull(properties.getProperty("timeBetweenEvictionRunsMillis")));
            instance.setNumTestsPerEvictionRun(parseIntOrNull(properties.getProperty("numTestsPerEvictionRun")));
            instance.setBlockWhenExhausted(parseBooleanOrNull(properties.getProperty("blockWhenExhausted")));
            instance.setTestWhileIdle(parseBooleanOrNull(properties.getProperty("testWhileIdle")));
            // 自定义配置
            instance.setUserCacheKeepTime(parseIntOrNull(properties.getProperty("userCacheKeepTime")));
        }
        return instance;
    }

}
