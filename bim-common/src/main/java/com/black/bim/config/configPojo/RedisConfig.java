package com.black.bim.config.configPojo;

import com.black.bim.config.BimBaseConfig;
import lombok.Data;

/**
 * @description：
 * @author：8568
 */
@Data
public class RedisConfig extends BimBaseConfig {

    private Integer maxTotal;

    private Integer maxIdle;

    private Long maxWaitMillis;

    private Boolean testOnBorrow;

    private String host;

    private Integer port;

    private Integer connTimeout;

    private Integer readTimeout;

    private String password;

    private Integer database;

    private Long minEvictableIdleTimeMillis;

    private Long softMinEvictableIdleTimeMillis;

    private Long timeBetweenEvictionRunsMillis;

    private Integer numTestsPerEvictionRun;

    /**
     * 连接池耗尽时是否阻塞getResource方法
     * true: 阻塞
     * false: 抛出异常
    */
    private Boolean blockWhenExhausted;

    private Boolean testWhileIdle;

    /**
     * 用户缓存的生存时间、单位秒
    */
    private Integer userCacheKeepTime = 60 * 30;
}
