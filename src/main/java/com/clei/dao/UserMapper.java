package com.clei.dao;

import com.clei.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * user mapper
 *
 * @author KIyA
 * @date 2020-04-17
 */
@Mapper
@Component
public interface UserMapper {

    /**
     * 根据登录名查询
     *
     * @param loginName
     * @return
     */
    User selectByLoginName(String loginName);
}
