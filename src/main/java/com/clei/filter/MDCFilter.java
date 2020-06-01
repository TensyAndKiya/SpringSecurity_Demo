package com.clei.filter;

import com.clei.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 加上threadId
 *
 * @author KIyA
 * @date 2020-04-17
 */

@WebFilter(filterName = "MDCFilter",urlPatterns = {"/*"})
public class MDCFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String threadId = IdUtil.ID();

        MDC.put("threadId",threadId);

        filterChain.doFilter(servletRequest,servletResponse);

        MDC.remove("threadId");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
