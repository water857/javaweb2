package edu.hyit.zyj.icss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hyit.zyj.icss.model.RepairOrder;
import edu.hyit.zyj.icss.service.RepairService;
import edu.hyit.zyj.icss.model.User;

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
 * 报修工单Servlet控制器
 */
@WebServlet("/api/repair/*")
public class RepairServlet extends HttpServlet {
    
    private RepairService repairService = new RepairService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/create".equals(pathInfo)) {
            handleCreateRepairOrder(request, response);
        } else if ("/assign".equals(pathInfo)) {
            handleAssignService(request, response);
        } else if ("/complete".equals(pathInfo)) {
            handleCompleteOrder(request, response);
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
            handleListRepairOrders(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/detail/")) {
            handleGetRepairOrderDetail(request, response);
        } else {
            sendErrorResponse(response, "无效的请求路径", 404);
        }
    }
    
    /**
     * 处理创建报修工单
     */
    private void handleCreateRepairOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        // 只有居民可以创建报修工单
        if (!"resident".equals(user.getRole())) {
            sendErrorResponse(response, "只有居民可以创建报修工单", 403);
            return;
        }
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String images = request.getParameter("images");
        String category = request.getParameter("category");
        String urgencyLevel = request.getParameter("urgencyLevel");
        
        // 参数校验
        if (title == null || title.trim().isEmpty()) {
            sendErrorResponse(response, "标题不能为空", 400);
            return;
        }
        
        if (category == null || category.isEmpty()) {
            sendErrorResponse(response, "类别不能为空", 400);
            return;
        }
        
        if (urgencyLevel == null || urgencyLevel.isEmpty()) {
            sendErrorResponse(response, "紧急程度不能为空", 400);
            return;
        }
        
        // 创建报修工单
        RepairOrder order = repairService.createRepairOrder(
                user.getId(), title, description, images, category, urgencyLevel);
        
        if (order != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "报修工单创建成功");
            result.put("order", order);
            sendJsonResponse(response, result);
        } else {
            sendErrorResponse(response, "报修工单创建失败", 500);
        }
    }
    
    /**
     * 处理分配服务商
     */
    private void handleAssignService(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        // 只有物业管理员可以分配服务商
        if (!"property".equals(user.getRole())) {
            sendErrorResponse(response, "只有物业管理员可以分配服务商", 403);
            return;
        }
        
        String orderIdStr = request.getParameter("orderId");
        String serviceIdStr = request.getParameter("serviceId");
        
        // 参数校验
        if (orderIdStr == null || orderIdStr.isEmpty() || 
            serviceIdStr == null || serviceIdStr.isEmpty()) {
            sendErrorResponse(response, "工单ID和服务商ID不能为空", 400);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdStr);
            int serviceId = Integer.parseInt(serviceIdStr);
            
            // 分配服务商
            boolean success = repairService.assignService(orderId, serviceId);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "服务商分配成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "服务商分配失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "工单ID和服务商ID必须是数字", 400);
        }
    }
    
    /**
     * 处理完成工单
     */
    private void handleCompleteOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        // 只有服务商可以完成工单
        if (!"service".equals(user.getRole())) {
            sendErrorResponse(response, "只有服务商可以完成工单", 403);
            return;
        }
        
        String orderIdStr = request.getParameter("orderId");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        
        // 参数校验
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            sendErrorResponse(response, "工单ID不能为空", 400);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdStr);
            int rating = ratingStr != null ? Integer.parseInt(ratingStr) : 0;
            
            // 完成工单
            boolean success = repairService.completeOrder(orderId, rating, comment);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "工单完成成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "工单完成失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "工单ID和评分必须是数字", 400);
        }
    }
    
    /**
     * 处理更新工单状态
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        String orderIdStr = request.getParameter("orderId");
        String status = request.getParameter("status");
        
        // 参数校验
        if (orderIdStr == null || orderIdStr.isEmpty() || 
            status == null || status.isEmpty()) {
            sendErrorResponse(response, "工单ID和状态不能为空", 400);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdStr);
            
            // 更新工单状态
            boolean success = repairService.updateStatus(orderId, status);
            
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "工单状态更新成功");
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "工单状态更新失败", 500);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "工单ID必须是数字", 400);
        }
    }
    
    /**
     * 处理获取报修工单列表
     */
    private void handleListRepairOrders(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
        
        List<RepairOrder> orders;
        
        // 根据用户角色获取不同的工单列表
        if ("resident".equals(user.getRole())) {
            // 居民只能查看自己的工单
            orders = repairService.getRepairOrdersByResidentId(user.getId());
        } else if ("service".equals(user.getRole())) {
            // 服务商只能查看分配给自己的工单
            orders = repairService.getRepairOrdersByServiceId(user.getId());
        } else {
            // 物业管理员可以查看所有工单
            orders = repairService.getAllRepairOrders(page, size);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("orders", orders);
        sendJsonResponse(response, result);
    }
    
    /**
     * 处理工单详情
     */
    private void handleGetRepairOrderDetail(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendErrorResponse(response, "用户未登录", 401);
            return;
        }
        
        // 从路径中提取工单ID
        String pathInfo = request.getPathInfo();
        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            sendErrorResponse(response, "无效的请求路径", 404);
            return;
        }
        
        try {
            int orderId = Integer.parseInt(parts[2]);
            
            // 获取工单详情
            RepairOrder order = repairService.getRepairOrderById(orderId);
            
            if (order != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("order", order);
                sendJsonResponse(response, result);
            } else {
                sendErrorResponse(response, "工单不存在", 404);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "无效的工单ID", 400);
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