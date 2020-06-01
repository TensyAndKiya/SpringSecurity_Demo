package com.clei.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * login mapper
 *
 * @author KIyA
 * @date 2020-04-17
 */
@Mapper
@Component
public interface LoginMapper {

    /**
     * 保存logins
     * @param persistentRememberMeToken
     */
    void saveLogins(PersistentRememberMeToken persistentRememberMeToken);

    /**
     * 更新token
     * @param param
     */
    void updateTokenBySeries(Map<String, Object> param);

    /**
     * 查询token
     * @param series
     * @return
     */
    PersistentRememberMeToken getBySeries(String series);

    /**
     * 删除
     * @param userName
     */
    void deleteByUserName(String userName);
}
