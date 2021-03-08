package com.black.bim.config.configHolder;

import com.black.bim.config.BimConfigFactory;
import com.black.bim.config.configPojo.BimClientConfig;
import com.black.bim.util.IOUtil;

import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
public class BimClientHolder {

    private static BimClientConfig instance;

    public synchronized static BimClientConfig getInstance() {
        if (null == instance) {
            instance = new BimClientConfig();
            Properties properties = IOUtil.getProperties(BimConfigFactory.BimConfigTypes.BIM_CLIENT.getPropertiesName());
        }
        return instance;
    }
}
