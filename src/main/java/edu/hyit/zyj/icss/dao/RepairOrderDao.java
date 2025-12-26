package edu.hyit.zyj.icss.dao;


import edu.hyit.zyj.icss.model.RepairOrder;
import edu.hyit.zyj.icss.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 报修工单数据访问对象
 */
public class RepairOrderDao {
    
    /**
     * 创建新的报修工单
     * @param repairOrder 报修工单对象
     * @return 是否创建成功
     */
    public boolean createRepairOrder(RepairOrder repairOrder) {
        String sql = "INSERT INTO repair_orders (order_number, resident_id, title, description, images, category, urgency_level, status, assigned_service_id, appointment_time, completed_time, rating, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, repairOrder.getOrderNumber());
            stmt.setInt(2, repairOrder.getResidentId());
            stmt.setString(3, repairOrder.getTitle());
            stmt.setString(4, repairOrder.getDescription());
            stmt.setString(5, repairOrder.getImages());
            stmt.setString(6, repairOrder.getCategory());
            stmt.setString(7, repairOrder.getUrgencyLevel());
            stmt.setString(8, repairOrder.getStatus());
            stmt.setObject(9, repairOrder.getAssignedServiceId(), Types.INTEGER);
            stmt.setTimestamp(10, repairOrder.getAppointmentTime() != null ? new Timestamp(repairOrder.getAppointmentTime().getTime()) : null);
            stmt.setTimestamp(11, repairOrder.getCompletedTime() != null ? new Timestamp(repairOrder.getCompletedTime().getTime()) : null);
            stmt.setObject(12, repairOrder.getRating(), Types.INTEGER);
            stmt.setString(13, repairOrder.getComment());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 获取生成的主键
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    repairOrder.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 根据ID查找报修工单
     * @param id 工单ID
     * @return RepairOrder对象，如果未找到返回null
     */
    public RepairOrder findById(int id) {
        String sql = "SELECT * FROM repair_orders WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRepairOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据订单号查找报修工单
     * @param orderNumber 订单号
     * @return RepairOrder对象，如果未找到返回null
     */
    public RepairOrder findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM repair_orders WHERE order_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRepairOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取指定居民的所有报修工单
     * @param residentId 居民ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByResidentId(int residentId) {
        List<RepairOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM repair_orders WHERE resident_id = ? ORDER BY create_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToRepairOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    /**
     * 获取指定服务商的所有报修工单
     * @param serviceId 服务商ID
     * @return 报修工单列表
     */
    public List<RepairOrder> getRepairOrdersByServiceId(int serviceId) {
        List<RepairOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM repair_orders WHERE assigned_service_id = ? ORDER BY create_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToRepairOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    /**
     * 获取所有报修工单（分页）
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 报修工单列表
     */
    public List<RepairOrder> getAllRepairOrders(int offset, int limit) {
        List<RepairOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM repair_orders ORDER BY create_time DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToRepairOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    /**
     * 更新报修工单状态
     * @param orderId 工单ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE repair_orders SET status = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 分配服务商到报修工单
     * @param orderId 工单ID
     * @param serviceId 服务商ID
     * @return 是否分配成功
     */
    public boolean assignService(int orderId, int serviceId) {
        String sql = "UPDATE repair_orders SET assigned_service_id = ?, status = 'assigned', update_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            stmt.setInt(2, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 完成报修工单
     * @param orderId 工单ID
     * @param rating 评分
     * @param comment 评论
     * @return 是否完成成功
     */
    public boolean completeOrder(int orderId, int rating, String comment) {
        String sql = "UPDATE repair_orders SET status = 'completed', rating = ?, comment = ?, completed_time = NOW(), update_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rating);
            stmt.setString(2, comment);
            stmt.setInt(3, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 将ResultSet映射为RepairOrder对象
     * @param rs ResultSet对象
     * @return RepairOrder对象
     * @throws SQLException
     */
    private RepairOrder mapResultSetToRepairOrder(ResultSet rs) throws SQLException {
        RepairOrder order = new RepairOrder();
        order.setId(rs.getInt("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setResidentId(rs.getInt("resident_id"));
        order.setTitle(rs.getString("title"));
        order.setDescription(rs.getString("description"));
        order.setImages(rs.getString("images"));
        order.setCategory(rs.getString("category"));
        order.setUrgencyLevel(rs.getString("urgency_level"));
        order.setStatus(rs.getString("status"));
        order.setAssignedServiceId(rs.getObject("assigned_service_id", Integer.class));
        order.setAppointmentTime(rs.getTimestamp("appointment_time"));
        order.setCompletedTime(rs.getTimestamp("completed_time"));
        order.setRating(rs.getObject("rating", Integer.class));
        order.setComment(rs.getString("comment"));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setUpdateTime(rs.getTimestamp("update_time"));
        return order;
    }
}