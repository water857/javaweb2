package edu.hyit.zyj.icss.service;

import edu.hyit.zyj.icss.model.RepairOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RepairService测试类
 */
public class RepairServiceTest {
    
    private RepairService repairService;
    
    @BeforeEach
    public void setUp() {
        repairService = new RepairService();
    }
    
    @Test
    public void testCreateRepairOrder() {
        // 测试创建报修工单功能
        RepairOrder order = repairService.createRepairOrder(
                1, "水龙头漏水", "厨房水龙头漏水严重", 
                null, "plumbing", "medium");
        
        assertNotNull(order);
        assertEquals("水龙头漏水", order.getTitle());
        assertEquals("plumbing", order.getCategory());
        assertEquals("medium", order.getUrgencyLevel());
        assertEquals("submitted", order.getStatus());
        assertNotNull(order.getOrderNumber());
    }
    
    @Test
    public void testGetRepairOrderById() {
        // 测试根据ID获取报修工单
        RepairOrder order = repairService.createRepairOrder(
                1, "电路故障", "客厅电路跳闸", 
                null, "electrical", "high");
        
        assertNotNull(order);
        
        RepairOrder retrievedOrder = repairService.getRepairOrderById(order.getId());
        assertNotNull(retrievedOrder);
        assertEquals(order.getId(), retrievedOrder.getId());
        assertEquals("电路故障", retrievedOrder.getTitle());
    }
    
    @Test
    public void testUpdateStatus() {
        // 测试更新工单状态
        RepairOrder order = repairService.createRepairOrder(
                1, "电梯故障", "电梯无法运行", 
                null, "elevator", "emergency");
        
        assertNotNull(order);
        
        boolean result = repairService.updateStatus(order.getId(), "in_progress");
        assertTrue(result);
        
        RepairOrder updatedOrder = repairService.getRepairOrderById(order.getId());
        assertEquals("in_progress", updatedOrder.getStatus());
    }
    
    @Test
    public void testAssignService() {
        // 测试分配服务商
        RepairOrder order = repairService.createRepairOrder(
                1, "门锁损坏", "防盗门锁芯损坏", 
                null, "other", "low");
        
        assertNotNull(order);
        
        boolean result = repairService.assignService(order.getId(), 2);
        assertTrue(result);
        
        RepairOrder updatedOrder = repairService.getRepairOrderById(order.getId());
        assertEquals(Integer.valueOf(2), updatedOrder.getAssignedServiceId());
        assertEquals("assigned", updatedOrder.getStatus());
    }
    
    @Test
    public void testCompleteOrder() {
        // 测试完成工单
        RepairOrder order = repairService.createRepairOrder(
                1, "空调不制冷", "夏天空调不制冷", 
                null, "electrical", "medium");
        
        assertNotNull(order);
        
        // 先分配服务商
        repairService.assignService(order.getId(), 2);
        
        // 再完成工单
        boolean result = repairService.completeOrder(order.getId(), 5, "服务很好，及时解决问题");
        assertTrue(result);
        
        RepairOrder completedOrder = repairService.getRepairOrderById(order.getId());
        assertEquals("completed", completedOrder.getStatus());
        assertEquals(Integer.valueOf(5), completedOrder.getRating());
        assertEquals("服务很好，及时解决问题", completedOrder.getComment());
    }
}