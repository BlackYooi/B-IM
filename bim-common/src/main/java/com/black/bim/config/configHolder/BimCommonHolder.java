package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimCommonConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static com.black.bim.util.FunctionUtil.consumeIfValueNotNullOrThrow;
import static com.black.bim.util.PackingTypeUtil.parseShortOrNull;
import static com.black.bim.util.PackingTypeUtil.parseIntOrNull;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class BimCommonHolder {

    private static BimCommonConfig instance;

    public synchronized static BimCommonConfig getInstance() {
        if (null == instance) {
            instance = new BimCommonConfig();
            try {
                Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_COMMON.getPropertiesName());
                consumeIfValueNotNullOrThrow(instance::setMagicCode, parseShortOrNull(properties.getProperty("magicCode")));
                consumeIfValueNotNullOrThrow(instance::setVersionNumber, parseShortOrNull(properties.getProperty("versionNumber")));
                consumeIfValueNotNullOrThrow(instance::setHeartBeatInterval, parseIntOrNull(properties.getProperty("heartBeatInterval")));
            } catch (IOException e) {
                log.error(String.format("加载bimCommon配置文件失败【%s】", e.getMessage()));
                e.printStackTrace();
            }
        }
        return instance;
    }
}
