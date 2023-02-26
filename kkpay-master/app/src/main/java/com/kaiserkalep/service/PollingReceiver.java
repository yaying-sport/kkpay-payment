package com.kaiserkalep.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.kaiserkalep.present.OrderNotifyManager;
import com.kaiserkalep.utils.LogUtils;

public class PollingReceiver extends BroadcastReceiver {

    public static String TAG = "PollingReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG, "调用了onReceive");
        PollingUtils.startExactAgain(context, (int) (OrderNotifyManager.time / 1000), PollingReceiver.class, PollingUtils.ACTION);
        Intent i = PollingService.newIntent(context);
        try {
            //Android8.0以上不允许创建后台服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }
        } catch (Exception e) {
            LogUtils.e(TAG + " startPlayerService e = " + e.toString());
        }
    }

}