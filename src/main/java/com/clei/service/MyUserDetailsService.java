package com.clei.service;

import com.alibaba.fastjson.JSONObject;
import com.clei.dao.RoleMapper;
import com.clei.dao.UserMapper;
import com.clei.entity.Role;
import com.clei.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * security 用户信息获取
 *
 * @author KIyA
 * @date 2020-04-17
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    private static Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {

        logger.info("用户 {} 尝试登陆", loginName);

        User user = userMapper.selectByLoginName(loginName);

        logger.info("username : " + user.getUsername());
        logger.info("user : " + JSONObject.toJSONString(user));

        if(null != user){

            if(user.isDeleted()){
                logger.info("用户 {} 已被删除", loginName);
                throw new UsernameNotFoundException("");
            }

            if(!user.isEnabled()){
                logger.info("用户 {} 已被停用", loginName);
                throw new UsernameNotFoundException("用户 " + loginName + " 已被停用");
            }

            if(user.isAccountExpired()){
                logger.info("用户 {} 已过期", loginName);
                throw new UsernameNotFoundException("用户 " + loginName + " 已过期");
            }

            if(user.isAccountLocked()){
                logger.info("用户 {} 已被锁定", loginName);
                throw new UsernameNotFoundException("用户 " + loginName + " 已被锁定");
            }

            if(user.isCredentialExpired()){
                logger.info("用户 {} 凭据已过期", loginName);
                throw new UsernameNotFoundException("用户 " + loginName + " 凭据已过期");
            }


            List<Role> roles = roleMapper.selectByUserId(user.getId());
            if(null != roles && !roles.isEmpty()){
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        // 筛选出未删除的
                        .filter(r -> !r.isDeleted())
                        // 查询角色权限
                        .map(Role::getName)
                        // 加个 ROLE_前缀
                        .map( name -> "ROLE_" + name)
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                user.setAuthorities(authorities);

                logger.info("用户 {} 登录成功", loginName);

                return user;
            }else{
                logger.info("用户 {} 没有对应的角色", loginName);
                throw new UsernameNotFoundException("");
            }
        }else{
            logger.info("用户 {} 不存在", loginName);
            throw new UsernameNotFoundException("");
        }
    }
}
