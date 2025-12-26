package edu.hyit.zyj.icss.service;


import edu.hyit.zyj.icss.dao.SystemLogDao;
import edu.hyit.zyj.icss.model.SystemLog;

/**
 * 系统日志服务类
 */
public class SystemLogService {
    
    private SystemLogDao systemLogDao = new SystemLogDao();
    
    /**
     * 记录系统操作日志
     * @param userId 用户ID
     * @param module 模块名称
     * @param action 操作名称
     * @param targetId 目标ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param status 状态
     * @param errorMessage 错误信息
     * @return 是否记录成功
     */
    public boolean logOperation(Integer userId, String module, String action, Integer targetId, 
                              String ipAddress, String userAgent, String status, String errorMessage) {
        SystemLog log = new SystemLog();
        log.setUserId(userId);
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        
        return systemLogDao.createLog(log);
    }
    
    /**
     * 记录成功的操作日志
     * @param userId 用户ID
     * @param module 模块名称
     * @param action 操作名称
     * @param targetId 目标ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 是否记录成功
     */
    public boolean logSuccessOperation(Integer userId, String module, String action, Integer targetId, 
                                    String ipAddress, String userAgent) {
        return logOperation(userId, module, action, targetId, ipAddress, userAgent, "success", null);
    }
    
    /**
     * 记录失败的操作日志
     * @param userId 用户ID
     * @param module 模块名称
     * @param action 操作名称
     * @param targetId 目标ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param errorMessage 错误信息
     * @return 是否记录成功
     */
    public boolean logFailedOperation(Integer userId, String module, String action, Integer targetId, 
                                   String ipAddress, String userAgent, String errorMessage) {
        return logOperation(userId, module, action, targetId, ipAddress, userAgent, "failure", errorMessage);
    }
    
    /**
     * 根据ID获取系统日志
     * @param id 日志ID
     * @return SystemLog对象
     */
    public SystemLog getLogById(int id) {
        return systemLogDao.findById(id);
    }
}