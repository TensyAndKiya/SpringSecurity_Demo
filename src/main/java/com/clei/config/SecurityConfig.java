package com.clei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurity config
 * 适用于前后端不分离的
 *
 * @author KIyA
 * @date 2020-04-14
 */
// @Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
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
                // defaultSuccessUrl 设置一个默认登录成功后跳转的url，
                // 这里设置为 /index，假设访问/hello，没登陆跳转到登录页面，
                // 登录成功后会跳转到 /hello
                // successForwardUrl 登录成功后理科跳到到的url，
                // 不管没登陆前访问的是那个url 登录成功后都跳到 /index
                // 这两个 设置一个就行了
                .defaultSuccessUrl("/index")
                // 下面这行的效果和 下下行一样
                // .defaultSuccessUrl("/index",true)
                // .successForwardUrl("/index")
                // 登录失败后跳转
                // failureUrl 重定向 failureForwardUrl 服务器转发
                // 两个设置一个就行了
                // .failureUrl("/loginFailure")
                .failureForwardUrl("/login.html")
                // permitAll 登录相关的页面/接口不要被拦截
                .permitAll()
                // 以下是注销操作
                .and()
                .logout()
                // 下面这两个 设置一个就行了。
                .logoutUrl("/logout")
                // 这个还能改变请求方式
                // .logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST"))
                // 注销登录后跳转
                .logoutSuccessUrl("/login.html")
                .deleteCookies()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                // 关闭 csrf防御
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用配置类来代替默认密码
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("123456")
                .roles("admin");
    }

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
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
