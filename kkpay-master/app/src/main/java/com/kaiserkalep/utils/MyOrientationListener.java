package com.kaiserkalep.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

import com.kaiserkalep.interfaces.SucceedCallBackListener;


/**
 *  横竖屏切换陀螺仪
 *
 * @Auther: Jack
 * @Date: 2020/9/11 11:17
 * @Description:
 */
public class MyOrientationListener extends OrientationEventListener {

    private Activity context;
    private SucceedCallBackListener<Integer> listener;
    private int currentOrientation;
    private int landscape;//0,正常  1,竖屏下点全屏tag  2,横屏下点返回tag
    private long time1, time2, time3;
    private int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public MyOrientationListener(Activity context, SucceedCallBackListener<Integer> listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        time1 = System.currentTimeMillis();
        time2 = System.currentTimeMillis();
        time3 = System.currentTimeMillis();
    }

    public void setCurrentOrientation(int landscape) {
        this.landscape = landscape;
    }

    private static long INTERVAL = 500L; // 防止连续点击的时间间隔
    public static long lastClickTime = 0L; // 上一次点击的时间


    public boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime) > INTERVAL) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }

    @Override
    public void onOrientationChanged(int orientation) {
//        int abs = Math.abs(Math.abs(currentOrientation) - Math.abs(orientation));
//        LogUtils.d("横竖屏切换__角度__" + orientation + "__" + currentOrientation + "__" + abs
//                + "__时间__" + DateUtils.covertDateToString(System.currentTimeMillis(), DateUtils.DATE_FORMAT_HOURS));

        if (orientation == ORIENTATION_UNKNOWN) return;

//        if (((LivePlayActivity) context).isInter()) return;

        if (orientation < 0 /*|| abs <= 20*/) {
            return;
        }
        if (landscape != 0) {
            if (landscape == 1) {
                if (orientation > 80 && orientation <= 100 ||
                        orientation > 260 && orientation <= 280) {
                    landscape = 0;
                }
            } else if (landscape == 2) {
                if (orientation >= 345 || orientation <= 15) {
                    landscape = 0;
                }
            }
            return;
        }
        if (orientation >= 345 || orientation <= 15) {//设置竖屏
            time1 = System.currentTimeMillis();
            if (getOrienSpeed(time1, time2) > 0 || getOrienSpeed(time1, time3) > 0) {
                LogUtils.d("设置竖屏__时间间隔");
                return;
            }
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                currentOrientation = orientation;
                if (listener != null) {
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    listener.succeedCallBack(screenOrientation);
                }
            }
        } else if (orientation > 260 && orientation <= 280) { //设置横屏
            time2 = System.currentTimeMillis();
            if (getOrienSpeed(time1, time2) > 0) {
                LogUtils.d("设置横屏__时间间隔");
                return;
            }
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                currentOrientation = orientation;
                if (listener != null) {
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    listener.succeedCallBack(screenOrientation);
                }
            }
        } else if (orientation > 80 && orientation <= 100) {// 设置反向横屏
            time3 = System.currentTimeMillis();
            if (getOrienSpeed(time1, time3) > 0) {
                LogUtils.d("设置反向横屏__时间间隔");
                return;
            }
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                currentOrientation = orientation;
                if (listener != null) {
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    listener.succeedCallBack(screenOrientation);
                }
            }
        }
//        else if (orientation > 135 && orientation < 225) {
//            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
//                if (listener != null) {
//                    listener.succeedCallBack(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                }
//                LogUtils.d("反向竖屏");
//            }
//        }
    }

    private long getOrienSpeed(long time1, long time2) {
        long time = Math.abs(time1 - time2);
        if (time == 0) {
            return 0;
        }
        return 1200 / time;
    }
}
