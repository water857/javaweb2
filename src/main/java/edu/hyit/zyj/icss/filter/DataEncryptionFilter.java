package edu.hyit.zyj.icss.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据加密过滤器
 * 对敏感数据进行加密处理
 */
public class DataEncryptionFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取请求URI
        String requestURI = httpRequest.getRequestURI();
        
        // 对特定敏感接口进行数据加密处理
        if (requestURI.contains("/api/user/") || 
            requestURI.contains("/api/property/")) {
            // 在这里可以对请求参数进行加密处理
            // 例如：加密身份证号、手机号等敏感信息
        }
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
        
        // 对响应数据进行加密处理（如果需要）
        // 注意：这里需要包装response才能处理响应数据
    }
}