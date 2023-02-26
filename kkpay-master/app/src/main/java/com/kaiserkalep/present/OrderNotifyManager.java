package com.kaiserkalep.present;


import com.kaiserkalep.MyApp;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 全局订单状态轮询
 *
 * @Auther: Jack
 * @Date: 2021/3/23 17:08
 * @Description:
 */
public class OrderNotifyManager {

    public static final long time = 18 * 1000;

    private SucceedCallBackListener<Boolean> listener;

    private OrderNotifyManager() {
    }

    public static OrderNotifyManager getManager() {
        return OrderNotifyManager.BuyCoinListRefreshManagerHolder.INSTANCE;
    }

    private static class BuyCoinListRefreshManagerHolder {
        private static final OrderNotifyManager INSTANCE = new OrderNotifyManager();
    }

    public void startRefresh(boolean refreshNow, SucceedCallBackListener<Boolean> listener) {
        stopRefresh();
        this.listener = listener;
        MyApp.postDelayed(runnable, time);
        if (refreshNow && listener != null) {
            listener.succeedCallBack(true);
        }
    }

    public void stopRefresh() {
        MyApp.removeCallbacks(runnable);
    }

    private Runnable runnable = this::run;

    private void run() {
        if (listener != null) {
            listener.succeedCallBack(true);
            MyApp.postDelayed(runnable, time);
        }
    }


}
