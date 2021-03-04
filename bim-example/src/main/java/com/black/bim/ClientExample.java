package com.black.bim;

import com.black.bim.client.BimClient;
import com.black.bim.entity.DefaultUserInfo;
import io.vavr.control.Try;

import java.util.concurrent.TimeUnit;

/**
 * @description：
 * @author：8568
 */
public class ClientExample {
    public static void main(String[] args) {
        BimClient client = BimClient.defaultClient();
        // connect to server
        if (client.connectToServer()) {
            DefaultUserInfo userInfo = new DefaultUserInfo();
            userInfo.setUid("black");
            userInfo.setUserName("qq85689049");
            userInfo.setPassword("qq85689049");
            client.login(userInfo);
        }
        // send msg
        Try.run(() -> TimeUnit.HOURS.sleep(1));
    }
}
