package com.kaiserkalep.utils.update.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.utils.update.DownloadObserver;
import com.kaiserkalep.widgets.UpdateDialog;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

/**
 * @author hule
 * @date 2019/7/15 16:43
 * description:下载监听handler
 */
public class DownloadHandler extends Handler {

    private final String TAG = getClass().getSimpleName();

    /**
     * 弱引用上下文
     */
    private final WeakReference<Context> wrfContext;
    /**
     * 弱引用DownloadObserver
     */
    private final WeakReference<DownloadObserver> wrfDownloadObserver;
    /**
     * 弱引用进度对话框
     */
    private final WeakReference<UpdateDialog> wrfUpdateProgressDialog;
    /**
     * 弱引用与前台的通讯
     */
    /**
     * 下载的id
     */
    private final long lastDownloadId;
    /**
     * 下载的管理器
     */
    private final DownloadManager downloadManager;
    /**
     * 更新实体
     */
    private final UpdateDate appUpdate;


    public DownloadHandler(Context context, DownloadObserver downloadObserver, UpdateDialog progressDialog,
                           DownloadManager downloadManager, long lastDownloadId, UpdateDate appUpdate) {
        wrfContext = new WeakReference<>(context);
        wrfDownloadObserver = new WeakReference<>(downloadObserver);
        wrfUpdateProgressDialog = new WeakReference<>(progressDialog);
        this.lastDownloadId = lastDownloadId;
        this.downloadManager = downloadManager;
        this.appUpdate = appUpdate;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case DownloadManager.STATUS_PAUSED:
                //设置下载暂停状态
                if (wrfContext.get() != null && wrfUpdateProgressDialog.get() != null) {
                    wrfUpdateProgressDialog.get().setReUpdate();
                }
                break;
            case DownloadManager.STATUS_PENDING:
                // 开始
                break;
            case DownloadManager.STATUS_RUNNING:
                // 下载中
                if (wrfUpdateProgressDialog.get() != null) {
                    wrfUpdateProgressDialog.get().setProgress(msg.arg1);
                }
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                if (wrfUpdateProgressDialog.get() != null) {
                    wrfUpdateProgressDialog.get().setProgress(100);
                }
                // 取消监听的广播
                if (wrfContext.get() != null && wrfDownloadObserver.get() != null) {
                    wrfContext.get().getContentResolver().unregisterContentObserver(wrfDownloadObserver.get());
                }
//                //下载成功,做300ms安装延迟
//                postDelayed(this::downloadSuccess, 300);
                downloadSuccess();
                break;
            case DownloadManager.STATUS_FAILED:
                try {
                    // 下载失败，清除本次的下载任务
                    if (lastDownloadId != -1) {
                        downloadManager.remove(lastDownloadId);
                    }
                    // 取消监听的广播
                    if (wrfContext.get() != null && wrfDownloadObserver.get() != null) {
                        wrfContext.get().getContentResolver().unregisterContentObserver(wrfDownloadObserver.get());
                    }
                    //设置下载失败状态
                    if (wrfContext.get() != null && wrfUpdateProgressDialog.get() != null) {
                        wrfUpdateProgressDialog.get().setErrorUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 下载成功
     */
    private void downloadSuccess() {
        // 下载框回复为按钮
        UpdateDialog updateDialog = wrfUpdateProgressDialog.get();
        if (updateDialog != null && updateDialog.isShowing()) {
            if (appUpdate.isForce_update()) {//是否是强更
                updateDialog.setForceUpdate();
            } else {
                updateDialog.setNormalUpdate();
            }
        }
    }
}
