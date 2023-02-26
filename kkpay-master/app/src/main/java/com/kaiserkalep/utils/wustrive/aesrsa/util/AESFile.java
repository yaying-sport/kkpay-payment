package com.kaiserkalep.utils.wustrive.aesrsa.util;

import com.kaiserkalep.utils.CommonUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Auther: Jack
 * @Date: 2020/4/23 18:58
 * @Description:
 */
public class AESFile {

    private static final int ZERO = 0;//加密
    private static final int ONE = 1;//解密

    /**
     * 文件处理方法
     * code为加密或者解密的判断条件
     * file 密文文件
     * key 加密密钥
     * ivParameterm iv向量
     */
    private static void doFile(int code, String filePath, String key, String ivParameterm) throws Exception {
        File file = new File(filePath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] bytIn = new byte[(int) file.length()];
        bis.read(bytIn);
        bis.close();
        // AES加密
        byte[] raw = key.getBytes("ISO8859-1");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Cipher cipher;
        byte[] bytes;
        if (CommonUtils.StringNotNull(ivParameterm)) {
            bytes = ivParameterm.getBytes();
//        } else {
//            cipher = Cipher.getInstance("AES/ECB/NoPadding");
//            if (0 == code) {
//                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//            } else if (1 == code) {
//                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            }
        } else {
            bytes = new byte[16];
        }
        cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(bytes);
        if (0 == code) {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        } else if (1 == code) {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        }
        // 写文件
        byte[] bytOut = cipher.doFinal(bytIn);
        File outfile = new File(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(outfile));
        bos.write(bytOut);
        bos.close();
    }

    //文件加密
    public static void encryptFile(String filePath, String key, String ivParameter) throws Exception {
        doFile(ZERO, filePath, key, ivParameter);
    }

    //文件解密
    public static void decryptFile(String filePath, String key, String ivParameter) throws Exception {
        doFile(ONE, filePath, key, ivParameter);
    }
}
