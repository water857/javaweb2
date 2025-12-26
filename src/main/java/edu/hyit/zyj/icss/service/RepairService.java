package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.RepairOrderDao;
import edu.hyit.zyj.icss.model.RepairOrder;

import java.util.List;
import java.util.UUID;

/**
 * 报修服务类
 */
public class RepairService {
    
    private RepairOrderDao repairOrderDao = new RepairOrderDao();
    
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
        return repairOrderDao.findById(id);
    }
    
    /**
     * 根据订单号获取报修工单
     * @param orderNumber 订单号
     * @return RepairOrder对象
     */
    public RepairOrder getRepairOrderByOrderNumber(String orderNumber) {
        return repairOrderDao.findByOrderNumber(orderNumber);
    }
    
    /**
     * 获取指定居民的所有报修工单
     * @param residentId 居民ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByResidentId(int residentId) {
        return repairOrderDao.getRepairOrdersByResidentId(residentId);
    }
    
    /**
     * 获取指定服务商的所有报修工单
     * @param serviceId 服务商ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByServiceId(int serviceId) {
        return repairOrderDao.getRepairOrdersByServiceId(serviceId);
    }
    
    /**
     * 获取所有报修工单（分页）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 报修工单列表
     */
    public List<RepairOrder> getAllRepairOrders(int page, int size) {
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
        return repairOrderDao.updateStatus(orderId, status);
    }
    
    /**
     * 分配服务商到报修工单
     * @param orderId 工单ID
     * @param serviceId 服务商ID
     * @return 是否分配成功
     */
    public boolean assignService(int orderId, int serviceId) {
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
        return repairOrderDao.completeOrder(orderId, rating, comment);
    }
    
    /**
     * 生成唯一的订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        return "RO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}