package com.black.bim.im.exception;

/**
 * @description：
 * @author：8568
 */
public class PeerSendIsNotConnectionException extends Exception {
    public PeerSendIsNotConnectionException(String thisNodeId, String targetNodeId) {
        super(String.format("本节点【%s】为连接到目标节点【%s】", thisNodeId, targetNodeId));
    }
}
