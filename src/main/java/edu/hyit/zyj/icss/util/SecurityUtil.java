package edu.hyit.zyj.icss.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * 安全工具类
 * 提供数据加密、解密、哈希等安全相关功能
 */
public class SecurityUtil {
    
    /**
     * SHA256哈希加密
     * @param data 原始数据
     * @return 加密后的哈希值
     */
    public static String sha256Hash(String data) {
        return DigestUtils.sha256Hex(data);
    }
    
    /**
     * MD5哈希加密
     * @param data 原始数据
     * @return 加密后的哈希值
     */
    public static String md5Hash(String data) {
        return DigestUtils.md5Hex(data);
    }
    
    /**
     * AES加密
     * @param content 待加密内容
     * @return 加密后的内容（Base64编码）
     */
    public static String aesEncrypt(String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(ApplicationConfiguration.getProperty("security.aes.key").getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content.getBytes("utf-8"));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * AES解密
     * @param content 待解密内容（Base64编码）
     * @return 解密后的内容
     */
    public static String aesDecrypt(String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(ApplicationConfiguration.getProperty("security.aes.key").getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 生成随机盐值
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            salt.append(chars.charAt(random.nextInt(chars.length())));
        }
        return salt.toString();
    }
    
    /**
     * 使用盐值进行哈希加密
     * @param data 原始数据
     * @param salt 盐值
     * @return 加密后的哈希值
     */
    public static String hashWithSalt(String data, String salt) {
        return sha256Hash(data + salt);
    }
}