package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.util.IOUtil;

import java.util.Properties;

import static com.black.bim.util.FunctionUtil.consumeIfValueNotNullOrThrow;
import static com.black.bim.util.PackingTypeUtil.parseIntOrNull;
import static com.black.bim.util.PackingTypeUtil.parseShortOrNull;

/**
 * @description：
 * @author：8568
 */
public class BimCommonHolder {

    private static BimCommonConfig instance;

    public synchronized static BimCommonConfig getInstance() {
        if (null == instance) {
            instance = new BimCommonConfig();
            Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_COMMON.getPropertiesName());
            consumeIfValueNotNullOrThrow(instance::setMagicCode, parseShortOrNull(properties.getProperty("magicCode")));
            consumeIfValueNotNullOrThrow(instance::setVersionNumber, parseShortOrNull(properties.getProperty("versionNumber")));
            consumeIfValueNotNullOrThrow(instance::setHeartBeatInterval, parseIntOrNull(properties.getProperty("heartBeatInterval")));
        }
        return instance;
    }
}
