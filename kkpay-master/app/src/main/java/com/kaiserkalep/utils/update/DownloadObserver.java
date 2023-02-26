package com.kaiserkalep.utils.update;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.kaiserkalep.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author hule
 * @date 2019/7/11 10:38
 * description:通过ContentObserver监听下载的进度
 */
public class DownloadObserver extends ContentObserver {

    private final String TAG = getClass().getCanonicalName();

    private final Handler handler;
    private Timer timer;

    /**
     * 记录成功或者失败的状态，主要用来只发送一次成功或者失败
     */
    private boolean isEnd = false;

    private final DownloadManager downloadManager;

    private final DownloadManager.Query query;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(Handler handler, DownloadManager downloadManager, long downloadId) {
        super(handler);
        this.handler = handler;
        this.downloadManager = downloadManager;
        query = new DownloadManager.Query().setFilterById(downloadId);
        timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            queryDownloadStatus();
        }
    };

    /**
     * 检查下载的状态
     */
    private void queryDownloadStatus() {
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int currentSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                // 当前进度
                int mProgress;
                if (totalSize != 0) {
                    mProgress = (int) (((currentSize * 1f) / totalSize * 1f) * 100);
                } else {
                    mProgress = 0;
                }
                LogUtils.d(TAG, status + "___" + currentSize + "___" + totalSize);
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        // 下载暂停
                        handler.sendEmptyMessage(DownloadManager.STATUS_PAUSED);
                        LogUtils.d(TAG, "STATUS_PAUSED");
                        break;
                    case DownloadManager.STATUS_PENDING:
                        // 开始下载
                        handler.sendEmptyMessage(DownloadManager.STATUS_PENDING);
                        LogUtils.d(TAG, "STATUS_PENDING");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        // 正在下载，不做任何事情
                        Message message = new Message();
                        message.what = DownloadManager.STATUS_RUNNING;
                        message.arg1 = mProgress;
                        handler.sendMessage(message);
                        LogUtils.d(TAG, "STATUS_RUNNING_" + mProgress);
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (!isEnd) {
                            // 完成
                            handler.sendEmptyMessage(DownloadManager.STATUS_SUCCESSFUL);
                            LogUtils.d(TAG, "STATUS_SUCCESSFUL");
                        }
                        isEnd = true;
                        break;
                    case DownloadManager.STATUS_FAILED:
                        if (!isEnd) {
                            handler.sendEmptyMessage(DownloadManager.STATUS_FAILED);
                            LogUtils.d(TAG, "STATUS_FAILED");
                        }
                        isEnd = true;
                        break;
                    default:
                        LogUtils.d(TAG, "default");
                        break;
                }
            } else {
                LogUtils.d(TAG, "cursor======null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (isEnd) {
                destroy();
            }
        }
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
            if (timer != null) {
                timer.purge();
                timer = null;
            }
        }
    }
}
