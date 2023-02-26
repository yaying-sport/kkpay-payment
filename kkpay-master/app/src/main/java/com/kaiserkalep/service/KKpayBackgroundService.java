package com.kaiserkalep.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.UIUtils;

import com.kaiserkalep.R;
import com.kaiserkalep.constants.StringConstants;

public class KKpayBackgroundService extends Service {
    String TAG = getClass().getSimpleName();
    String ChannelId = "KKpayBackgroundService";
    private final static int NOTIFICATION_ID = android.os.Process.myPid();

    public static Intent serviceIntent;

    public static void startService(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, KKpayBackgroundService.class);
//            serviceIntent.putExtra(StringConstants.LIVE_DATA, data);
//            serviceIntent.putExtra(StringConstants.ROOM_TITLE, data.getRoom_title());
            try {
                //Android8.0以上不允许创建后台服务
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            } catch (Exception e) {
                LogUtils.e("KKpayBackgroundService startPlayerService e = " + e.toString());
            }
        }
    }

    public static void stopService(Context context) {
        if (serviceIntent != null) {
            context.stopService(serviceIntent);
        }
        serviceIntent = null;
    }

    Handler mHandler;
    Runnable stopServiceRunnable = new Runnable() {
        @Override
        public void run() {
            stopService(KKpayBackgroundService.this);
        }
    };
    public static Notification notification;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mHandler = new Handler();
        //切到后台超过8小时停止服务
        mHandler.postDelayed(stopServiceRunnable, 1000 * 60 * 60 * 8);


        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ChannelId,
                    ChannelId, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        String titleText = UIUtils.getString(R.string.app_name);
        String contentText = UIUtils.getString(R.string.pendingorder_or_order_underway);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_kkpay_background_service);
        remoteViews.setTextViewText(R.id.title_TV, titleText);
        remoteViews.setTextViewText(R.id.content_TV, contentText);
        //设置多语言图片
        BitmapDrawable bd = (BitmapDrawable) UIUtils.getResource().getDrawable(R.mipmap.ic_launcher);
        remoteViews.setImageViewBitmap(R.id.iv, bd.getBitmap());

        // APP在后台运行 切换到前台
        PendingIntent clickPendingIntent2 = PendingIntent.getBroadcast(this,
                0,//不同事件 requestCode 不能一样
                new Intent(KKpayBroadCastReceiver.ACTION_SUPER_PLAYER)
                        .putExtra(StringConstants.CODE, 0),
                PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.rootView, clickPendingIntent2);

        // 点击关闭按钮 停播
//        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
//                1,
//                new Intent(KKpayBroadCastReceiver.ACTION_SUPER_PLAYER)
//                        .putExtra(StringConstants.CODE, 1),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.closeButton, clickPendingIntent);

//        Intent nfIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfIntent, 0);
        if (notification == null) {
            Notification.Builder nb = new Notification.Builder(this)
                    .setTicker("Nature")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(titleText)
                    .setContentText(contentText)
                    .setContent(remoteViews)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(null)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(clickPendingIntent2);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nb.setChannelId(ChannelId);
            }
            notification = nb.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }
//        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notification);
        LogUtils.e("NOTIFICATION_ID1  " + NOTIFICATION_ID);
        //5秒内要开起 startForeground
        startForeground(NOTIFICATION_ID, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        LogUtils.e("NOTIFICATION_ID2  " + NOTIFICATION_ID);
        //5秒内要开起 startForeground
        startForeground(NOTIFICATION_ID, notification);
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        mHandler.removeCallbacks(stopServiceRunnable);
        mHandler = null;
        stopServiceRunnable = null;
        stopForeground(true);
        super.onDestroy();
    }
}
