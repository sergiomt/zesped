package com.zesped.action;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class EncodingFilter implements Filter {

private String encoding;
private FilterConfig filterConfig;

/**
* @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
*/
public void init(FilterConfig fc) throws ServletException {
  filterConfig = fc;
  encoding = filterConfig.getInitParameter("encoding");
}

/**
* @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
*/
public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
  req.setCharacterEncoding(encoding);
  HttpServletResponse httpResponse = (HttpServletResponse) resp;
  httpResponse.setDateHeader("Expires", 0);
  httpResponse.setHeader("Cache-Control", "no-cache");
  httpResponse.setHeader("Pragma", "no-cache");  
  chain.doFilter(req, resp);
}

/**
* @see javax.servlet.Filter#destroy()
*/
public void destroy() { }

}