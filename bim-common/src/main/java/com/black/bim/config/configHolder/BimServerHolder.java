package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimServerConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

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
            try {
                Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_SERVER.getPropertiesName());
            } catch (IOException e) {
                log.error(String.format("加载binServer配置文件失败【%s】", e.getMessage()));
                e.printStackTrace();
            }
        }
        return instance;
    }
}
