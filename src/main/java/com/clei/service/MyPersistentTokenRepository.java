package com.clei.service;

import com.clei.dao.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * security 自定义用户持久化token处理类
 *
 * @author KIyA
 * @date 2020-05-23
 */
@Service
public class MyPersistentTokenRepository implements PersistentTokenRepository {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {

        loginMapper.saveLogins(persistentRememberMeToken);
    }

    @Override
    public void updateToken(String series, String token, Date date) {
        Map<String,Object> param = new HashMap<>(5);
        param.put("token",token);
        param.put("series",series);
        param.put("date",date);

        loginMapper.updateTokenBySeries(param);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        return loginMapper.getBySeries(series);
    }

    @Override
    public void removeUserTokens(String userName) {
        loginMapper.deleteByUserName(userName);
    }
}
