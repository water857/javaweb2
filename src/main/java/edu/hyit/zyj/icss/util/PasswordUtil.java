package edu.hyit.zyj.icss.util;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        // 使用SHA256加密密码
        return SecurityUtil.sha256Hash(password);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        // 对原始密码进行加密并与存储的密码比较
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}