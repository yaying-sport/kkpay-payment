package com.kaiserkalep.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kaiserkalep.utils.NetWorkUtils;

import androidx.annotation.Nullable;

/**
 * 网络监听
 *
 * @Auther: Jack
 * @Date: 2020/2/27 16:36
 * @Description:
 */
public class NetWorkService extends IntentService {

    public NetWorkService() {
        super("NetWorkService");
    }

    public static void startMyService(Context context) {
        Intent intent = new Intent(context, NetWorkService.class);
        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            NetWorkUtils.ping();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("config", "Configuration load failure!!!");
        }
    }
}
