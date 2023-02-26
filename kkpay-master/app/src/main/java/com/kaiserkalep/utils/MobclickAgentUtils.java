package com.kaiserkalep.utils;

import com.umeng.analytics.MobclickAgent;

/**
 * 页面统计
 *
 * @Auther: Jack
 * @Date: 2020/1/16 15:23
 * @Description:
 */
public class MobclickAgentUtils {

    public static String startName = "";
    public static String endName = "";


    /**
     * 开始页面统计
     */
    public static void onPageStart(String name) {
        if (CommonUtils.StringNotNull(name, startName) && startName.equals(name)) {
            LogUtils.i("UMLog_页面_重复进入:" + name);
            return;
        }
        LogUtils.i("UMLog_页面_Start:" + name);
        MobclickAgent.onPageStart(name);
        startName = name;
    }

    /**
     * 结束页面统计
     */
    public static void onPageEnd(String name) {
        if (!CommonUtils.StringNotNull(startName)) {
            LogUtils.i("UMLog_页面_退出时无进入:" + name);
            return;
        }
        if (CommonUtils.StringNotNull(endName, name) && endName.equals(name)) {
            LogUtils.i("UMLog_页面_重复退出:" + name);
            return;
        }
        LogUtils.i("UMLog_页面_End:" + name);
        MobclickAgent.onPageEnd(name);
        endName = name;
    }
}
