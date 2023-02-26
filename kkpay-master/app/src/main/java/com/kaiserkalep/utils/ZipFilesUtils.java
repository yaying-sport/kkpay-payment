package com.kaiserkalep.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Auther: Jack
 * @Date: 2020/5/12 16:38
 * @Description:
 */
public class ZipFilesUtils {


    private static final byte[] BUF = new byte[1024];

    private ZipFilesUtils() {
    }

    ;

    /**
     * 压缩
     *
     * @param files       多文件
     * @param zipFilePath 目标压缩文件路径
     * @throws IOException
     */
    public static void zip(List<File> files, String zipFilePath) throws IOException {
        LogUtils.d("启用ZIP压缩工具 >>>>>>>>>> ");
        if (files == null || files.size() == 0) return;
        if (TextUtils.isEmpty(zipFilePath)) return;
        File zipFile = createFile(zipFilePath);
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        FileInputStream inputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            for (File file : files) {
                LogUtils.d("正在打包文件 -> " + file.getAbsolutePath());
                if (file.exists()) {
                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                    inputStream = new FileInputStream(file);
                    int len;
                    while ((len = inputStream.read(BUF)) > 0) {
                        zipOutputStream.write(BUF, 0, len);
                    }
                    zipOutputStream.closeEntry();
                }
            }
            LogUtils.d("压缩完成 <<<<<<<<<< " + zipFile.getAbsolutePath());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * 压缩（将文件夹打包）
     *
     * @param fileDir     文件夹目录路径
     * @param zipFilePath 目标压缩文件路径
     * @throws IOException
     */
    public static void zipByFolder(String fileDir, String zipFilePath) throws IOException {
        File folder = new File(fileDir);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            List<File> filesList = Arrays.asList(files);
            zip(filesList, zipFilePath);
        }
    }

    /**
     * 解压
     *
     * @param zipFile 压缩文件
     * @param descDir 目标文件路径
     * @return
     * @throws IOException
     */
    public static List<File> unzip(File zipFile, String descDir) throws IOException {
        List<File> files = new ArrayList<>();
        if (!TextUtils.isEmpty(descDir)) {
            LogUtils.d("启用ZIP解压工具 >>>>>>>>>> ");
            if (zipFile.exists() && zipFile.getName().endsWith(".zip")) {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    ZipFile zf = new ZipFile(zipFile);
                    Enumeration entries = zf.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                        String zipEntryName = zipEntry.getName();
                        LogUtils.d("正在解压文件 -> " + zipEntryName);
                        inputStream = zf.getInputStream(zipEntry);
                        String descFilePath = descDir + File.separator + zipEntryName;
                        File descFile = createFile(descFilePath);
                        files.add(descFile);
                        outputStream = new FileOutputStream(descFilePath);
                        int len;
                        while ((len = inputStream.read(BUF)) > 0) {
                            outputStream.write(BUF, 0, len);
                        }
                    }
                    LogUtils.d("解压完成 <<<<<<<<<< " + descDir);
                } finally {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != outputStream) {
                        outputStream.close();
                    }
                }
            }
        }
        return files;
    }

    private static File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    /**
     * 解压assets目录下的zip到指定的路径
     *
     * @param zipFileString ZIP的名称，压缩包的名称：xxx.zip
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void UnZipAssetsFolder(Context context, String zipFileString, String
            outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(context.getAssets().open(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                LogUtils.d(outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    LogUtils.d("Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
}
