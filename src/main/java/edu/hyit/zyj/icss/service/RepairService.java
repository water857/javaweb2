package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.RepairOrderDao;
import edu.hyit.zyj.icss.model.RepairOrder;
import edu.hyit.zyj.icss.util.DataSourceUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

/**
 * 报修服务类
 */
public class RepairService {
    
    private RepairOrderDao repairOrderDao = new RepairOrderDao();
    private final Map<Integer, RepairOrder> inMemoryOrders = new HashMap<>();
    private final AtomicInteger orderIdGenerator = new AtomicInteger(1);
    
    /**
     * 创建新的报修工单
     * @param residentId 居民ID
     * @param title 标题
     * @param description 描述
     * @param images 图片URL（JSON格式）
     * @param category 类别
     * @param urgencyLevel 紧急程度
     * @return 创建的报修工单，如果失败返回null
     */
    public RepairOrder createRepairOrder(int residentId, String title, String description,
                                         String images, String category, String urgencyLevel) {
        if (!isDatabaseAvailable()) {
            RepairOrder order = new RepairOrder();
            order.setId(orderIdGenerator.getAndIncrement());
            order.setOrderNumber(generateOrderNumber());
            order.setResidentId(residentId);
            order.setTitle(title);
            order.setDescription(description);
            order.setImages(images);
            order.setCategory(category);
            order.setUrgencyLevel(urgencyLevel);
            inMemoryOrders.put(order.getId(), order);
            return order;
        }
        RepairOrder order = new RepairOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setResidentId(residentId);
        order.setTitle(title);
        order.setDescription(description);
        order.setImages(images);
        order.setCategory(category);
        order.setUrgencyLevel(urgencyLevel);
        
        if (repairOrderDao.createRepairOrder(order)) {
            return order;
        }
        return null;
    }
    
    /**
     * 根据ID获取报修工单
     * @param id 工单ID
     * @return RepairOrder对象
     */
    public RepairOrder getRepairOrderById(int id) {
        if (!isDatabaseAvailable()) {
            return inMemoryOrders.get(id);
        }
        return repairOrderDao.findById(id);
    }
    
    /**
     * 根据订单号获取报修工单
     * @param orderNumber 订单号
     * @return RepairOrder对象
     */
    public RepairOrder getRepairOrderByOrderNumber(String orderNumber) {
        if (!isDatabaseAvailable()) {
            return inMemoryOrders.values().stream()
                    .filter(order -> orderNumber.equals(order.getOrderNumber()))
                    .findFirst()
                    .orElse(null);
        }
        return repairOrderDao.findByOrderNumber(orderNumber);
    }
    
    /**
     * 获取指定居民的所有报修工单
     * @param residentId 居民ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByResidentId(int residentId) {
        if (!isDatabaseAvailable()) {
            List<RepairOrder> result = new ArrayList<>();
            for (RepairOrder order : inMemoryOrders.values()) {
                if (order.getResidentId() == residentId) {
                    result.add(order);
                }
            }
            return result;
        }
        return repairOrderDao.getRepairOrdersByResidentId(residentId);
    }
    
    /**
     * 获取指定服务商的所有报修工单
     * @param serviceId 服务商ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByServiceId(int serviceId) {
        if (!isDatabaseAvailable()) {
            List<RepairOrder> result = new ArrayList<>();
            for (RepairOrder order : inMemoryOrders.values()) {
                if (order.getAssignedServiceId() != null && order.getAssignedServiceId() == serviceId) {
                    result.add(order);
                }
            }
            return result;
        }
        return repairOrderDao.getRepairOrdersByServiceId(serviceId);
    }
    
    /**
     * 获取所有报修工单（分页）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 报修工单列表
     */
    public List<RepairOrder> getAllRepairOrders(int page, int size) {
        if (!isDatabaseAvailable()) {
            List<RepairOrder> allOrders = new ArrayList<>(inMemoryOrders.values());
            int fromIndex = Math.max(0, (page - 1) * size);
            int toIndex = Math.min(allOrders.size(), fromIndex + size);
            if (fromIndex >= allOrders.size()) {
                return new ArrayList<>();
            }
            return allOrders.subList(fromIndex, toIndex);
        }
        int offset = (page - 1) * size;
        return repairOrderDao.getAllRepairOrders(offset, size);
    }
    
    /**
     * 更新报修工单状态
     * @param orderId 工单ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateStatus(int orderId, String status) {
        if (!isDatabaseAvailable()) {
            RepairOrder order = inMemoryOrders.get(orderId);
            if (order == null) {
                return false;
            }
            order.setStatus(status);
            return true;
        }
        return repairOrderDao.updateStatus(orderId, status);
    }
    
    /**
     * 分配服务商到报修工单
     * @param orderId 工单ID
     * @param serviceId 服务商ID
     * @return 是否分配成功
     */
    public boolean assignService(int orderId, int serviceId) {
        if (!isDatabaseAvailable()) {
            RepairOrder order = inMemoryOrders.get(orderId);
            if (order == null) {
                return false;
            }
            order.setAssignedServiceId(serviceId);
            order.setStatus("assigned");
            return true;
        }
        return repairOrderDao.assignService(orderId, serviceId);
    }
    
    /**
     * 完成报修工单
     * @param orderId 工单ID
     * @param rating 评分
     * @param comment 评论
     * @return 是否完成成功
     */
    public boolean completeOrder(int orderId, int rating, String comment) {
        if (!isDatabaseAvailable()) {
            RepairOrder order = inMemoryOrders.get(orderId);
            if (order == null) {
                return false;
            }
            order.setStatus("completed");
            order.setRating(rating);
            order.setComment(comment);
            order.setCompletedTime(new Date());
            return true;
        }
        return repairOrderDao.completeOrder(orderId, rating, comment);
    }
    
    /**
     * 生成唯一的订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        return "RO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isDatabaseAvailable() {
        return DataSourceUtil.isInitialized();
    }
}
