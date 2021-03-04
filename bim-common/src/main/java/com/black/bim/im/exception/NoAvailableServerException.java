package com.black.bim.im.exception;

/**
 * @description：
 * @author：8568
 */
public class NoAvailableServerException extends Exception {
    public NoAvailableServerException() {
        super("无可用节点");
    }
}
