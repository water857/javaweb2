package edu.hyit.zyj.icss.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接工具类 - 使用数据源管理连接
 */
public class DatabaseUtil {
    
    /**
     * 获取数据库连接 - 通过数据源获取
     * @return Connection对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DataSourceUtil.getConnection();
    }
    
    /**
     * 关闭数据库连接 - 连接池会自动管理连接的关闭和复用
     * @param conn Connection对象
     */
    public static void closeConnection(Connection conn) {
        // 注意：使用连接池时，不需要手动关闭连接
        // 调用close()方法实际上是将连接归还给连接池
        if (conn != null) {
            try {
                conn.close(); // 归还连接到连接池
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}