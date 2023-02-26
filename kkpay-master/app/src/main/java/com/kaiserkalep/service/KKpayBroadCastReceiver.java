package com.kaiserkalep.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.PushMessageUtils;

public class KKpayBroadCastReceiver extends BroadcastReceiver {

    String TAG = getClass().getSimpleName();
    public static String ACTION_SUPER_PLAYER = "com.KKpayBroadCast.intent.action";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "action = " + action);
        if (ACTION_SUPER_PLAYER.equals(action)) {
            //1 暂停播放  关闭浮窗  关闭后台播放通知
            int a = intent.getIntExtra(StringConstants.CODE, 0);
            Log.e(TAG, "CODE = " + a);
            switch (a) {
                case 0:
                    //如果在后台运行,切换到前台
                    PushMessageUtils.setTopApp(context);
                    break;
                case 1:
//                    if (MyApp.superPlayerView != null) {
//                        MyApp.superPlayerView.canPlayBackground = false;
//                        MyApp.superPlayerView.pause();
//                        MyApp.superPlayerView.closeFloatPlay();
//                        MyApp.superPlayerView.stopPlayerService();
//                    }
                    break;
            }
            CommonUtils.collapseNotificationBar(context);
        }
    }


}
