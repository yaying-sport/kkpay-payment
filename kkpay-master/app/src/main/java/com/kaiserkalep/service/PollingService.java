package com.kaiserkalep.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.kaiserkalep.ui.activity.MainActivity;

public class PollingService extends IntentService {

    private static final String TAG = "PollingService";

    public PollingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (KKpayBackgroundService.notification != null) {
            startForeground(android.os.Process.myPid(), KKpayBackgroundService.notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 做你想做的事情，比如说发送请求
        if (MainActivity.instance != null) {
            MainActivity.instance.unReadOrderNotice();
        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PollingService.class);
        return intent;
    }


}
