package edu.hyit.zyj.icss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hyit.zyj.icss.model.CommunityEvent;
import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.service.CommunityEventService;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区活动Servlet控制器
 */
@WebServlet("/api/event/*")
public class EventServlet extends HttpServlet {
    
    private CommunityEventService eventService = new CommunityEventService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/create".equals(pathInfo)) {
            handleCreateEvent(request, response);
        } else if ("/updateStatus".equals(pathInfo)) {
            handleUpdateStatus(request, response);
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
            handleListEvents(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/detail/")) {
            handleGetEventDetail(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    /**
     * 处理创建社区活动
     */
    private void handleCreateEvent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        // 只有物业管理员和居民可以创建活动
        if (!"property".equals(user.getRole()) && !"resident".equals(user.getRole())) {
            sendErrorResponse(response, "只有物业管理员和居民可以创建活动", 403);
            return;
        }
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String eventType = request.getParameter("eventType");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String location = request.getParameter("location");
        String maxParticipantsStr = request.getParameter("maxParticipants");
        String coverImage = request.getParameter("coverImage");
        String qrCodeUrl = request.getParameter("qrCodeUrl");
        
        // 参数校验
        if (title == null || title.trim().isEmpty()) {
            sendErrorResponse(response, "标题不能为空", 400);
            return;
        }
        
        if (eventType == null || eventType.isEmpty()) {
            sendErrorResponse(response, "活动类型不能为空", 400);
            return;
        }
        
        if (startTimeStr == null || startTimeStr.isEmpty()) {
            sendErrorResponse(response, "开始时间不能为空", 400);
            return;
        }
        
        if (endTimeStr == null || endTimeStr.isEmpty()) {
            sendErrorResponse(response, "结束时间不能为空", 400);
            return;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date startTime, endTime;
        try {
            startTime = dateFormat.parse(startTimeStr);
            endTime = dateFormat.parse(endTimeStr);
        } catch (ParseException e) {
            sendErrorResponse(response, "时间格式不正确，应为 yyyy-MM-dd HH:mm:ss", 400);
            return;
        }
        
        Integer maxParticipants = null;
        if (maxParticipantsStr != null && !maxParticipantsStr.isEmpty()) {
            try {
                maxParticipants = Integer.parseInt(maxParticipantsStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(response, "最大参与人数必须是数字", 400);
                return;
            }
        }
        
        // 创建社区活动
        CommunityEvent event = eventService.createEvent(
                user.getId(), title, description, eventType, startTime, endTime,
                location, maxParticipants, coverImage, qrCodeUrl);
        
        if (event != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "社区活动创建成功");
            result.put("event", event);
            sendJsonResponse(response, result);
        } else {
            sendErrorResponse(response, "社区活动创建失败", 500);
        }
    }
    
    /**
     * 处理更新活动状态
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        // 只有活动组织者可以更新活动状态
        String eventIdStr = request.getParameter("eventId");
        String status = request.getParameter("status");
        
        // 参数校验
        if (eventIdStr == null || eventIdStr.isEmpty()) {
            sendErrorResponse(response, "活动ID不能为空", 400);
            return;
        }
        
        if (status == null || status.isEmpty()) {
            sendErrorResponse(response, "状态不能为空", 400);
            return;
        }
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            
            // 检查用户是否有权限更新该活动
            CommunityEvent event = eventService.getEventById(eventId);
            if (event == null) {
                sendErrorResponse(response, "活动不存在", 404);
                return;
            }
            
            if (!event.getOrganizerId().equals(user.getId()) && !"property".equals(user.getRole())) {
                sendErrorResponse(response, "没有权限更新该活动", 403);
                return;
            }
            
            // 更新活动状态
            boolean success = eventService.updateStatus(eventId, status);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "活动状态更新成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "活动状态更新失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "活动ID必须是数字", 400);
        }
    }
    
    /**
     * 处理获取活动列表
     */
    private void handleListEvents(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
        
        // 获取所有已发布的活动
        List<CommunityEvent> events = eventService.getAllEvents(page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("events", events);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理活动详情
     */
    private void handleGetEventDetail(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 从路径中提取活动ID
        String pathInfo = request.getPathInfo();
        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            sendErrorResponse(response, "无效的请求路径", 404);
            return;
        }
        
        try {
            int eventId = Integer.parseInt(parts[2]);
            
            // 获取活动详情
            CommunityEvent event = eventService.getEventById(eventId);
            
            if (event != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("event", event);
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "活动不存在", 404);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的活动ID", 400);
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