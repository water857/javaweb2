package edu.hyit.zyj.icss.dao;



import edu.hyit.zyj.icss.model.SystemLog;
import edu.hyit.zyj.icss.util.DatabaseUtil;

import java.sql.*;

/**
 * 系统日志数据访问对象
 */
public class SystemLogDao {
    
    /**
     * 创建新的系统日志
     * @param log 系统日志对象
     * @return 是否创建成功
     */
    public boolean createLog(SystemLog log) {
        String sql = "INSERT INTO system_logs (user_id, module, action, target_id, ip_address, user_agent, status, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, log.getUserId(), Types.INTEGER);
            stmt.setString(2, log.getModule());
            stmt.setString(3, log.getAction());
            stmt.setObject(4, log.getTargetId(), Types.INTEGER);
            stmt.setString(5, log.getIpAddress());
            stmt.setString(6, log.getUserAgent());
            stmt.setString(7, log.getStatus());
            stmt.setString(8, log.getErrorMessage());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 获取生成的主键
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 根据ID查找系统日志
     * @param id 日志ID
     * @return SystemLog对象，如果未找到返回null
     */
    public SystemLog findById(int id) {
        String sql = "SELECT * FROM system_logs WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSystemLog(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将ResultSet映射为SystemLog对象
     * @param rs ResultSet对象
     * @return SystemLog对象
     * @throws SQLException
     */
    private SystemLog mapResultSetToSystemLog(ResultSet rs) throws SQLException {
        SystemLog log = new SystemLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getObject("user_id", Integer.class));
        log.setModule(rs.getString("module"));
        log.setAction(rs.getString("action"));
        log.setTargetId(rs.getObject("target_id", Integer.class));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserAgent(rs.getString("user_agent"));
        log.setOperationTime(rs.getTimestamp("operation_time"));
        log.setStatus(rs.getString("status"));
        log.setErrorMessage(rs.getString("error_message"));
        return log;
    }
}