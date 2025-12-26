package edu.hyit.zyj.icss.filter;

import edu.hyit.zyj.icss.util.ApplicationConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * JWT认证过滤器
 */
public class JwtAuthenticationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 获取Authorization头
        String authHeader = httpRequest.getHeader("Authorization");
        
        // 获取请求URI
        String requestURI = httpRequest.getRequestURI();
        System.out.println("Request URI: " + requestURI);
        
        // 如果是登录、注册等公开接口，直接放行
        if (requestURI.endsWith("/api/user/login") || 
            requestURI.endsWith("/api/user/register") ||
            requestURI.equals("/") ||
            requestURI.endsWith("/index.html")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 如果是静态资源文件，直接放行
        if (requestURI.endsWith(".html") || 
            requestURI.endsWith(".css") || 
            requestURI.endsWith(".js") || 
            requestURI.endsWith(".png") || 
            requestURI.endsWith(".jpg") || 
            requestURI.endsWith(".jpeg") || 
            requestURI.endsWith(".gif") || 
            requestURI.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 验证JWT Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉"Bearer "前缀
            
            try {
                // 解析JWT Token
                Claims claims = Jwts.parser()
                        .setSigningKey(ApplicationConfiguration.getProperty("security.jwt.secret"))
                        .parseClaimsJws(token)
                        .getBody();
                
                // 检查token是否过期
                Date expiration = claims.getExpiration();
                if (expiration.before(new Date())) {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.getWriter().write("{\"success\":false,\"message\":\"Token已过期\"}");
                    return;
                }
                
                // 将用户信息添加到请求属性中
                httpRequest.setAttribute("userId", claims.getSubject());
                httpRequest.setAttribute("userRole", claims.get("role"));
                
                // 继续执行过滤器链
                chain.doFilter(request, response);
            } catch (SignatureException e) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("{\"success\":false,\"message\":\"无效的Token\"}");
                return;
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"success\":false,\"message\":\"缺少认证信息\"}");
            return;
        }
    }
    
    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT Token
     */
    public static String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000); // 24小时后过期
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, ApplicationConfiguration.getProperty("security.jwt.secret"))
                .compact();
    }
}