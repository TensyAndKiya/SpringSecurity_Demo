package com.clei.dao;

import com.clei.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * role mapper
 *
 * @author KIyA
 * @date 2020-04-17
 */
@Mapper
@Component
public interface RoleMapper {

    /**
     * 根据用户id查询
     *
     * @param userId
     * @return
     */
    List<Role> selectByUserId(String userId);
}
