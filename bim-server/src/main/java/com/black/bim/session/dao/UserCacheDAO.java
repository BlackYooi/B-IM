package com.black.bim.session.dao;

import com.black.bim.session.sessionEntity.SessionCacheEntity;
import com.black.bim.session.sessionEntity.UserCache;

/**
 * @author 85689
 */
public interface UserCacheDAO
{
    /**
     * 保持用户缓存
    */
    void save(UserCache s);

    /**
     * 获取用户缓存
    */
    UserCache get(String userUid);

    /**
     * 增加用户的会话
    */
    void addSession(String uid, SessionCacheEntity session);


    /**
     * 删除 用户的  会话
    */
    void removeSession(String uid, String sessionId);

}
