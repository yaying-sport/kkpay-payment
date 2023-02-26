package com.kaiserkalep.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.kaiserkalep.R;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.toast.ToastUtils;


//import androidx.multidex.MultiDexApplication;

public class BaseApp extends Application {

    //以下属性应用于整个应用程序，合理利用资源，减少资源浪费
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;//上下文
    private static long mMainThreadId;//主线程id
    private static Handler mHandler;//主线程Handler
    private static boolean umengInit = false;//友盟初始化是否完成

    @Override
    public void onCreate() {
        setUmengInit(false);
        super.onCreate();

        //对全局属性赋值
        mContext = getApplicationContext();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getMyString(int res) {
        if (null != mContext) {
            return mContext.getResources().getString(res);
        }
        return "";
    }

    public static String getLanguageString(Context context,int res) {
        if (null != context) {
            return context.getString(res);
        }
        return "";
    }

    public static float getMyDimension(int res) {
        return mContext.getResources().getDimension(res);
    }

    public static int getMyColor(int res) {
        return mContext.getResources().getColor(res);
    }

    public static Drawable getMyDrawable(int res) {
        return mContext.getResources().getDrawable(res);
    }

    public static int getMyInteger(int res) {
        return mContext.getResources().getInteger(res);
    }

    public static void setContext(Context mContext) {
        BaseApp.mContext = mContext;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    public static boolean isUmengInit() {
        return umengInit;
    }

    public static void setUmengInit(boolean umengInit) {
        BaseApp.umengInit = umengInit;
    }

    public static void toast(String msg) {
        ToastUtils.show(CommonUtils.StringNotNull(msg) ? msg : getMyString(R.string.toast_common_system_net_error));
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        getMainHandler().postDelayed(r, delayMillis);
    }

    public static void removeCallbacks(Runnable r) {
        getMainHandler().removeCallbacks(r);
    }

    public static void post(Runnable r) {
        getMainHandler().post(r);
    }
}
