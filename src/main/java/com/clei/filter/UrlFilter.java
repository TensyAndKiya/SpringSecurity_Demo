package com.clei.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * boot 使用filter 使用servlet和listener也是类似的操作
 * 方式1 filter上使用 @WebFilter注解
 *     启动类上使用 @ServletComponentScan注解
 * 方式2 使用配置类 搞一个FilterRegistrationBean
 *
 * @author KIyA
 * @date 2020-04-17
 */
@WebFilter(filterName = "UrlFiler",urlPatterns = {"/*"})
public class UrlFilter implements Filter, Ordered {

    private Logger logger= LoggerFactory.getLogger(UrlFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String uri = request.getRequestURI();

        if(uri.contains("/static/")
                || uri.contains("/js/")
                || uri.contains("/css/")
                || uri.contains("/images/")
                || uri.contains("/favicon.ico")){

            filterChain.doFilter(servletRequest,servletResponse);

        }else{
            String method = request.getMethod();

            logger.info("{} {}",method,uri);

            long startTime = System.currentTimeMillis();

            filterChain.doFilter(servletRequest,servletResponse);

            logger.info("{} {}  耗时 : {}ms",method,uri,(System.currentTimeMillis() - startTime));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
