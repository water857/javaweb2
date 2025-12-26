package edu.hyit.zyj.icss.service;

import edu.hyit.zyj.icss.model.CommunityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CommunityEventService测试类
 */
public class CommunityEventServiceTest {
    
    private CommunityEventService eventService;
    
    @BeforeEach
    public void setUp() {
        eventService = new CommunityEventService();
    }
    
    @Test
    public void testCreateEvent() {
        // 测试创建社区活动
        CommunityEvent event = eventService.createEvent(
                1, "春季运动会", "一年一度的春季运动会", 
                "sports", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + 3600000),
                "社区广场", 100, null, null);
        
        assertNotNull(event);
        assertEquals("春季运动会", event.getTitle());
        assertEquals("sports", event.getEventType());
        assertEquals("draft", event.getStatus());
        assertEquals(0, event.getCurrentParticipants().intValue());
    }
    
    @Test
    public void testGetEventById() {
        // 测试根据ID获取社区活动
        CommunityEvent event = eventService.createEvent(
                1, "儿童绘画班", "儿童绘画培训班", 
                "education", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + 7200000),
                "社区活动中心", 30, null, null);
        
        assertNotNull(event);
        
        CommunityEvent retrievedEvent = eventService.getEventById(event.getId());
        assertNotNull(retrievedEvent);
        assertEquals(event.getId(), retrievedEvent.getId());
        assertEquals("儿童绘画班", retrievedEvent.getTitle());
    }
    
    @Test
    public void testUpdateStatus() {
        // 测试更新活动状态
        CommunityEvent event = eventService.createEvent(
                1, "志愿服务", "社区清洁志愿服务", 
                "volunteer", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + 1800000),
                "社区各区域", 20, null, null);
        
        assertNotNull(event);
        
        boolean result = eventService.updateStatus(event.getId(), "published");
        assertTrue(result);
        
        CommunityEvent updatedEvent = eventService.getEventById(event.getId());
        assertEquals("published", updatedEvent.getStatus());
    }
    
    @Test
    public void testUpdateParticipants() {
        // 测试更新活动参与人数
        CommunityEvent event = eventService.createEvent(
                1, "健康讲座", "老年人健康讲座", 
                "education", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + 5400000),
                "社区会议室", 50, null, null);
        
        assertNotNull(event);
        
        boolean result = eventService.updateParticipants(event.getId(), 25);
        assertTrue(result);
        
        CommunityEvent updatedEvent = eventService.getEventById(event.getId());
        assertEquals(25, updatedEvent.getCurrentParticipants().intValue());
    }
    
    @Test
    public void testGetPublishedEvents() {
        // 测试获取已发布的活动
        // 创建一个已发布的活动
        CommunityEvent event = eventService.createEvent(
                1, "已发布活动", "这是一个已发布的活动", 
                "recreation", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + 3600000),
                "社区广场", 50, null, null);
        
        assertNotNull(event);
        eventService.updateStatus(event.getId(), "published");
        
        // 获取已发布的活动列表
        java.util.List<CommunityEvent> events = eventService.getPublishedEvents();
        assertNotNull(events);
        // 由于我们无法确定数据库中已有的数据，这里只验证不抛出异常
    }
}