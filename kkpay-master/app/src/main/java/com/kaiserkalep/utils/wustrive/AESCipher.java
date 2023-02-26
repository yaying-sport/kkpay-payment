package com.kaiserkalep.utils.wustrive;

import android.util.Base64;

import com.kaiserkalep.bean.PrivateStringData;
import com.kaiserkalep.utils.BASE64Utils;
import com.kaiserkalep.utils.CommonUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密
 *
 * @Auther: Jack
 * @Date: 2019/10/19 15:30
 * @Description:
 */
public class AESCipher {
    private static final String charset = "UTF-8";

    /**
     * 获取加密
     *
     * @param data
     * @return
     */
    public static String getEncrypt(String data) {
        return getEncrypt(data, PrivateStringData.k, true);
    }

    /**
     * 获取加密
     *
     * @param data
     * @return
     */
    public static String getEncrypt(String data, boolean base64) {
        return getEncrypt(data, PrivateStringData.k, base64);
    }

    /**
     * 获取加密
     *
     * @param data
     * @return
     */
    public static String getEncrypt(String data, String k, boolean base64) {
        try {
            if (!CommonUtils.StringNotNull(k, data)) {
                return "";
            }
            return AESCipher.aesEncryptString(data, k, base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取解密
     *
     * @param data
     * @return
     */
    public static String getDecrypt(String data) {
        return getDecrypt(data, true);
    }

    /**
     * 获取解密
     *
     * @param data
     * @return
     */
    public static String getDecrypt(String data, boolean isBase64) {
        return getDecrypt(data, PrivateStringData.k, isBase64);
    }

    /**
     * 获取解密
     *
     * @param data
     * @param isBase64 是否需要base64转码
     * @return
     */
    public static String getDecrypt(String data, String k, boolean isBase64) {
        try {
            if (!CommonUtils.StringNotNull(k, data)) {
                return "";
            }
            return AESCipher.aesDecryptString(data, k, isBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    private static String aesEncryptString(String content, String k, boolean base64) throws Exception {
        byte[] contentBytes = content.getBytes(charset);
        byte[] keyBytes = k.getBytes(charset);
        byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes, k);
        if (base64) {
            return BASE64Utils.encode(new String(MyBase64.encode(encryptedBytes), charset), Base64.NO_PADDING);
        } else {
            return new String(MyBase64.encode(encryptedBytes), charset);
        }
    }

    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    private static String aesDecryptString(String content, String k, boolean isBase64) throws Exception {
        byte[] encryptedBytes = isBase64 ?
                Base64.decode(MyBase64.decode2(content), Base64.NO_PADDING) :
                MyBase64.decode2(content);
        byte[] keyBytes = k.getBytes(charset);
        byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes, k);
        return new String(decryptedBytes, charset);
    }

    private static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes, String k) throws Exception {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE, k);
    }

    private static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes, String k) throws Exception {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE, k);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode, String k) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = k.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ivParameterSpec);

        return cipher.doFinal(contentBytes);
    }

}
