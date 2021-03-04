package com.black.bim.config.configPojo;

import com.black.bim.config.BimBaseConfig;
import lombok.Data;

/**
 * @description：
 * @author：8568
 */
@Data
public class BimServerConfig extends BimBaseConfig {
    /**
     * 服务器端口
     */
    protected Integer port;

    /**
     * 服务端地址
    */
    protected String ip;
}
