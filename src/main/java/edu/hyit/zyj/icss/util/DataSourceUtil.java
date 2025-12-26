package edu.hyit.zyj.icss.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源工具类 - 使用HikariCP连接池管理数据库连接
 */
public class DataSourceUtil {
    
    // HikariCP数据源
    private static HikariDataSource dataSource;
    

    
    /**
     * 初始化数据源
     */
    public static void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ApplicationConfiguration.getDatabaseUrl());
            config.setUsername(ApplicationConfiguration.getDatabaseUsername());
            config.setPassword(ApplicationConfiguration.getDatabasePassword());
            config.setDriverClassName(ApplicationConfiguration.getDatabaseDriver());
            
            // 连接池配置
            config.setMaximumPoolSize(ApplicationConfiguration.getMaximumPoolSize());
            config.setMinimumIdle(ApplicationConfiguration.getMinimumIdle());
            config.setConnectionTimeout(ApplicationConfiguration.getConnectionTimeout());
            config.setIdleTimeout(ApplicationConfiguration.getIdleTimeout());
            config.setMaxLifetime(ApplicationConfiguration.getMaxLifetime());
            config.setLeakDetectionThreshold(ApplicationConfiguration.getLeakDetectionThreshold());
            
            // 连接池名称
            config.setPoolName(ApplicationConfiguration.getPoolName());
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize datasource", e);
        }
    }
    
    /**
     * 获取数据源
     * @return DataSource对象
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 从连接池获取数据库连接
     * @return Connection对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * 关闭数据源
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}