package edu.hyit.zyj.icss.dao;



import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 */
public class UserDao {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return User对象，如果未找到返回null
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据用户ID查找用户
     * @param id 用户ID
     * @return User对象，如果未找到返回null
     */
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 创建新用户
     * @param user 用户对象
     * @return 是否创建成功
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, email, phone, real_name, role, avatar_url, id_card, apartment_number, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRealName());
            stmt.setString(6, user.getRole());
            stmt.setString(7, user.getAvatarUrl());
            stmt.setString(8, user.getIdCard());
            stmt.setString(9, user.getApartmentNumber());
            stmt.setString(10, user.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 获取生成的主键
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, phone = ?, real_name = ?, avatar_url = ?, id_card = ?, apartment_number = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getRealName());
            stmt.setString(4, user.getAvatarUrl());
            stmt.setString(5, user.getIdCard());
            stmt.setString(6, user.getApartmentNumber());
            stmt.setString(7, user.getStatus());
            stmt.setInt(8, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 是否更新成功
     */
    public boolean updateLastLoginTime(int userId) {
        String sql = "UPDATE users SET last_login_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet对象
     * @return User对象
     * @throws SQLException
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRealName(rs.getString("real_name"));
        user.setRole(rs.getString("role"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setIdCard(rs.getString("id_card"));
        user.setApartmentNumber(rs.getString("apartment_number"));
        user.setStatus(rs.getString("status"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setLastLoginTime(rs.getTimestamp("last_login_time"));
        return user;
    }
}