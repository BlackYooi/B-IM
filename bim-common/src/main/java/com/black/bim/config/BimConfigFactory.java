package com.black.bim.config;

import com.black.bim.config.configHolder.*;
import com.black.bim.config.configPojo.*;
import lombok.Getter;

/**
 * @description：
 * 配置的获取工厂
 * @author：8568
 */
public class BimConfigFactory extends BimConfigHolder {

    public static <T extends BimBaseConfig> T getConfig(Class<? extends BimBaseConfig> configClass) {
        BimConfigTypes type = BimConfigTypes.getType(configClass);
        switch (type) {
            case BIM_COMMON: return (T) BimCommonHolder.getInstance();
            case BIM_SERVER: return (T) BimServerHolder.getInstance();
            case BIM_CLIENT: return (T) BimClientHolder.getInstance();
            case BIM_REDIS: return (T) RedisHolder.getInstance();
            case BIM_ZOOKEEPER: return (T) ZKHolder.getInstance();
            default: return null;
        }
    }

    @Getter
    public enum BimConfigTypes {

        BIM_ZOOKEEPER(0,"bim-zk.properties","zk配置", ZkConfig.class),
        BIM_COMMON(1, "bim-common.properties", "bim配置", BimCommonConfig.class),
        BIM_SERVER(2, "bim-server.properties", "bim配置", BimServerConfig.class),
        BIM_CLIENT(3, "bim-client.properties", "bim配置", BimClientConfig.class),
        BIM_REDIS(4, "bim-redis.properties", "redis配置", RedisConfig.class);

        private Integer id;
        private String propertiesName;
        private String describe;
        private Class<? extends BimBaseConfig> configClass;

        BimConfigTypes(Integer id, String propertiesName, String describe, Class<? extends BimBaseConfig> configClass) {
            this.id = id;
            this.propertiesName = propertiesName;
            this.describe = describe;
            this.configClass = configClass;
        }

        public static BimConfigTypes getType(Class<? extends  BimBaseConfig> configClass) {
            for (BimConfigTypes type : BimConfigTypes.values()) {
                if (type.getConfigClass().equals(configClass)) {
                    return type;
                }
            }
            return null;
        }
    }
}
