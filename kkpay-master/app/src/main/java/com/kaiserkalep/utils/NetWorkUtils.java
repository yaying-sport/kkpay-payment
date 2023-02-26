package com.kaiserkalep.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 检查网络是否可用
 *
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 21:33
 * @Description:
 */
public class NetWorkUtils {


    /**
     * 当前网络是否可以上网,默认true
     */
    public static boolean isConnected = true;
    private static ThreadUtils threadUtils;


    public static boolean isNetworkConnected() {
        return isNetworkConnected(MyApp.getApplication());
    }

    /**
     * 是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager
                        .getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable() && isConnected;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static {
        threadUtils = ThreadUtils.initThread(1);
    }

    public static void ping() {
        threadUtils.addRunnable(new Runnable() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                try {
                    Process p = runtime.exec("ping -c 1 www.baidu.com");
                    int ret = p.waitFor();
                    Process p2 = runtime.exec("ping -c 1 www.qq.com");
                    int ret2 = p2.waitFor();
                    Process p3 = runtime.exec("ping -c 1 www.alibaba.com");
                    int ret3 = p3.waitFor();
                    LogUtils.i("Avalible", "Process:" + ret + "__" + ret2 + "__" + ret3);
                    //挂vpn下ping会失败,加上socket连接状态判断
                    boolean b = ret == 0 || ret2 == 0 || ret3 == 0;
                    if (listener != null && b) {
                        listener.succeedCallBack(true);
                        listener = null;
                    }
                    isConnected = b;
                    Thread.sleep(5000);
                    ping();
                } catch (Exception e) {
                    e.printStackTrace();
                    isConnected = false;
                }
            }
        });
    }


    /**
     * 初始网络从无到有,只会调用一次
     */
    private static SucceedCallBackListener<Boolean> listener;

    public static void setListener(SucceedCallBackListener<Boolean> listener) {
        NetWorkUtils.listener = listener;
    }
}

