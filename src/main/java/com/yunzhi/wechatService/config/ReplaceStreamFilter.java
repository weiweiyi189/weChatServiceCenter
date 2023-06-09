package com.yunzhi.wechatService.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 解决request流只读取一次的问题
 */
public class ReplaceStreamFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    ServletRequest requestWrapper = new RequestWrapper((HttpServletRequest) request);
    chain.doFilter(requestWrapper, response);
  }
}



