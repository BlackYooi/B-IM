package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.RedisConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

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
            try {
                Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_REDIS.getPropertiesName());
                instance.setMaxTotal(parseInt(properties.getProperty("maxTotal")));
                instance.setMaxIdle(parseInt(properties.getProperty("maxIdle")));
                instance.setMaxWaitMillis(parseLong(properties.getProperty("maxWaitMillis")));
                instance.setTestOnBorrow(parseBoolean(properties.getProperty("testOnBorrow")));
                instance.setHost((String) properties.get("host"));
                instance.setPort(parseInt(properties.getProperty("port")));
                instance.setConnTimeout(parseInt(properties.getProperty("connTimeout")));
                instance.setReadTimeout(parseInt(properties.getProperty("readTimeout")));
                instance.setPassword(properties.getProperty("password"));
                instance.setDatabase(parseInt(properties.getProperty("database")));
                instance.setMinEvictableIdleTimeMillis(parseLong(properties.getProperty("minEvictableIdleTimeMillis")));
                instance.setSoftMinEvictableIdleTimeMillis(parseLong(properties.getProperty("softMinEvictableIdleTimeMillis")));
                instance.setTimeBetweenEvictionRunsMillis(parseLong(properties.getProperty("timeBetweenEvictionRunsMillis")));
                instance.setNumTestsPerEvictionRun(parseInt(properties.getProperty("numTestsPerEvictionRun")));
                instance.setBlockWhenExhausted(parseBoolean(properties.getProperty("blockWhenExhausted")));
                instance.setTestWhileIdle(parseBoolean(properties.getProperty("testWhileIdle")));
                instance.setUserCacheKeepTime(parseInt(properties.getProperty("userCacheKeepTime")));
            } catch (IOException e) {
                log.error(String.format("加载redis配置文件失败【%s】", e.getMessage()));
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static Integer parseInt(String value) {
        if (null == value) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public static Long parseLong(String value) {
        if (null == value) {
            return null;
        }
        return Long.parseLong(value);
    }

    public static Boolean parseBoolean(String value) {
        if (null == value) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

}
