package com.black.bim.session.dao;


import com.black.bim.session.sessionEntity.SessionCacheEntity;

/**
 * 会话管理  DAO
 * @author 85689
 */
public interface SessionCacheDAO
{
    /**
     * 保存会话到缓存
    */
    void save(SessionCacheEntity s);

    /**
     * 从缓存获取会话
    */
    SessionCacheEntity get(String sessionId);

    /**
     * 删除会话
    */
    void remove(String sessionId);

}
