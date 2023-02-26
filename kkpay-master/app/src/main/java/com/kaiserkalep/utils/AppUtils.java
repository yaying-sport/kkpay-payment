package com.kaiserkalep.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 22:23
 * @Description:
 */
public class AppUtils {
    /**
     * 安装apk
     * 实际上是隐式意图启动系统安装界面
     *
     * @param file
     */
    public static void installAPK(File file, Context context) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri data = Uri.fromFile(file);
        installIntent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }


    /**
     * @return
     * @Description:获取当前应用的名称
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }


    public static int getAppVersionCode(Context context) {
        int nVersionCode = -1;
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            nVersionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nVersionCode;
    }


    public static String getVersionName(Context context) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(String processName, Context context) {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(context, pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if (processName.equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
}

