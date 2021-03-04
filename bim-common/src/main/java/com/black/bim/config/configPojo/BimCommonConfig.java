package com.black.bim.config.configPojo;

import com.black.bim.config.BimBaseConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * @author：8568
 */
@Slf4j
@Data
public class BimCommonConfig extends BimBaseConfig {

    /**
     * 魔数
    */
    protected short magicCode = 0;

    /**
     * 版本数字编码
    */
    protected short versionNumber = 0;

    /**
     * 心跳时间间隔、单位-秒
    */
    protected Integer heartBeatInterval = 60 * 60;

    /**
     * 服务器端口
     */
    protected Integer port;
}
