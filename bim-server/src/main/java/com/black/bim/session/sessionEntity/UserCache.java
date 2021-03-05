package com.black.bim.session.sessionEntity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
    private List<SessionCacheEntity> sessions = new ArrayList<>(10);

    public UserCache(String userUid) {
        this.userUid = userUid;
    }

    /**
     * 为用户增加sessionCache
    */
    public void addSession(SessionCacheEntity session) {

        sessions.add(session);
    }

    /**
     * 为用户移除session
    */
    public void removeSession(String sessionId) {
        for (SessionCacheEntity sessionCacheEntity : sessions) {
            if (sessionCacheEntity.getSessionId().equals(sessionId)) {
                sessions.remove(sessionCacheEntity);
            }
        }
    }


}
