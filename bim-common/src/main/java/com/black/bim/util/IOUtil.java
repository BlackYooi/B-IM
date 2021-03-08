package com.black.bim.util;

import com.black.bim.exception.CanNotLoadPropertiesException;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
public class IOUtil {

    /**
     * 读取配置文件
    */
    public static Properties getProperties(String name) {
        Properties properties = new Properties();
        try (InputStream resourceAsStream = IOUtil.class.getClassLoader().getResourceAsStream(name)) {
            properties.load(resourceAsStream);
        } catch (Exception e) {
            throw new CanNotLoadPropertiesException(name);
        }
        return properties;
    }

    public static String getHostAddress() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
        }
        return ip;
    }
}
