package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimServerConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static com.black.bim.util.FunctionUtil.consumeIfValueNotNullOrThrow;
import static com.black.bim.util.PackingTypeUtil.parseIntOrNull;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class BimServerHolder {

    private static BimServerConfig instance;

    public static BimServerConfig getInstance() {
        if (null == instance) {
            instance = new BimServerConfig();
            Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_SERVER.getPropertiesName());
            consumeIfValueNotNullOrThrow(instance::setPort, parseIntOrNull(properties.getProperty("port")));
            consumeIfValueNotNullOrThrow(instance::setIp, properties.getProperty("ip"));
            consumeIfValueNotNullOrThrow(instance::setNodeToken, properties.getProperty("nodeToken"));
        }
        return instance;
    }
}
