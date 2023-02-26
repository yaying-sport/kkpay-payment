package com.kaiserkalep.utils.wustrive.aesrsa.util;

import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Auther: Jack
 * @Date: 2019/10/19 16:12
 * @Description:
 *
 * AES加密解密字符串工具类
 * 概述：AES高级加密标准，是对称密钥加密中最流行的算法之一；
 *       工作模式包括：ECB、CBC、CTR、OFB、CFB；
 * 使用范围：该工具类仅支持CBC模式下的：
 *              填充：PKCS7PADDING
 *              数据块：128位
 *              密码（key）：32字节长度（例如：12345678901234567890123456789012）
 *              偏移量（iv）：16字节长度（例如：1234567890123456）
 *              输出：hex
 *              字符集：UTF-8
 * 使用方式：String encrypt = AESCBCUtil.encrypt("wy");
 *           String decrypt = AESCBCUtil.decrypt(encrypt);
 * 验证方式：http://tool.chacuo.net/cryptaes（在线AES加密解密）
 * <p>
 */
public class AESCBCUtil {
    //密码
    private static final String key = "6byN8ymx10DsRHM42k82E1GUT041F317";

    //iv偏移量
    private static final String iv = "5fgjDRk7FGH3tF";

    public static final String CHAR_ENCODING = "UTF-8";
    
    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    

    public static void main(String[] args) throws Exception {
        String ss = "hello word";
        System.out.print("加密前:" + ss);
        String encrypt = encrypt(ss);
        System.out.print("\n加密后:" + encrypt);
        String decrypt = decrypt(encrypt);
        System.out.print("\n解密后:" + decrypt);
    }




    /**
     * 加密：对字符串进行加密，并返回十六进制字符串(hex)
     *
     * @param encryptStr 需要加密的字符串
     * @return 加密后的十六进制字符串(hex)
     */
    public static String encrypt(String encryptStr) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(CHAR_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHAR_ENCODING), "AES");

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(encryptStr.getBytes());

            return byte2HexStr(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 解密：对加密后的十六进制字符串(hex)进行解密，并返回字符串
     *
     * @param encryptedStr 需要解密的，加密后的十六进制字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedStr) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(CHAR_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CHAR_ENCODING), "AES");


            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] bytes = hexStr2Bytes(encryptedStr);
            byte[] original = cipher.doFinal(bytes);

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 十六进制字符串转换为byte[]
     *
     * @param hexStr 需要转换为byte[]的字符串
     * @return 转换后的byte[]
     */
    public static byte[] hexStr2Bytes(String hexStr) {
        /*对输入值进行规范化整理*/
        hexStr = hexStr.trim().replace(" ", "").toUpperCase(Locale.US);
        //处理值初始化
        int m = 0, n = 0;
        int iLen = hexStr.length() / 2; //计算长度
        byte[] ret = new byte[iLen]; //分配存储空间

        for (int i = 0; i < iLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte) (Integer.decode("0x" + hexStr.substring(i * 2, m) + hexStr.substring(m, n)) & 0xFF);
        }
        return ret;
    }

    /**
     * byte[]转换为十六进制字符串
     *
     * @param bytes 需要转换为字符串的byte[]
     * @return 转换后的十六进制字符串
     */
    public static String byte2HexStr(byte[] bytes) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = (Integer.toHexString(bytes[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs;
    }
}
