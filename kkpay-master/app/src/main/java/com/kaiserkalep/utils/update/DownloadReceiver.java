package com.kaiserkalep.utils.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.base.ActivityBase;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDataManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.utils.update.utils.Md5Util;

import java.io.File;
import java.util.Objects;

import androidx.core.content.FileProvider;

/**
 * @author hule
 * @date 2019/7/11 10:34
 * description:DownloadManager下载广播接收器，需要在xml中注册,
 * 主要实现通知栏显示下载完成自动安装，或者通知栏点击跳转到系统的下载管理器界面
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                // 下载完成
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                long id = SPUtil.getDownloadId();

                if (downloadId >= 0 && id >= 0 && id == downloadId) {//清除下载中任务id
                    SPUtil.setDownloadId(-1);
                }
                // 自动安装app
                MyApp.postDelayed(() -> installApp(context, downloadId), 800);
            }
        }
    }

    /**
     * 跳转安装
     *
     * @param context
     * @param downloadId
     */
    private void installApp(Context context, long downloadId) {
        ActivityBase currentActivity = MyActivityManager.getActivityManager().getCurrentActivity();
        if (currentActivity != null) {
            if (currentActivity instanceof ZZActivity) {
                ((ZZActivity) currentActivity).installApp(downloadId);
            }
        } else {
            try {
                if (downloadId == -1) {
                    return;
                }
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                Cursor cursor = downloadManager.query(query.setFilterById(downloadId));
                if (cursor != null && cursor.moveToFirst()) {
                    String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    String path = Uri.parse(fileUri).getPath();
                    cursor.close();
                    if (!TextUtils.isEmpty(path)) {
                        File apkFile = null;
                        //下载的安装包版本
                        String versionName = MyDataManager.GetApkVersionName(context, path);
                        if (CommonUtils.StringNotNull(versionName)) {//下载完成信息无误才能安装
                            apkFile = new File(path);
                        }
                        String downBrowserUrl = SPUtil.getDownBrowserUrl();
                        if (apkFile == null || !apkFile.exists() || !apkFile.isFile()) {
                            downError(context, downBrowserUrl);
                            return;
                        }
                        String md5 = SPUtil.getUpdateMd5();
                        if (CommonUtils.StringNotNull(md5)) {
                            boolean md5IsRight = Md5Util.checkFileMd5(md5, apkFile);
                            if (!md5IsRight) {//MD5校验不通过,重新下载安装
                                downError(context, downBrowserUrl);
                                return;
                            }
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        } else {
                            //Android7.0之后获取uri要用contentProvider
                            Uri apkUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileProvider", apkFile);
                            //Granting Temporary Permissions to a URI
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void downError(Context context, String downBrowserUrl) {
        MyDataManager.deleteUpdateApk(context);
        Intent webIntent = IntentUtils.getWebIntent(downBrowserUrl);
        if (webIntent != null) {
            ToastUtils.show("为了安全性和更好的体验，为你推荐浏览器下载更新！");
            context.startActivity(webIntent);
        }
    }
}
