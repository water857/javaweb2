package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.UserDao;
import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.util.DataSourceUtil;
import edu.hyit.zyj.icss.util.PasswordUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户服务类
 */
public class UserService {
    
    private UserDao userDao = new UserDao();
    private final Map<Integer, User> inMemoryUsers = new HashMap<>();
    private final Map<String, Integer> usernameIndex = new HashMap<>();
    private final AtomicInteger userIdGenerator = new AtomicInteger(1);
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 注册结果：成功返回用户对象，失败返回null
     */
    public User register(String username, String password, String role) {
        if (!isDatabaseAvailable()) {
            if (usernameIndex.containsKey(username)) {
                return null;
            }
            User user = new User(username, PasswordUtil.encrypt(password), role);
            user.setId(userIdGenerator.getAndIncrement());
            inMemoryUsers.put(user.getId(), user);
            usernameIndex.put(username, user.getId());
            return user;
        }
        // 检查用户名是否已存在
        if (userDao.findByUsername(username) != null) {
            return null; // 用户名已存在
        }
        
        // 创建新用户
        User user = new User(username, PasswordUtil.encrypt(password), role);
        if (userDao.createUser(user)) {
            return user;
        }
        return null;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果：成功返回用户对象，失败返回null
     */
    public User login(String username, String password) {
        if (!isDatabaseAvailable()) {
            Integer userId = usernameIndex.get(username);
            if (userId == null) {
                return null;
            }
            User user = inMemoryUsers.get(userId);
            if (user != null && PasswordUtil.verify(password, user.getPassword())) {
                user.setLastLoginTime(new Date());
                return user;
            }
            return null;
        }
        User user = userDao.findByUsername(username);
        if (user != null && PasswordUtil.verify(password, user.getPassword())) {
            // 更新最后登录时间
            userDao.updateLastLoginTime(user.getId());
            return user;
        }
        return null;
    }
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return User对象
     */
    public User getUserById(int id) {
        if (!isDatabaseAvailable()) {
            return inMemoryUsers.get(id);
        }
        return userDao.findById(id);
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(User user) {
        if (!isDatabaseAvailable()) {
            if (user.getId() == null || !inMemoryUsers.containsKey(user.getId())) {
                return false;
            }
            inMemoryUsers.put(user.getId(), user);
            usernameIndex.put(user.getUsername(), user.getId());
            return true;
        }
        return userDao.updateUser(user);
    }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        if (!isDatabaseAvailable()) {
            return new ArrayList<>(inMemoryUsers.values());
        }
        return userDao.getAllUsers();
    }

    private boolean isDatabaseAvailable() {
        return DataSourceUtil.isInitialized();
    }
}
