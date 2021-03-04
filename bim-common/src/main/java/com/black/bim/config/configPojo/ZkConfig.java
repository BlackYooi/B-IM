package com.black.bim.config.configPojo;

import com.black.bim.config.BimBaseConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @description：
 * zk配置
 * @author：8568
 */
@Slf4j
public class ZkConfig extends BimBaseConfig {

    /**
     * 连接信息
     * 如：127.0.0.1:1998 或者 127.0.0.1:1998,127.0.0.1:1999,127.0.0.1:2000
    */
    @Getter
    @Setter
    private String connectionString;

    /**
     * 管理节点
     * 如 /im/nodes
     * 所有 Worker{@link com.black.bim.distributed.BimWorker}临时工作节点的父亲节点的路径
    */
    @Getter
    @Setter
    private String workerManagePath;

    /**
     * 临时节点前缀
     * 如"/im/nodes/id-"、“/im/nodes/seq-”等等
    */
    @Getter
    @Setter
    private String workerPathPrefix;
}
