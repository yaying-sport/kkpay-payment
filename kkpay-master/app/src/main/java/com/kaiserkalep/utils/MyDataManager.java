package com.kaiserkalep.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.service.StartAdService;
import com.kaiserkalep.utils.skinlibrary.SkinConfig;
import com.kaiserkalep.utils.skinlibrary.utils.SkinFileUtils;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Auther: Administrator
 * @Date: 2019/4/17 0017 16:32
 * @Description:
 */
public class MyDataManager {
    /**
     * 获取本应用外部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    public static String getInternalCache(Context context) {
        return getFilesByDirectory(context.getApplicationContext(),
                new File(context.getCacheDir(), context.getString(R.string.ZZImageCache)));
    }


    /**
     * 清除所有缓存数据
     */
    public static void deleteAllCache(Context context) {
        deleteNetWorkCache(context);//接口缓存
        GlideUtil.cleanDiskCache(context);//图片缓存
    }


    /**
     * 清除更新下载的apk
     *
     * @param context
     */
    public static void deleteUpdateApk(Context context) {
        new Thread(() -> {//下载apk
            deleteDir(getUpdatePathFile(context));
        }).start();
    }


    /**
     * 获取apk下载目录
     *
     * @param context
     * @return
     */
    public static File getUpdatePathFile(Context context) {
        //拿到SD卡（外部存储设备，不一定是sd卡）的文件目录
        return context.getExternalFilesDir(context.getString(R.string.ZZFileCache));
    }


    /**
     * 清除网络接口缓存
     */
    public static void deleteNetWorkCache(Context context) {
        new Thread(() -> deleteDir(new File(context.getCacheDir(),
                context.getString(R.string.ZZNetWorkCache)))).start();
    }

    /**
     * 清除data/user/cache下所有数据
     */
    public static void deleteAllData(Context context) {
        new Thread(() -> deleteDir(new File(context.getCacheDir(),
                context.getString(R.string.ZZRootCache)))).start();
    }


    /**
     * 清除ad
     *
     * @param context
     */
    public static void deleteAD(Context context) {
        new Thread(() -> {
            deleteDir(new File(context.getCacheDir(), context.getString(R.string.ZZImageAdCache)));
        }).start();
    }


    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        //判断是否有SD卡
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return false;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                //递归删除目录中的子目录下
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * 获取外部缓存文件
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDirectory(Context context) {
        File cacheDirectory = null;
        try {
            String diskName = context.getString(R.string.ZZRootCache);
//            cacheDirectory = context.getExternalCacheDir();
            cacheDirectory = context.getCacheDir();
            return new File(cacheDirectory, diskName);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("cacheDirectory目录为空");
        }
        return cacheDirectory;
    }


    /**
     * 获取外部 根据指定的路径缓存文件
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDirectory(Context context, int path) {
        File cacheDirectory = null;
        try {
            String diskName = context.getString(path);
            cacheDirectory = context.getCacheDir();
            return new File(cacheDirectory, diskName);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("cacheDirectory目录为空");
        }
        return cacheDirectory;
    }


    /**
     * 获取外部缓存目录
     *
     * @param context
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        String cacheDirectory = "";
        try {
            cacheDirectory = getExternalCacheDirectory(context).getAbsolutePath();
            return cacheDirectory;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("cacheDirectory目录为空");
        }
        return cacheDirectory;
    }


    /**
     * 获取多个目录文件大小
     *
     * @param context
     * @param files
     * @return
     */
    public static String getFilesByDirectory(Context context, File... files) {
        long blockSize = 0;
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file != null) {
                    blockSize += getFilesByDirectory(file, context);
                }
            }
        }
        return FormetFileSize(blockSize);
    }


    /**
     * 获取多个目录文件大小
     *
     * @param context
     * @param files
     * @return
     */
    public static long getFilesByDirectorySize(Context context, File... files) {
        long blockSize = 0;
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file != null) {
                    blockSize += getFilesByDirectory(file, context);
                }
            }
        }
        return blockSize;
    }


    /**
     * 获取目录文件大小
     *
     * @param file
     * @param context
     * @return
     */
    private static long getFilesByDirectory(File file, Context context) {
        long blockSize = 0;
        if (!file.exists()) {
            file = new File(context.getString(R.string.ZZRootCache));
        }
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return blockSize;
    }


    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */

    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 获取文件的大小
     *
     * @param file
     * @return
     */
    public static String getFileSize(File file) {
        long fileSizeSize = getFileSizeSize(file);
        return fileSizeSize > 0 ? FormetFileSize(fileSizeSize) : "";
    }

    /**
     * 获取文件的大小
     *
     * @param file
     * @return
     */
    public static long getFileSizeSize(File file) {
        long blockSize = 0;
        try {
            if (file != null && file.exists()) {
                blockSize = getFileSizes(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return blockSize;
    }

    public static String DEFAULT_FILE_SIZE = "0.00M";

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    public static String GetApkVersionName(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                String packageName = appInfo.packageName;  //得到安装包名称
                String version = info.versionName;//获取安装包的版本号
                LogUtils.i("GetApkVersionName: " + packageName + "-------" + version);
                return version;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取下载的文件
     *
     * @return file
     */
    public static File getDownloadFile(Context context) {
        File file = getUpdatePathFile(context);
        if (file.isDirectory()) {
            File[] flist = file.listFiles();
            if (flist.length > 0) {
                if (flist[0].isFile()) {
                    return new File(file, flist[0].getName());
                }
            }
        }
        return null;
    }

    /**
     * app版本更新清除缓存/上个版本文件结构不同等等
     */
    public static void cleanVersion(Context context) {
        int versionCode = SPUtil.getIntValue(SPUtil.VERSION_CODE);
        int nVersionCode = AppUtils.getAppVersionCode(context);
        boolean isUpdate = versionCode != nVersionCode; //首次安装和版本变化都为true
        if (isUpdate) { //更新启动
            //清除缓存数据...
            SPUtil.setIntValue(SPUtil.VERSION_CODE, nVersionCode);
        }
        deleteUpdateApkFile(context);
    }


    /**
     * 删除更新文件
     */
    private static void deleteUpdateApkFile(Context context) {
        try {
            File downloadFile = MyDataManager.getDownloadFile(context);
            if (downloadFile == null || !downloadFile.isFile() || !downloadFile.exists()) {
                SPUtil.reSetUpdate();
                return;
            }
            //下载的安装包版本
            String versionName = MyDataManager.GetApkVersionName(context, downloadFile.getPath());
            if (versionName == null || TextUtils.isEmpty(versionName)) {
                long downloadId = SPUtil.getDownloadId();
                if (downloadId >= 0) {
                    return;//下载中不清除
                }
                SPUtil.reSetUpdate();
                return;
            }
            if (!versionName.contains(".")) {
                SPUtil.reSetUpdate();
                return;
            }
            //安装包版本 2.1.0
            int i = NumberUtils.stringToInt(versionName.replace(".", ""));
            String version = ClientInfo.VER;
            if (!version.contains(".")) {
                SPUtil.reSetUpdate();
                return;
            }
            //当前已安装版本 2.2.1
            int k = NumberUtils.stringToInt(version.replace(".", ""));
            if (k >= i) {//已更新或安装包是低版本,删除更新包,重置安装相关数据
                if (MyApp.isDebug) {
                    return;//测试版不删除
                }
                LogUtils.d("删除更新包");
                MyDataManager.deleteUpdateApk(context);
                SPUtil.reSetUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除过期广告
     *
     * @param context
     * @param deleteImageUrls
     */
    public static void deleteOverdueAd(Context context, List<String> deleteImageUrls) {
        if (context != null && CommonUtils.ListNotNull(deleteImageUrls)) {
            new Thread(() -> {
                for (String url : deleteImageUrls) {
                    if (CommonUtils.StringNotNull(url)) {
                        try {
                            url = URLEncoder.encode(url, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        MyDataManager.deleteDir(new File(context.getCacheDir(), context.getString(R.string.ZZImageAdCache) +
                                File.separator + url));
                    }
                }
            }).start();
        }
    }

    /**
     * 下载广告图片
     *
     * @param loadImageUrls
     */
    public static void downLoadImage(Context context, List<String> loadImageUrls, SucceedCallBackListener<Boolean> listener) {
        if (context != null && CommonUtils.ListNotNull(loadImageUrls)) {
            StartAdService.startMyService(context, loadImageUrls);
        }
    }

    /**
     * 获取内存卡读写权限
     *
     * @param activity
     * @param listener
     */
    public static void setPermission(Activity activity, SucceedCallBackListener<Boolean> listener) {
        setPermission(activity, listener, activity.getString(R.string.storage_jurisdiction));
    }

    /**
     * 获取内存卡读写权限
     *
     * @param activity
     * @param listener
     */
    public static void setPermission(Activity activity, SucceedCallBackListener<Boolean> listener, String s) {
        RxPermissions permissions = new RxPermissions((FragmentActivity) activity);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (listener != null) {
                    listener.succeedCallBack(aBoolean);
                }
                if (!aBoolean) {
                    ToastUtils.show(s);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }


    /**
     * 清除皮肤缓存
     *
     * @param context
     */
    public static void clearSkinFile(Context context) {
        try {
            String[] skinFiles = context.getAssets().list(SkinConfig.SKIN_DIR_NAME);
            if (skinFiles != null && skinFiles.length > 0) {
                for (String fileName : skinFiles) {
                    File file = new File(SkinFileUtils.getSkinDir(context), fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}