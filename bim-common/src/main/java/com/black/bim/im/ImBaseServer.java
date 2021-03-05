package com.black.bim.im;

/**
 * @description：服务器基础类
 * @author：8568
 */
public abstract class ImBaseServer implements AutoCloseable{

    /**
     * Description: 启动服务器
     *
     * @param
     * @return: boolean
    */
    public abstract void run() throws Exception;

    public abstract boolean isRunning();

    public abstract void close();
}
