package edu.hyit.zyj.icss.dao;

import edu.hyit.zyj.icss.model.CommunityEvent;
import edu.hyit.zyj.icss.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 社区活动数据访问对象
 */
public class CommunityEventDao {
    
    /**
     * 创建新的社区活动
     * @param event 社区活动对象
     * @return 是否创建成功
     */
    public boolean createEvent(CommunityEvent event) {
        String sql = "INSERT INTO community_events (title, description, organizer_id, event_type, start_time, end_time, location, max_participants, current_participants, cover_image, status, qr_code_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setInt(3, event.getOrganizerId());
            stmt.setString(4, event.getEventType());
            stmt.setTimestamp(5, new Timestamp(event.getStartTime().getTime()));
            stmt.setTimestamp(6, new Timestamp(event.getEndTime().getTime()));
            stmt.setString(7, event.getLocation());
            stmt.setObject(8, event.getMaxParticipants(), Types.INTEGER);
            stmt.setInt(9, event.getCurrentParticipants());
            stmt.setString(10, event.getCoverImage());
            stmt.setString(11, event.getStatus());
            stmt.setString(12, event.getQrCodeUrl());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 获取生成的主键
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 根据ID查找社区活动
     * @param id 活动ID
     * @return CommunityEvent对象，如果未找到返回null
     */
    public CommunityEvent findById(int id) {
        String sql = "SELECT * FROM community_events WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCommunityEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取所有已发布的社区活动
     * @return 社区活动列表
     */
    public List<CommunityEvent> getPublishedEvents() {
        List<CommunityEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM community_events WHERE status = 'published' ORDER BY start_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                events.add(mapResultSetToCommunityEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    /**
     * 获取指定组织者的所有社区活动
     * @param organizerId 组织者ID
     * @return 社区活动列表
     */
    public List<CommunityEvent> getEventsByOrganizerId(int organizerId) {
        List<CommunityEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM community_events WHERE organizer_id = ? ORDER BY create_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToCommunityEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    /**
     * 获取所有社区活动（分页）
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 社区活动列表
     */
    public List<CommunityEvent> getAllEvents(int offset, int limit) {
        List<CommunityEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM community_events ORDER BY create_time DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToCommunityEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    /**
     * 更新社区活动状态
     * @param eventId 活动ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateStatus(int eventId, String status) {
        String sql = "UPDATE community_events SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, eventId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新活动参与人数
     * @param eventId 活动ID
     * @param currentParticipants 当前参与人数
     * @return 是否更新成功
     */
    public boolean updateParticipants(int eventId, int currentParticipants) {
        String sql = "UPDATE community_events SET current_participants = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, currentParticipants);
            stmt.setInt(2, eventId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 将ResultSet映射为CommunityEvent对象
     * @param rs ResultSet对象
     * @return CommunityEvent对象
     * @throws SQLException
     */
    private CommunityEvent mapResultSetToCommunityEvent(ResultSet rs) throws SQLException {
        CommunityEvent event = new CommunityEvent();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setEventType(rs.getString("event_type"));
        event.setStartTime(rs.getTimestamp("start_time"));
        event.setEndTime(rs.getTimestamp("end_time"));
        event.setLocation(rs.getString("location"));
        event.setMaxParticipants(rs.getObject("max_participants", Integer.class));
        event.setCurrentParticipants(rs.getInt("current_participants"));
        event.setCoverImage(rs.getString("cover_image"));
        event.setStatus(rs.getString("status"));
        event.setQrCodeUrl(rs.getString("qr_code_url"));
        event.setCreateTime(rs.getTimestamp("create_time"));
        return event;
    }
}