package com.kaiserkalep.constants;

/**
 * number常量
 *
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:18
 * @Description:
 */
public interface NumberConstants {


    /**
     * Activity StatusViewID 动态改变status的ViewId
     */
    int ACTIVITY_STATUS_VIEW_ID = 0x12345678;


    /**
     * 全局列表预加载下一页触发位置(倒数第n条,为0时关闭该功能,默认4/5条)
     */
    int PRELOAD_LIST_NUMBER = 0;


    /**
     * 搜索记录最大条数
     */
    int SEARCH_HISTORY_SIZE = 6;


    /**
     * 昵称
     */
    int NIKE_NAME_LENGTH = 10;


    /**
     * 首页轮播图时间 毫秒
     */
    int AUTO_TURNING_TIME = 5000;


    /**
     * 开屏广告时长
     */
    int START_DELAY = 6;

    /**
     * 支付方式做多条数
     */
    int PAYMENT_MAX_NUM = 6;
}
