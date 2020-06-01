package com.clei.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SpringSecurity config
 * 适用于前后端分离的
 *
 * @author KIyA
 * @date 2020-04-14
 */
// @Configuration
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 以下是拦截规则
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                // 以下是表单登录
                .formLogin()
                // 登录页面
                .loginPage("/login.html")
                // 登录接口
                .loginProcessingUrl("/login")
                // 表单登录的 用户名 和 密码 的 name属性
                .usernameParameter("userName")
                .passwordParameter("pwd")
                // 登录成功处理
                .successHandler((request,response,authentication) -> {

                    Object principal = authentication.getPrincipal();

                    String msg = JSONObject.toJSONString(principal);

                    responseJsonMsg(response,msg);
                })
                // 登录失败处理
                .failureHandler((request,response,exception) -> {
                    responseJsonMsg(response,exception.getMessage());
                })

                // permitAll 登录相关的页面/接口不要被拦截
                .permitAll()
                // 以下是注销操作
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request,response,authentication) -> {
                    responseJsonMsg(response,"注销成功。");
                })
                .deleteCookies()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                // 关闭 csrf防御
                .csrf().disable()
                // 未登录 请求处理
                .exceptionHandling()
                .authenticationEntryPoint((request,response,exception) -> {
                    responseJsonMsg(response,"尚未登录，请先登录。");
                });

    }

    /**
     * 添加内存用户的方法1
     * @param web
     * @throws Exception
     */
    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用配置类来代替默认密码
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("123456")
                .roles("admin")
                .and()
                .withUser("user")
                .password("123456")
                .roles("user");
    }*/

    @Override
    public void configure(WebSecurity web) throws Exception{
        // 配置忽略掉的 URL 地址，一般用于静态文件
        web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**");
    }

    /**
     * 不对密码加密
     * @return
     */
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 添加内存用户的方法2
     * @return
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("admin").password("123456").roles("admin").build());
        manager.createUser(User.withUsername("user").password("123456").roles("user").build());

        return manager;
    }

    /**
     * 角色继承 儿子能干的爸爸都能干
     */
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");

        return roleHierarchy;
    }


    /**
     * 通过response返回 json 内容
     * @param response
     * @param msg
     */
    private void responseJsonMsg(HttpServletResponse response, String msg) throws IOException {

        response.setContentType("application/json;charset=utf-8");

        PrintWriter out = response.getWriter();

        out.write(msg);

        out.flush();

        out.close();
    }
}
