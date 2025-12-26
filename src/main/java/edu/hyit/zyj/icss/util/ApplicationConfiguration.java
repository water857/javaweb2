package edu.hyit.zyj.icss.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * 应用程序配置管理类
 * 负责读取和管理应用程序的配置信息
 */
public class ApplicationConfiguration {

    private static Properties properties = new Properties();


    /**
     * 加载配置文件
     */
    public static void loadProperties(Reader reader) {
        try{
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("加载配置文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取字符串类型的配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取字符串类型的配置值
     * @param key 配置键
     * @return 配置值
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取整数类型的配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("配置项 " + key + " 的值不是有效的整数: " + value);
            }
        }
        return defaultValue;
    }

    /**
     * 获取长整数类型的配置值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.err.println("配置项 " + key + " 的值不是有效的长整数: " + value);
            }
        }
        return defaultValue;
    }

    // ==================== 数据库配置 ====================

    public static String getDatabaseUrl() {
        return getProperty("db.url");
    }

    public static String getDatabaseUsername() {
        return getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return getProperty("db.password");
    }

    public static String getDatabaseDriver() {
        return getProperty("db.driver");
    }

    // ==================== 数据库连接池配置 ====================

    public static int getMaximumPoolSize() {
        return getIntProperty("db.pool.maximumPoolSize", 20);
    }

    public static int getMinimumIdle() {
        return getIntProperty("db.pool.minimumIdle", 5);
    }

    public static long getConnectionTimeout() {
        return getLongProperty("db.pool.connectionTimeout", 30000L);
    }

    public static long getIdleTimeout() {
        return getLongProperty("db.pool.idleTimeout", 600000L);
    }

    public static long getMaxLifetime() {
        return getLongProperty("db.pool.maxLifetime", 1800000L);
    }

    public static long getLeakDetectionThreshold() {
        return getLongProperty("db.pool.leakDetectionThreshold", 60000L);
    }

    public static String getPoolName() {
        return getProperty("db.pool.name", "SmartCommunityHikariPool");
    }

    // ==================== 文件上传配置 ====================

    public static String getUploadDirectory() {
        return getProperty("upload.directory", "uploads");
    }

    public static int getMaxFileSize() {
        return getIntProperty("upload.maxFileSize", 5 * 1024 * 1024);
    }

    public static int getMaxRequestSize() {
        return getIntProperty("upload.maxRequestSize", 6 * 1024 * 1024);
    }

    // ==================== 应用程序配置 ====================

    public static String getApplicationName() {
        return getProperty("app.name", "Intelligent Community Service System");
    }

    public static String getApplicationVersion() {
        return getProperty("app.version", "1.0.0");
    }
}