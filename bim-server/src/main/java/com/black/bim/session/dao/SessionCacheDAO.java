package com.black.bim.session.dao;


import com.black.bim.session.sessionEntity.SessionCache;

/**
 * 会话管理  DAO
 * @author 85689
 */
public interface SessionCacheDAO
{
    /**
     * 保存会话到缓存
    */
    void save(SessionCache s);

    /**
     * 从缓存获取会话
    */
    SessionCache get(String sessionId);

    /**
     * 删除会话
    */
    void remove(String sessionId);

}
