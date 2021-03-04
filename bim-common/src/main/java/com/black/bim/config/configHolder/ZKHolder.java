package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.ZkConfig;
import com.black.bim.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class ZKHolder {

    private static ZkConfig instance;

    public static ZkConfig getInstance() {
        if (null == instance) {
            instance = new ZkConfig();
            Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_ZOOKEEPER.getPropertiesName());
            instance.setConnectionString(properties.getProperty("connectionString"));
            instance.setWorkerManagePath(null == properties.getProperty("workerManagePath") ? "/im/nodes" : properties.getProperty("workerManagePath"));
            instance.setWorkerPathPrefix(null == properties.getProperty("workerPathPrefix") ? "" : properties.getProperty("workerPathPrefix"));
        }
        return instance;
    }
}
