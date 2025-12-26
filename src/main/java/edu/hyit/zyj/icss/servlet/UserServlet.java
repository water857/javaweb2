package edu.hyit.zyj.icss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关Servlet控制器
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    
    private UserService userService = new UserService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/register".equals(pathInfo)) {
            handleRegister(request, response);
        } else if ("/login".equals(pathInfo)) {
            handleLogin(request, response);
        } else if ("/logout".equals(pathInfo)) {
            handleLogout(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/profile".equals(pathInfo)) {
            handleGetProfile(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    /**
     * 处理用户注册
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        // 参数校验
        if (username == null || username.trim().isEmpty() || 
            password == null || password.isEmpty() ||
            role == null || role.isEmpty()) {
            sendErrorResponse(response, "用户名、密码和角色不能为空", 400);
            return;
        }
        
        // 注册用户
        User user = userService.register(username, password, role);
        
        if (user != null) {
            // 注册成功
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("user", user);
            sendJsonResponse(response, result);
        } else {
            // 注册失败
            sendErrorResponse(response, "注册失败，用户名可能已存在", 400);
        }
    }
    
    /**
     * 处理用户登录
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // 参数校验
        if (username == null || username.trim().isEmpty() || 
            password == null || password.isEmpty()) {
            sendErrorResponse(response, "用户名和密码不能为空", 400);
            return;
        }
        
        // 用户登录
        User user = userService.login(username, password);
        
        if (user != null) {
            // 登录成功，将用户信息保存到Session中
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", user);
            sendJsonResponse(response, result);
        } else {
            // 登录失败
            sendErrorResponse(response, "登录失败，用户名或密码错误", 401);
        }
    }
    
    /**
     * 处理用户登出
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 销毁Session
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "登出成功");
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理获取用户信息
     */
    private void handleGetProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("user", user);
        sendJsonResponse(response, result);
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), data);
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        objectMapper.writeValue(response.getWriter(), error);
    }
}