package com.kaiserkalep.base;

import com.kaiserkalep.R;
import com.kaiserkalep.bean.ResultData;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.TokenTimeOutEvent;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DomainErrorLogUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.NetWorkUtils;
import com.kaiserkalep.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 19:50
 * @Description:
 */
public abstract class CallbackBase<T> extends Callback<ResultData<T>> {


    String cancelTag = "";
    boolean isCancel;
    private static final String TAG = "CallbackBase";
    IBaseView ui;
    private long lastLoadTime;
    long timeOut = BaseNetUtil.TIME_OUT_MILLIS;

    public CallbackBase(IBaseView ui) {
        this.ui = ui;
        setTag();
        this.cancelTag = UUID.randomUUID().toString();
    }

    /**
     * 是否需要请求中弹框
     */
    boolean isNeedDialog = true;


    /**
     * 加载框显示文案
     */
    String dialogMsg = "";
    /**
     * 是否需要报错提示toast
     */
    boolean isNeedToast = true;
    /**
     * 是否延时loading
     */
    boolean isDelayedLoading = false;

    /**
     * 加载延时时间
     */
    int delayMillis = 0;

    /**
     * 登录失效,是否跳转登录
     */
    boolean isGoToLogin = true;

    /**
     * 是否校验登录失效
     */
    boolean isTokenTimeOut = true;


    /**
     * 是否缓存接口
     */
    boolean isCache;

    /**
     * Thread Pool Thread
     *
     * @param response
     * @param id
     */
    @Override
    public abstract ResultData<T> parseNetworkResponse(Response response, int id) throws Exception;

    public void onError(String msg, String code) {
        if (!isUIUseable()) {
            return;
        }
        if (isNeedToast()) {
            ui.toastError(msg);
        }
        LogUtils.e(TAG, msg + "____" + code);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (ui != null && NetWorkUtils.isNetworkConnected(ui.getContext())) {
            if (e != null && (e.toString().contains("closed") || e.toString().contains("Canceled"))) {
//                onError("连接服务失败", "404");
                if (isCancel) {//主动取消
                    return;
                }
                onConnectFail(false);
            } else if (call != null && call.isCanceled()) {
                LogUtils.e("用户取消请求");
            } else if (e != null && (e.toString().contains("502"))) {
//                onError("服务未启动", "502");
                onConnectFail(false);
            } else if (e != null && (e.toString().contains("404"))) {
//                onError("访问服务器地址出错", "404");
                onConnectFail(false);
            } else if (e != null && (e.toString().toUpperCase().contains("FAILED TO CONNECT TO"))) {
//                onError("连接服务失败", "404");
                onConnectFail(false);
            } else if ((e != null && e.getCause() != null && e.getCause() instanceof TimeoutException)
                    || (e != null && (e.toString().toUpperCase().contains("TIMEOUT")))) {
//                onError("连接超时", "502");
                onConnectFail(false);
            } else {
                onError(getString(R.string.toast_common_system_net_error), StringConstants.REQUEST_OTHER_ERROR);
            }
        } else {
            onConnectFail(true);
        }
    }

    /**
     * 没有网络
     */
    public void onConnectFail(boolean notNetwork) {
        if (DomainErrorLogUtils.instance().getAllUrlError()) {
            onError(getString(R.string.toast_common_not_connect_server), StringConstants.REQUEST_OTHER_ERROR);
        } else {
            if (notNetwork) {
                onError(getString(R.string.toast_common_system_net_error), StringConstants.REQUEST_OTHER_ERROR);
            } else {
                onError(getString(R.string.toast_common_system_net_stablize), StringConstants.REQUEST_OTHER_ERROR);
            }
        }
    }

    @Override
    public void onResponse(ResultData<T> response, int id) {
        DomainErrorLogUtils.instance().setAllUrlError(false);
        try {
            closeDialog();
            if (response != null) {
                if (response.isOk()) {
                    onSuccess(response.getData());
                } else if (response.isVerifyNewPass()) {
                    onSuccessToVerifyNew(response.getData(), true);
                } else if (response.isLoginOut()) {
                    EventBusUtil.post(new TokenTimeOutEvent(true, true, response.getMsg()));
                } else if (CommonUtils.StringNotNull(response.getMsg())) {
                    LogUtils.d(response.getMsg());
                    onError(response.getMsg(), response.getCode());
                } else {
                    onError(null, null, -1);
                }
            } else {
                onError(null, null, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(e);
            onConnectFail(false);
        }
    }

    /**
     * 延时
     */
    void animationTime() {
        if (delayMillis != 0) {
            if (System.currentTimeMillis() - lastLoadTime < delayMillis) {
                try {
                    LogUtils.d("延时开始" + System.currentTimeMillis());
                    Thread.sleep(delayMillis);//保证300毫秒的动画
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtils.d("延时结束" + System.currentTimeMillis());
        }
    }


    /**
     * 跳转更新维护
     *
     * @param errorMsg
     */
    private void gotoUpdateMaintain(String errorMsg) {
//        if (UpdateMaintainActivity.UPDATE_MAINTAIN_TAG) {
//            return;
//        }
//        if (!isUIUseable()) {
//            return;
//        }
//        ActivityBase currentActivity = MyDialogManager.getManager().getCurrentActivity();
//        if (currentActivity != null && currentActivity instanceof UpdateMaintainActivity) {
//            return;
//        }
//        if (ui instanceof ActivityIntentInterface) {
//            UpdateMaintainActivity.UPDATE_MAINTAIN_TAG = true;
//            ActivityPresent.lastClickTime = 0;
//            ((ActivityIntentInterface) ui).startClass(R.string.UpdateMaintainActivity, IntentUtils
//                    .getHashObj(new String[]{StringConstants.DATA, errorMsg}));
//            MyDialogManager.getManager().initAllDialog();
//            MyActivityManager.getActivityManager().popAllActivityExceptOne(UpdateMaintainActivity.class);
//        }
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (!isUIUseable()) {
            return;
        }
        ui.onBefore();
        if (delayMillis != 0) {
            lastLoadTime = System.currentTimeMillis();
            LogUtils.d("开始时间" + lastLoadTime);
        }
        if (isNeedDialog() && NetWorkUtils.isNetworkConnected(ui.getContext())) {//无网络时,不显示loading,
            ui.showDialog(getDialogMsg(), false, isDelayedLoading(), this);
        }
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        closeDialog();
    }


    private void closeDialog() {
        if (!isUIUseable()) {
            return;
        }
        if (null != ui && isNeedDialog()) {
            ui.closeDialog();
        }
    }

    public boolean isUIUseable() {
        return ui != null && ui.getContext() != null;
    }


    public abstract void onSuccess(T response);

    /**
     * 登录换设备，效验手机号
     */
    public void onSuccessToVerifyNew(T response, boolean isVerifynew) {

    }

    String getString(int id) {
        if (isUIUseable() && ui.getContext() != null) {
            return ui.getContext().getString(id);
        }
        return "";
    }

    public boolean isNeedDialog() {
        return isNeedDialog;
    }

    public CallbackBase<T> setNeedDialog(boolean needDialog) {
        isNeedDialog = needDialog;
        return this;
    }

    public String getDialogMsg() {
        return dialogMsg;
    }

    public CallbackBase<T> setDialogMsg(String dialogMsg) {
        this.dialogMsg = dialogMsg;
        return this;
    }

    public boolean isNeedToast() {
        return isNeedToast;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public CallbackBase<T> setTimeOut(long timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public CallbackBase<T> setNeedToast(boolean needToast) {
        isNeedToast = needToast;
        return this;
    }

    public boolean isDelayedLoading() {
        return isDelayedLoading;
    }

    public CallbackBase<T> setDelayedLoading(boolean isDelayedLoading) {
        this.isDelayedLoading = isDelayedLoading;
        return this;
    }

    /**
     * 取消tag
     *
     * @return
     */
    public String getCancelTag() {
        return cancelTag;
    }

    /**
     * 是否允许空入参
     *
     * @return
     */
    public boolean isAllowNullParams() {
        return false;
    }

    /**
     * 取消请求(事件上只是取消了回调,已经发出的请求无法取消)
     */
    public void cancel() {
        isCancel = true;
        LogUtils.e("cancel_network_" + getCancelTag());
        OkHttpUtils.getInstance().cancelTag(getCancelTag());
    }

    public boolean isGoToLogin() {
        return isGoToLogin;
    }

    public CallbackBase<T> setGoToLogin(boolean goToLogin) {
        isGoToLogin = goToLogin;
        return this;
    }

    private void setTag() {
//        if (ui != null) {
//            ui instanceof ZZActivity
//        }

    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public CallbackBase<T> setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    public CallbackBase<T> setCache(boolean cache) {
        isCache = cache;
        return this;
    }

    public CallbackBase<T> setTokenTimeOut(boolean tokenTimeOut) {
        isTokenTimeOut = tokenTimeOut;
        return this;
    }

    public IBaseView getUi() {
        return ui;
    }
}