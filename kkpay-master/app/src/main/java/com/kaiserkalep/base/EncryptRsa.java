package com.kaiserkalep.base;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptRsa {
    // 数字签名，密钥算法
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String RSA_TYPE_CIPHER_ANDROID = "RSA/ECB/PKCS1Padding";
    private static final String RSA_TYPE_CIPHER_JAVA = "RSA/None/PKCS1Padding";
    private static String pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCodhipA7Ptytbo2GuJ7DK9FU31H3X2NYKCcvkMVydMTVVdu5TaWaZ6RPYUEieevcfqm8zcAiyZ+Xu35Gm8OvY9+Y5mtWp26XxdF9yTNBGIHx721oOC0K/bRiy9ypVhLyj7NlNRSEdDjmRIBoxdyDXg58XaSysSqRQt/0k0M65K2QIDAQAB";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 公钥加密
     *
     * @param data    待加密数据
     * @param pub_key 公钥
     * @return 密文
     * @throws Exception 抛出异常
     */
    private static byte[] encryptByPubKey(byte[] data, byte[] pub_key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(RSA_TYPE_CIPHER_JAVA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data    待加密数据
     * @return 密文
     * @throws Exception 抛出异常
     */
    public static String encryptByPubKey(String data) {
        try {
            // 公匙加密
            byte[] pub_key_bytes = Base64.decode(pub_key.getBytes(), Base64.DEFAULT);
            byte[] enSign = encryptByPubKey(data.getBytes(), pub_key_bytes);
            return Base64.encodeToString(enSign, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 公钥解密
     *
     * @param data    待解密数据
     * @return 明文
     * @throws Exception 抛出异常
     */
    public static String decryptByPubKey(String data) {
        try {
            // 公匙解密
            byte[] pub_key_bytes = Base64.decode(pub_key.getBytes(), Base64.DEFAULT);
            byte[] design = decryptByPubKey(Base64.decode(data.getBytes(), Base64.DEFAULT), pub_key_bytes);
            return new String(design);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 公钥解密
     *
     * @param data    待解密数据
     * @param pub_key 公钥
     * @return 明文
     * @throws Exception 抛出异常
     */
    private static byte[] decryptByPubKey(byte[] data, byte[] pub_key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(RSA_TYPE_CIPHER_ANDROID);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
}
