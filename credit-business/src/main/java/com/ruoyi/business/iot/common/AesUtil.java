package com.ruoyi.business.iot.common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    // AES 加密相关常量
    private static final String AES_ALGORITHM = "AES";                    // 加密算法名称
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding"; // 加密模式：AES ECB 模式 + PKCS7 填充


    public static String getAesKey(String deviceSn){
        return "SHUANG12345678HE";
    }


    /**
     * 使用 AES ECB 模式和 PKCS7 填充对数据进行加密
     * @param payload 要加密的原始数据
     * @param key     16 字节的 AES 密钥
     * @return 加密后的字节数组
     */
    public static byte[] aesEncrypt(byte[] payload, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(payload);
    }



    /**
     * 使用 AES ECB 模式和 PKCS7 填充对数据进行解密
     * @param encryptedPayload 要解密的加密数据
     * @param key              16 字节的 AES 密钥
     * @return 解密后的原始字节数组
     */
    public static byte[] aesDecrypt(byte[] encryptedPayload, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedPayload);
    }
}
