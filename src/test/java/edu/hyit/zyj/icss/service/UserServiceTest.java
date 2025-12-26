package edu.hyit.zyj.icss.service;

import edu.hyit.zyj.icss.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService测试类
 */
public class UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }
    
    @Test
    public void testRegister() {
        // 测试注册功能
        User user = userService.register("testuser", "password123", "resident");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("resident", user.getRole());
        assertEquals("pending", user.getStatus());
    }
    
    @Test
    public void testRegisterDuplicateUser() {
        // 测试重复注册同一用户名
        userService.register("testuser2", "password123", "resident");
        User user = userService.register("testuser2", "password456", "property");
        assertNull(user); // 应该返回null，因为用户名已存在
    }
    
    @Test
    public void testLogin() {
        // 测试登录功能
        userService.register("testuser3", "password123", "resident");
        User user = userService.login("testuser3", "password123");
        assertNotNull(user);
        assertEquals("testuser3", user.getUsername());
    }
    
    @Test
    public void testLoginWithWrongPassword() {
        // 测试使用错误密码登录
        userService.register("testuser4", "password123", "resident");
        User user = userService.login("testuser4", "wrongpassword");
        assertNull(user); // 应该返回null，因为密码错误
    }
    
    @Test
    public void testLoginNonExistentUser() {
        // 测试登录不存在的用户
        User user = userService.login("nonexistent", "password123");
        assertNull(user); // 应该返回null，因为用户不存在
    }
}