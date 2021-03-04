package com.black.bim.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * @description：
 * @author：8568
 */
@Slf4j
public class IOUtil {

    /**
     * 读取配置文件
    */
    public static Properties getProperties(String name) throws IOException {
        Properties properties = new Properties();
        try (InputStream resourceAsStream = IOUtil.class.getClassLoader().getResourceAsStream(name)) {
            properties.load(resourceAsStream);
        } catch (Exception e) {
            throw e;
        }
        return properties;
    }

    public static String getHostAddress() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            log.error("获取ip报错", ex.getMessage());
        }
        return ip;
    }
}
