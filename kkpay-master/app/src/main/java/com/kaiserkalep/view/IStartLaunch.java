package com.kaiserkalep.view;


import com.kaiserkalep.bean.AdGuideData;
import com.kaiserkalep.interfaces.ActivityIntentInterface;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * 启动页
 *
 * @Auther: Jack
 * @Date: 2019/10/21 09:18
 * @Description:
 */
public interface IStartLaunch extends ActivityIntentInterface {


    /**
     * 设置倒计时
     *
     * @param s
     */
    void setTimeText(int over_status, String s);

    /**
     * 已有数据直接设置图片
     *
     * @param totalTimeMap 切换锚点
     * @param totalTime    总倒计时
     */
    void setAdGuideVisible(int over_status, LinkedHashMap<Integer, Integer> totalTimeMap, int totalTime, List<AdGuideData.ListBean> data);

    /**
     * 是否显示
     *
     * @return
     */
    boolean isShow();

    /**
     * 获取广告页码
     *
     * @return
     */
    int getCurrentItem();

    /**
     * 设置广告页码
     *
     * @param currentItem
     */
    void setCurrentItem(int currentItem);
}
