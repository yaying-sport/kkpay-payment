package com.kaiserkalep.eventbus;

/**
 *  登录失效(退出登录)
 *
 * @Auther: Administrator
 * @Date: 2019/3/23 0023 18:17
 * @Description:
 */
public class TokenTimeOutEvent {

    /**
     * 登录失效,是否跳转登录
     */
    private boolean isGoToLogin = false;
    /**
     * 是否显示toast
     */
    private boolean isShowToast = true;

    public String errorMsg;


    public TokenTimeOutEvent(boolean isGoToLogin, String errorMsg) {
        this.isGoToLogin = isGoToLogin;
        this.errorMsg = errorMsg;
    }

    public TokenTimeOutEvent(boolean isGoToLogin, boolean isShowToast, String errorMsg) {
        this.isGoToLogin = isGoToLogin;
        this.isShowToast = isShowToast;
        this.errorMsg = errorMsg;
    }

    public boolean isShowToast() {
        return isShowToast;
    }

    public void setShowToast(boolean showToast) {
        isShowToast = showToast;
    }

    public boolean isGoToLogin() {
        return isGoToLogin;
    }

    public void setGoToLogin(boolean goToLogin) {
        isGoToLogin = goToLogin;
    }

    private TokenTimeOutEvent() {
    }
}
