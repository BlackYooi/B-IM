package com.black.bim.im.exception;

/**
 * @description：
 * @author：8568
 */
public class ServerCanNotAvailableException extends Exception{
    public ServerCanNotAvailableException() {
        super("服务器不可达");
    }
}
