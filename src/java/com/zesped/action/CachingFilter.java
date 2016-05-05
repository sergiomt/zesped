
package com.zesped.action;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Montoro Ten
 */
public class CachingFilter implements Filter{

    private FilterConfig filterConfig = null;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setDateHeader("Expires", System.currentTimeMillis() + 604800000L);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
    
}
