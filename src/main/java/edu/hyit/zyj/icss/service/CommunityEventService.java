package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.CommunityEventDao;
import edu.hyit.zyj.icss.model.CommunityEvent;
import edu.hyit.zyj.icss.util.DataSourceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 社区活动服务类
 */
public class CommunityEventService {
    
    private CommunityEventDao communityEventDao = new CommunityEventDao();
    private final Map<Integer, CommunityEvent> inMemoryEvents = new HashMap<>();
    private final AtomicInteger eventIdGenerator = new AtomicInteger(1);
    
    /**
     * 创建新的社区活动
     * @param organizerId 组织者ID
     * @param title 标题
     * @param description 描述
     * @param eventType 活动类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 地点
     * @param maxParticipants 最大参与人数
     * @param coverImage 封面图片URL
     * @param qrCodeUrl 二维码URL
     * @return 创建的社区活动，如果失败返回null
     */
    public CommunityEvent createEvent(int organizerId, String title, String description,
                                      String eventType, java.util.Date startTime, java.util.Date endTime,
                                      String location, Integer maxParticipants, String coverImage, String qrCodeUrl) {
        if (!isDatabaseAvailable()) {
            CommunityEvent event = new CommunityEvent();
            event.setId(eventIdGenerator.getAndIncrement());
            event.setOrganizerId(organizerId);
            event.setTitle(title);
            event.setDescription(description);
            event.setEventType(eventType);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLocation(location);
            event.setMaxParticipants(maxParticipants);
            event.setCoverImage(coverImage);
            event.setQrCodeUrl(qrCodeUrl);
            inMemoryEvents.put(event.getId(), event);
            return event;
        }
        CommunityEvent event = new CommunityEvent();
        event.setOrganizerId(organizerId);
        event.setTitle(title);
        event.setDescription(description);
        event.setEventType(eventType);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);
        event.setMaxParticipants(maxParticipants);
        event.setCoverImage(coverImage);
        event.setQrCodeUrl(qrCodeUrl);
        
        if (communityEventDao.createEvent(event)) {
            return event;
        }
        return null;
    }
    
    /**
     * 根据ID获取社区活动
     * @param id 活动ID
     * @return CommunityEvent对象
     */
    public CommunityEvent getEventById(int id) {
        if (!isDatabaseAvailable()) {
            return inMemoryEvents.get(id);
        }
        return communityEventDao.findById(id);
    }
    
    /**
     * 获取所有已发布的社区活动
     * @return 社区活动列表
     */
    public List<CommunityEvent> getPublishedEvents() {
        if (!isDatabaseAvailable()) {
            List<CommunityEvent> result = new ArrayList<>();
            for (CommunityEvent event : inMemoryEvents.values()) {
                if ("published".equals(event.getStatus())) {
                    result.add(event);
                }
            }
            return result;
        }
        return communityEventDao.getPublishedEvents();
    }
    
    /**
     * 获取指定组织者的所有社区活动
     * @param organizerId 组织者ID
     * @return 社区活动列表
     */
    public List<CommunityEvent> getEventsByOrganizerId(int organizerId) {
        if (!isDatabaseAvailable()) {
            List<CommunityEvent> result = new ArrayList<>();
            for (CommunityEvent event : inMemoryEvents.values()) {
                if (event.getOrganizerId() == organizerId) {
                    result.add(event);
                }
            }
            return result;
        }
        return communityEventDao.getEventsByOrganizerId(organizerId);
    }
    
    /**
     * 获取所有社区活动（分页）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 社区活动列表
     */
    public List<CommunityEvent> getAllEvents(int page, int size) {
        if (!isDatabaseAvailable()) {
            List<CommunityEvent> allEvents = new ArrayList<>(inMemoryEvents.values());
            int fromIndex = Math.max(0, (page - 1) * size);
            int toIndex = Math.min(allEvents.size(), fromIndex + size);
            if (fromIndex >= allEvents.size()) {
                return new ArrayList<>();
            }
            return allEvents.subList(fromIndex, toIndex);
        }
        int offset = (page - 1) * size;
        return communityEventDao.getAllEvents(offset, size);
    }
    
    /**
     * 更新社区活动状态
     * @param eventId 活动ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateStatus(int eventId, String status) {
        if (!isDatabaseAvailable()) {
            CommunityEvent event = inMemoryEvents.get(eventId);
            if (event == null) {
                return false;
            }
            event.setStatus(status);
            return true;
        }
        return communityEventDao.updateStatus(eventId, status);
    }
    
    /**
     * 更新活动参与人数
     * @param eventId 活动ID
     * @param currentParticipants 当前参与人数
     * @return 是否更新成功
     */
    public boolean updateParticipants(int eventId, int currentParticipants) {
        if (!isDatabaseAvailable()) {
            CommunityEvent event = inMemoryEvents.get(eventId);
            if (event == null) {
                return false;
            }
            event.setCurrentParticipants(currentParticipants);
            return true;
        }
        return communityEventDao.updateParticipants(eventId, currentParticipants);
    }

    private boolean isDatabaseAvailable() {
        return DataSourceUtil.isInitialized();
    }
}
