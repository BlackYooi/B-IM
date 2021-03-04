package com.black.bim.config;

import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.config.configPojo.RedisConfig;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.config.configPojo.BimServerConfig;
import lombok.Data;

/**
 * @description：
 * 配置的持有者、也是不同获取配置的方式的适配器
 * @author：8568
 */
@Data
public class BimConfigHolder {
    private RedisConfig redisConfig;
    private ZkConfig zkConfig;
    private BimCommonConfig imCommonConfig;
    private BimClientConfig clientConfig;
    private BimServerConfig serverConfig;
}
