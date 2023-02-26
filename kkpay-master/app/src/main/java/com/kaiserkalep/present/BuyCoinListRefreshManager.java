package com.kaiserkalep.present;


import com.kaiserkalep.MyApp;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

/**
 * 交易中心轮询
 *
 * @Auther: Jack
 * @Date: 2021/3/23 17:08
 * @Description:
 */
public class BuyCoinListRefreshManager {

    private final long time = 20 * 1000;
    /**
     * Config.ZERO_STRING   立即刷新
     * <p>
     * Config.ONE_STRING    轮询刷新
     */
    private SucceedCallBackListener<String> listener;

    private BuyCoinListRefreshManager() {
    }

    public static BuyCoinListRefreshManager getManager() {
        return BuyCoinListRefreshManager.BuyCoinListRefreshManagerHolder.INSTANCE;
    }

    private static class BuyCoinListRefreshManagerHolder {
        private static final BuyCoinListRefreshManager INSTANCE = new BuyCoinListRefreshManager();
    }

    public void startRefresh(boolean refreshNow, SucceedCallBackListener<String> listener) {
        stopRefresh();
        this.listener = listener;
        MyApp.postDelayed(runnable, time);
        if (refreshNow && listener != null) {
            listener.succeedCallBack(Config.ZERO_STRING);
        }
    }

    public void stopRefresh() {
        MyApp.removeCallbacks(runnable);
    }

    private Runnable runnable = this::run;

    private void run() {
        if (listener != null) {
            listener.succeedCallBack(Config.ONE_STRING);
            MyApp.postDelayed(runnable, time);
        }
    }

}
