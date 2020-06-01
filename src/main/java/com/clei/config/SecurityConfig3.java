package com.clei.config;

import com.alibaba.fastjson.JSONObject;
import com.clei.service.MyPersistentTokenRepository;
import com.clei.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SpringSecurity config
 * 适用于前后端分离的
 *
 * @author KIyA
 * @date 2020-04-17
 */
@Configuration
public class SecurityConfig3 extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyPersistentTokenRepository myPersistentTokenRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private SessionRegistry sessionRegistry;

    private static Logger logger = LoggerFactory.getLogger(SecurityConfig3.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // 以下是拦截规则
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                // 注意 fullyAuthenticated rememberMe authenticated 区别哦
                .antMatchers("/remember/**").rememberMe()
                .anyRequest().authenticated()

                .and()
                // 以下是表单登录
                .formLogin()
                // 登录页面
                .loginPage("/login.html")
                // 登录接口
                .loginProcessingUrl("/login")
                // 表单登录的 用户名 和 密码 的 name属性
                .usernameParameter("uname")
                .passwordParameter("pwd")
                // 登录成功处理
                .successHandler((request,response,authentication) -> {

                    Object principal = authentication.getPrincipal();

                    String msg = JSONObject.toJSONString(principal);

                    responseJsonMsg(response,msg);
                })
                // 登录失败处理
                .failureHandler((request,response,exception) -> {
                    logger.error("AuthenticationException failureHandler Exception : ",exception);
                    // 超过maxSession 拒绝登录后 处理
                    // 这个竟然是这里处理的 不是 sessionAuthenticationFailureHandler处理的
                    if(exception instanceof SessionAuthenticationException){
                        responseJsonMsg(response,"当前用户已在其它地方登录");
                    }else {
                        responseJsonMsg(response,exception.getMessage());
                    }
                })
                // permitAll 登录相关的页面/接口不要被拦截
                .permitAll()

                // 以下是注销操作
                .and()
                // 记住我
                .rememberMe()
                // 加密key
                .key("KIyA")
                // token持久化
                .tokenRepository(myPersistentTokenRepository)
                // token 有效期 7天
                .tokenValiditySeconds(3600 * 24 * 7)
                // 使用指定的userDetailsService 不然自动登录会出错
                .userDetailsService(myUserDetailsService)

                // 登出处理
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
                // 未登录
                .authenticationEntryPoint((request,response,exception) -> {
                    logger.error("AuthenticationException authenticationEntryPoint Exception : ",exception);
                    responseJsonMsg(response,"尚未登录，请先登录。");
                })
                // 权限不够
                .accessDeniedHandler((request,response,exception) -> {
                    logger.error("AccessDeniedException accessDeniedHandler Exception : ",exception);
                    responseJsonMsg(response,"当前账户无权访问");
                })

                // session管理
                .and()
                .sessionManagement()
                // session认证失败处理
                .sessionAuthenticationFailureHandler((request,response,exception) -> {
                    logger.error("AuthenticationException sessionAuthenticationFailureHandler Exception : ",exception);

                    if(exception instanceof SessionAuthenticationException){
                        // 超过maxSession 拒绝登录后 处理
                        if(exception.getMessage().contains("Maximum sessions")){
                            responseJsonMsg(response,"当前用户已在其它地方登录");
                        }else {
                            responseJsonMsg(response,"登录已失效，请重新登录。");
                        }
                    }else {
                        responseJsonMsg(response,exception.getMessage());
                    }
                })
                // 避免一个用户在多端登录
                // 之前设置为1 但是不起效果 同个帐号可以在不同浏览器同时登录
                // 原来是 比较的时候调用的User的hashCode equals 方法 来判断是不是同一个用户，而这个方法没有重写，导致的
                .maximumSessions(1)
                // .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry)
                .expiredSessionStrategy(e -> {
                    logger.info("expiredSessionStrategy " + e.getSessionInformation().getPrincipal());
                    responseJsonMsg(e.getResponse(),"登录已失效，请重新登录。");
                });
    }


    @Override
    public void configure(WebSecurity web) throws Exception{
        // 配置忽略掉的 URL 地址，一般用于静态文件
        web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**");
    }

    /**
     * 密码加密
     * @return
     */
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
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
     * sessionRegistry
     */
    @Bean
    SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    /**
     * 在 Spring Security 中，它是通过监听 session 的销毁事件，
     * 来及时的清理 session 的记录。
     * 用户从不同的浏览器登录后，都会有对应的 session，
     * 当用户注销登录之后，session 就会失效，
     * 但是默认的失效是通过调用 StandardSession#invalidate 方法来实现的，
     * 这一个失效事件无法被 Spring 容器感知到，
     * 进而导致当用户注销登录之后，
     * Spring Security 没有及时清理会话信息表，以为用户还在线，
     * 进而导致用户无法重新登录进来
     * @return
     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
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
