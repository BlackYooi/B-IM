package com.black.bim;

/**
 * @description：
 * @author：8568
 */
public class ServerExample {
    public static void main(String[] args) {
        BimServer server = BimServer.defaultServer();
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
