package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.UserDao;
import edu.hyit.zyj.icss.model.User;
import edu.hyit.zyj.icss.util.PasswordUtil;

import java.util.List;

/**
 * 用户服务类
 */
public class UserService {
    
    private UserDao userDao = new UserDao();
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 注册结果：成功返回用户对象，失败返回null
     */
    public User register(String username, String password, String role) {
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
        return userDao.findById(id);
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}