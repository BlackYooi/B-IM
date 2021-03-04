package com.black.bim.session.sessionEntity;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author 85689
 */
@Data
public class UserCache implements Serializable {

    private static final long serialVersionUID = 2996661276045152707L;

    private String userUid;

    /**
     * 可能会多端登录、所以一个用户对应多个session
    */
    private Map<String, SessionCache> map = new LinkedHashMap<>(10);

    public UserCache(String userUid) {
        this.userUid = userUid;
    }

    /**
     * 为用户增加sessionCache
    */
    public void addSession(SessionCache session) {

        map.put(session.getSessionId(), session);
    }

    /**
     * 为用户移除session
    */
    public void removeSession(String sessionId)
    {
        map.remove(sessionId);
    }


}
