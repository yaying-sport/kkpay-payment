package com.kaiserkalep.utils;

public class JudgeDoubleUtils {

    private static long lastClickTime;
    private static long lastUpdataTime;
    private static long lastCallOtherTime;

    /**
     * 控制点击控件速度。
     * true 双击拦截时间内
     * false 允许点击
     */
    public static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1200) {       //1200毫秒内按钮无效，这样可以控制快速点击，自己调整频率
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 控制点击控件速度。
     * true 双击拦截时间内
     * false 允许点击
     */
    public static boolean isCallOtherClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastCallOtherTime;
        if (0 < timeD && timeD < 5000) {       //5000毫秒内按钮无效，这样可以控制快速点击，自己调整频率
            return true;
        }
        lastCallOtherTime = time;
        return false;
    }

    /**
     * 域名错误轮询后，上报时间间隔
     * true 时间间隔内，不上报
     * false 已过时间，可用上报
     */
    public static boolean updataDomianLogClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastUpdataTime;
        if (0 < timeD && timeD < 8 * 60 * 1000) {       //8分钟内无效
            return true;
        }
        lastUpdataTime = time;
        return false;
    }

    /**
     * 初始化lastClickTime时间
     */
    public static void setLastClickTime() {
        lastClickTime = System.currentTimeMillis();
    }
}
