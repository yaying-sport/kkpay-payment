package com.kaiserkalep.utils.update.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.ui.activity.SettingActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyDataManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.utils.update.DownloadObserver;
import com.kaiserkalep.utils.update.listener.UpdateDialogListener;
import com.kaiserkalep.widgets.UpdateDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * @author hule
 * @date 2019/7/11 9:34
 * description: 下载更新工具类
 */
public class AppUpdateUtils implements UpdateDialogListener {

    private static final String TAG = "AppUpdateUtils";

    /**
     * context的弱引用
     */
    private final WeakReference<Context> wrfContext;
    /**
     * 系统DownloadManager
     */
    private DownloadManager downloadManager;
    /**
     * 上次下载的id
     */
    private long lastDownloadId = -1;
    /**
     * 更新的实体参数
     */
    private UpdateDate appUpdate;

    /**
     * 下载监听
     */
    private DownloadObserver downloadObserver;
    /**
     * 更新提醒对话框
     */
    private UpdateDialog updateDialog;
    private DownloadHandler downloadHandler;

    /**
     * 配置上下文，必须传
     *
     * @param context 上下文
     */
    public AppUpdateUtils(Context context) {
        wrfContext = new WeakReference<>(context);
    }

    /**
     * 开启下载更新
     *
     * @param appUpdate 更新数据
     * @return
     */
    public UpdateDialog startUpdate(UpdateDate appUpdate) {
        Context context = wrfContext.get();
        if (context == null) {
            throw new NullPointerException("AppUpdateUtils======context不能为null，请先在构造方法中传入！");
        }
        if (appUpdate == null) {
            throw new NullPointerException("AppUpdateUtils======appUpdate不能为null，请配置相关更新信息！");
        }
        this.appUpdate = appUpdate;
        updateDialog = new UpdateDialog(context);
        if (appUpdate.isForce_update() || context instanceof SettingActivity) {//强更 ||设置页手动检查更新
            showDialogAndPro(context);
        } else {//非强更
            if (!isDownloading()) {//非下载中才显示
                updateDialog.setData(appUpdate, this);
                updateDialog.addOnDismissListener(dialog -> destroy());
            } else {//下载中不弹框
                updateDialog = null;
            }
        }
        return updateDialog;
    }

    /**
     * 显示更新弹框
     *
     * @param context
     */
    private void showDialogAndPro(Context context) {
        updateDialog.setData(appUpdate, this);
        updateDialog.addOnDismissListener(dialog -> destroy());
        if (isDownloading()) {//下载中重新显示更新弹框,判断是否下载中,更新下载进度
            long downloadId = SPUtil.getDownloadId();
            if (downloadId >= 0) {
                //初始下载进度条
                if (updateDialog != null && updateDialog.isShowing()) {
                    updateDialog.setProgress(0);
                }
                lastDownloadId = downloadId;
                startDownLoadProgress(context);
            }
        }
    }


    /**
     * 开始下载监听
     *
     * @param context
     */
    private void startDownLoadProgress(Context context) {
        if (downloadHandler == null) {
            downloadHandler = new DownloadHandler(context, downloadObserver, updateDialog,
                    downloadManager, lastDownloadId, appUpdate);
        }
        if (downloadObserver == null) {
            downloadObserver = new DownloadObserver(downloadHandler, downloadManager, lastDownloadId);
            context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"),
                    true, downloadObserver);
        }
    }

    /**
     * 下载apk
     */
    private void downLoadApk() {
        try {
            File downloadFile = getDownloadFile();
            if (downloadFile != null && downloadFile.exists() && downloadFile.isFile()) {//已下载过,直接安装
                //下载的安装包版本
                String versionName = MyDataManager.GetApkVersionName(wrfContext.get(), downloadFile.getPath());
                if (CommonUtils.StringNotNull(versionName)) {//下载完成信息无误才能安装
                    installAppAgain(downloadFile);
                    return;
                }
            }
            Context context = wrfContext.get();
            if (context != null) {
                context = context.getApplicationContext();
                //初始下载进度条
                if (updateDialog != null && updateDialog.isShowing()) {
                    updateDialog.setProgress(0);
                }
                //清除并重置上个任务
                clearCurrentTask();
                // 获取下载管理器
                downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                // 下载地址如果为null,抛出异常
                String downloadUrl = Objects.requireNonNull(appUpdate.getUrl());
                Uri uri = Uri.parse(downloadUrl);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                // 下载中和下载完成显示通知栏
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                //文件名称
                String fileName = context.getString(R.string.my_app_name) + "_" + appUpdate.getVersion() + ".apk";
                // 自定义的下载目录,注意这是涉及到android Q的存储权限，建议不要用getExternalStorageDirectory（）
                File pathFile = MyDataManager.getUpdatePathFile(context);
                request.setDestinationInExternalFilesDir(context, context.getString(R.string.ZZFileCache), fileName);
//                request.setDestinationInExternalPublicDir(pathFile.getPath(), fileName);
                // 清除本地缓存的文件
                deleteApkFile(pathFile);
                // 设置通知栏的标题
                request.setTitle(getAppName());
                // 设置通知栏的描述
                request.allowScanningByMediaScanner();  //准许被系统扫描到
                request.setDescription("正在下载中...");
                // 设置媒体类型为apk文件
                request.setMimeType("application/vnd.android.package-archive");
                // 开启下载，返回下载id
                lastDownloadId = downloadManager.enqueue(request);
                SPUtil.setDownloadId(lastDownloadId);
                // 如需要进度及下载状态，增加下载监听
                startDownLoadProgress(context);
            } else {
                LogUtils.d(TAG, "context==null");
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            // 防止有些厂商更改了系统的downloadManager
            downloadFromBrowse();
        }

    }

    /**
     * 下载前清空本地缓存的文件
     */
    private void deleteApkFile(File destFileDir) {
        if (!destFileDir.exists()) {
            return;
        }
        if (destFileDir.isDirectory()) {
            File[] files = destFileDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteApkFile(f);
                }
            }
        }
        destFileDir.delete();
    }

    /**
     * 获取应用程序名称
     */
    private String getAppName() {
        try {
            Context context = wrfContext.get();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "下载";
    }

    /**
     * 从浏览器打开下载，暂时没有选择应用市场，因为应用市场太多，而且协议不同，无法兼顾所有
     */
    private void downloadFromBrowse() {
        try {
            String downloadUrl = appUpdate.getDownBrowserUrl();
            if (!TextUtils.isEmpty(downloadUrl)) {
                Intent intent = new Intent();
                Uri uri = Uri.parse(downloadUrl);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                wrfContext.get().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "无法通过浏览器下载！");
        }
    }

    /**
     * 清除上一个任务，防止apk重复下载
     */
    public void clearCurrentTask() {
        try {
            if (downloadManager != null && lastDownloadId != -1) {
                downloadManager.remove(lastDownloadId);
                lastDownloadId = -1;
            }
            if (downloadHandler != null) {//重置下载监听
                downloadHandler = null;
            }
            if (downloadObserver != null) {
                downloadObserver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始下载
     */
    @Override
    public void updateDownLoad() {
        if (setInstallPermission()) {
            checkPermissionUpdate();
        }
    }

    /**
     * 重试
     */
    @Override
    public void updateRetry() {
        if (setInstallPermission()) {
            checkPermissionUpdate();
        }
    }

    /**
     * 8.0以上系统设置安装未知来源权限
     */
    public boolean setInstallPermission() {
        if (null != wrfContext && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = this.wrfContext.get().getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                //开启安装未知来源权限
                Uri packageURI = Uri.parse("package:" + MyApp.getContext().getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                wrfContext.get().startActivity(intent);
                return false;
            }
        }
        return true;
    }

    /**
     * 储存卡权限
     */
    private void checkPermissionUpdate() {
        Context context = wrfContext.get();
        if (context != null && context instanceof ZZActivity) {
            MyDataManager.setPermission(((ZZActivity) context), o -> {
                if (o != null && o) {
                    //是否下载中
                    // 开启下载
                    downLoadApk();
                }
            });
        }
    }

    /**
     * 是否正在下载中,下载中赋值manager并注册进度广播
     *
     * @return
     */
    private boolean isDownloading() {
        Context context = wrfContext.get();
        if (context != null && appUpdate != null) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_PAUSED);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                // 下载中
                String currentUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                String down_url = appUpdate.getUrl();
                //下载器修改下载链接为https
                if (CommonUtils.StringNotNull(currentUrl, down_url)) {
                    if (currentUrl.contains("//") && down_url.contains("//")) {
                        String[] split = currentUrl.split("//");
                        String[] split2 = down_url.split("//");
                        if (split.length > 1 && split2.length > 1) {
                            currentUrl = split[1];
                            down_url = split2[1];
                        }
                        if (currentUrl.equals(down_url)) {
                            this.downloadManager = downloadManager;
                            LogUtils.d("下载中");
                            cursor.close();
                            return true;
                        }
                    }
                }
            }
            cursor.close();
        }
        return false;
    }


    @Override
    public void downFromBrowser() {
        // 从浏览器下载
        downloadFromBrowse();
    }

    /**
     * 取消更新
     */
    @Override
    public void cancelUpdate() {
        clearCurrentTask();
    }

    /**
     * 重新安装app
     */
    public void installAppAgain(File apkFile) {
        Context context = wrfContext.get();
        if (context != null) {
            try {
                File downloadFile;
                if (apkFile != null) {
                    downloadFile = apkFile;
                } else {
                    downloadFile = getDownloadFile();
                }
                //校验md5
                if (appUpdate != null) {
                    if (!TextUtils.isEmpty(appUpdate.getMd5())) {
                        boolean md5IsRight = Md5Util.checkFileMd5(appUpdate.getMd5(), apkFile);
                        if (!md5IsRight) {//MD5校验不通过(包含下载包版本不同或被纂改),重新下载安装
                            MyDataManager.deleteUpdateApk(context);
                            SPUtil.reSetUpdate();
                            updateDownLoad();
                            return;
                        }
                    }
                }
                if (downloadFile == null || !downloadFile.exists() || !downloadFile.isFile()) {
                    downLoadApk();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    intent.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
                } else {
                    //Android7.0之后获取uri要用contentProvider
                    Uri apkUri = FileProvider.getUriForFile(context.getApplicationContext(),
                            context.getPackageName() + ".fileProvider", downloadFile);
                    //Granting Temporary Permissions to a URI
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show("请点击通知栏完成应用的安装！");
            }
        }
    }


    /**
     * 获取下载的文件
     *
     * @return file
     */
    private File getDownloadFile() {
        DownloadManager.Query query = new DownloadManager.Query();
        if (downloadManager != null && lastDownloadId >= 0) {
            Cursor cursor = downloadManager.query(query.setFilterById(lastDownloadId));
            if (cursor != null && cursor.moveToFirst()) {
                String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(fileUri)) {
                    Uri parse = Uri.parse(fileUri);
                    if (parse != null) {
                        String apkPath = parse.getPath();
                        if (!TextUtils.isEmpty(apkPath)) {
                            return new File(apkPath);
                        }
                    }
                }
                cursor.close();
            }
        }
        Context context = wrfContext.get();
        if (context != null) {
            File downloadFile = MyDataManager.getDownloadFile(context.getApplicationContext());
            if (downloadFile != null && downloadFile.isFile() && downloadFile.exists()) {
                return downloadFile;
            }
        }
        return null;
    }


    public void destroy() {
        if (downloadObserver != null) {
            downloadObserver.destroy();
        }
    }

    public UpdateDate getAppUpdate() {
        return appUpdate;
    }

}
