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

    /**
     * 服务节点之间的token, 当通知消息携带的token等于这个值时改节点才是有效节点
    */
    protected String nodeToken;
}
