package edu.hyit.zyj.icss.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 角色权限过滤器
 */
public class RoleAuthorizationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取用户角色
        String userRole = (String) httpRequest.getAttribute("userRole");
        
        // 获取请求URI
        String requestURI = httpRequest.getRequestURI();
        
        // 检查权限
        if (isAuthorized(userRole, requestURI)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("{\"success\":false,\"message\":\"权限不足\"}");
        }
    }
    
    /**
     * 检查用户是否有权限访问指定资源
     * @param userRole 用户角色
     * @param requestURI 请求URI
     * @return 是否有权限
     */
    private boolean isAuthorized(String userRole, String requestURI) {
        // 居民角色权限
        if ("resident".equals(userRole)) {
            List<String> residentAllowedPaths = Arrays.asList(
                "/api/user/profile",
                "/api/repair",
                "/api/post",
                "/api/event"
            );
            
            for (String path : residentAllowedPaths) {
                if (requestURI.contains(path)) {
                    return true;
                }
            }
            return false;
        }
        
        // 物业管理员角色权限
        if ("property".equals(userRole)) {
            List<String> propertyAllowedPaths = Arrays.asList(
                "/api/user/profile",
                "/api/repair",
                "/api/event",
                "/api/property" // 物业管理专属接口
            );
            
            for (String path : propertyAllowedPaths) {
                if (requestURI.contains(path)) {
                    return true;
                }
            }
            return false;
        }
        
        // 服务商角色权限
        if ("service".equals(userRole)) {
            List<String> serviceAllowedPaths = Arrays.asList(
                "/api/user/profile",
                "/api/repair", // 只能处理分配给自己的报修单
                "/api/service" // 服务管理专属接口
            );
            
            for (String path : serviceAllowedPaths) {
                if (requestURI.contains(path)) {
                    return true;
                }
            }
            return false;
        }
        
        // 默认无权限
        return false;
    }
}