package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class BimClientHolder {

    private static BimClientConfig instance;

    public synchronized static BimClientConfig getInstance() {
        if (null == instance) {
            instance = new BimClientConfig();
            try {
                Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_CLIENT.getPropertiesName());
                instance.setServerIp(properties.getProperty("serverIP"));
            } catch (IOException e) {
                log.error(String.format("加载binClient配置文件失败【%s】", e.getMessage()));
                e.printStackTrace();
            }
        }
        return instance;
    }
}
