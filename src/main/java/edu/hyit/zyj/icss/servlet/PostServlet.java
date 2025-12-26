package edu.hyit.zyj.icss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hyit.zyj.icss.model.CommunityPost;
import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.service.CommunityPostService;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邻里圈动态Servlet控制器
 */
@WebServlet("/api/post/*")
public class PostServlet extends HttpServlet {
    
    private CommunityPostService postService = new CommunityPostService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/create".equals(pathInfo)) {
            handleCreatePost(request, response);
        } else if ("/like".equals(pathInfo)) {
            handleLikePost(request, response);
        } else if ("/delete".equals(pathInfo)) {
            handleDeletePost(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/list".equals(pathInfo)) {
            handleListPosts(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/detail/")) {
            handleGetPostDetail(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    /**
     * 处理创建邻里圈动态
     */
    private void handleCreatePost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        String content = request.getParameter("content");
        String images = request.getParameter("images");
        String visibility = request.getParameter("visibility");
        
        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            sendErrorResponse(response, "内容不能为空", 400);
            return;
        }
        
        if (visibility == null || visibility.isEmpty()) {
            visibility = "public"; // 默认公开
        }
        
        // 创建邻里圈动态
        CommunityPost post = postService.createPost(user.getId(), content, images, visibility);
        
        if (post != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "动态发布成功");
            result.put("post", post);
            sendJsonResponse(response, result);
        } else {
            sendErrorResponse(response, "动态发布失败", 500);
        }
    }
    
    /**
     * 处理点赞邻里圈动态
     */
    private void handleLikePost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        String postIdStr = request.getParameter("postId");
        String likeCountStr = request.getParameter("likeCount");
        
        // 参数校验
        if (postIdStr == null || postIdStr.isEmpty()) {
            sendErrorResponse(response, "动态ID不能为空", 400);
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            int likeCount = likeCountStr != null ? Integer.parseInt(likeCountStr) : 1;
            
            // 更新点赞数
            boolean success = postService.updateLikeCount(postId, likeCount);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "点赞成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "点赞失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "动态ID和点赞数必须是数字", 400);
        }
    }
    
    /**
     * 处理删除邻里圈动态
     */
    private void handleDeletePost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        String postIdStr = request.getParameter("postId");
        
        // 参数校验
        if (postIdStr == null || postIdStr.isEmpty()) {
            sendErrorResponse(response, "动态ID不能为空", 400);
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            
            // 检查是否有权限删除（只能删除自己的动态）
            CommunityPost post = postService.getPostById(postId);
            if (post == null) {
                sendErrorResponse(response, "动态不存在", 404);
                return;
            }
            
            if (!post.getUserId().equals(user.getId())) {
                sendErrorResponse(response, "没有权限删除该动态", 403);
                return;
            }
            
            // 删除动态
            boolean success = postService.deletePost(postId);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "动态删除成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "动态删除失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "动态ID必须是数字", 400);
        }
    }
    
    /**
     * 处理获取邻里圈动态列表
     */
    private void handleListPosts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
        
        // 获取所有公开的动态
        List<CommunityPost> posts = postService.getPublicPosts(page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("posts", posts);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理动态详情
     */
    private void handleGetPostDetail(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 从路径中提取动态ID
        String pathInfo = request.getPathInfo();
        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            sendErrorResponse(response, "无效的请求路径", 404);
            return;
        }
        
        try {
            int postId = Integer.parseInt(parts[2]);
            
            // 获取动态详情
            CommunityPost post = postService.getPostById(postId);
            
            if (post != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("post", post);
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "动态不存在", 404);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的动态ID", 400);
        }
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