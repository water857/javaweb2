package edu.hyit.zyj.icss.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 请求日志过滤器
 */
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(RequestLoggingFilter.class.getName());
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        Date startDate = new Date(startTime);
        
        // 获取请求信息
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        
        // 记录请求信息
        logger.info(String.format("请求开始 - 时间: %s, 方法: %s, URI: %s, 查询参数: %s, IP: %s, User-Agent: %s",
                startDate, method, uri, queryString, remoteAddr, userAgent));
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
        
        // 记录请求结束时间和响应状态
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        int status = httpResponse.getStatus();
        
        logger.info(String.format("请求结束 - URI: %s, 状态码: %d, 耗时: %d ms", uri, status, duration));
    }
}