package edu.hyit.zyj.icss.listener;

import edu.hyit.zyj.icss.util.ApplicationConfiguration;
import edu.hyit.zyj.icss.util.DataSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@WebListener
public class SystemListener implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemListener.class);
    
    // 存储在线用户的会话ID
//    private static final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("system start");
        var context = sce.getServletContext();
        // 初始化系统资源
        initializeSystemResources(context);
        System.out.println("system init OK!");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("system end");
        var context = sce.getServletContext();
        // 清理系统资源
        cleanupSystemResources(context);
    }
    
//    @Override
//    public void sessionCreated(HttpSessionEvent se) {
//        logger.info("新会话创建: " + se.getSession().getId());
//    }
    
//    @Override
//    public void sessionDestroyed(HttpSessionEvent se) {
//        logger.info("会话销毁: " + se.getSession().getId());
//
//        // 从在线用户集合中移除用户
//        String sessionId = se.getSession().getId();
//        onlineUsers.remove(sessionId);
//    }
    
    /**
     * 初始化系统资源
     */
    private void initializeSystemResources(ServletContext context) {
        // 初始化数据库连接池
        try {
            var inputStream=context.getResourceAsStream("WEB-INF/application.properties");
            ApplicationConfiguration.loadProperties(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            logger.info("read config file success");
            DataSourceUtil.initializeDataSource();
            logger.info("init database pool success");
        } catch (Exception e) {
            logger.error("read config file or init database fail", e);
        }
        
        // 示例：加载热点数据到缓存
        // 示例：初始化系统配置
        
        logger.info("系统资源初始化完成");
    }
    
    /**
     * 清理系统资源
     */
    private void cleanupSystemResources(ServletContext context) {
        // 关闭数据库连接池
        try {
            DataSourceUtil.closeDataSource();
            logger.info("数据库连接池已关闭");
        } catch (Exception e) {
            logger.error("关闭数据库连接池时出错", e);
        }
        
        // 示例：清理缓存
        // 示例：保存必要数据
        
        logger.info("系统资源清理完成");
    }
    
    /**
     * 添加在线用户
     * @param sessionId 会话ID
     */
//    public static void addOnlineUser(String sessionId) {
//        onlineUsers.add(sessionId);
//    }
    
    /**
     * 移除在线用户
     * @param sessionId 会话ID
     */
//    public static void removeOnlineUser(String sessionId) {
//        onlineUsers.remove(sessionId);
//    }
    
    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
//    public static int getOnlineUserCount() {
//        return onlineUsers.size();
//    }
}