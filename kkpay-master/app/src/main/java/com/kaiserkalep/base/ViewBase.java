package com.kaiserkalep.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.utils.AdvertTracker;
import com.kaiserkalep.utils.CommonUtils;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * @Auther: Administrator
 * @Date: 2019/3/25 0025 11:48
 * @Description:
 */
public abstract class ViewBase extends FrameLayout implements ActivityIntentInterface {
    public ViewBase(Context context) {
        super(context);
        register(context);
        initView();
    }

    public ViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        register(context);
        initView();
    }

    public ViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        register(context);
        initView();
    }

    public ActivityIntentInterface getCtrl() {
        if (activityIntentInterface == null && getContext() != null) {
            Activity activity = CommonUtils.getActivityFromContext(getContext());
            if (activity != null) {
                register(activity);
            }
        }
        return activityIntentInterface;
    }

    public void register(Context context) {
        if (context instanceof ActivityIntentInterface) {
            ActivityIntentInterface a = (ActivityIntentInterface) context;
            register(a);
        }
    }

    public void register(ActivityIntentInterface activityIntentInterface) {
        this.activityIntentInterface = activityIntentInterface;
    }

    ActivityIntentInterface activityIntentInterface;

    protected View initView() {
        if (getViewId() > 0) {
            View view = LayoutInflater.from(getContext()).inflate(getViewId(), null);
            addView(view);
            ButterKnife.bind(this, view);
            afterViews(view);
            return view;
        }
        return null;

    }

    public abstract int getViewId();

    public abstract void afterViews(View v);


    public Lifecycle getLifecycle() {
        if (getActivity() != null) {
            return getActivity().getLifecycle();
        }
        return null;
    }

    @Override
    public void startClass(String activityName) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName);
        }
    }

    @Override
    public void startClass(int activityName) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName);
        }
    }

    /**
     * 获取当前activity
     *
     * @return
     */
    @Override
    public FragmentActivity getActivity() {
        if (getCtrl() != null) {
            return getCtrl().getActivity();
        }
        return null;
    }

    /**
     * 页面打开之前判断登录（用于外部跳转）
     */
    @Override
    public void beforeOnCreate() {
        if (getCtrl() != null) {
            getCtrl().beforeOnCreate();
        }
    }

    @Override
    public void onBefore() {
        if (getCtrl() != null) {
            getCtrl().onBefore();
        }
    }

    /**
     * 跳转到登录页面
     */
    @Override
    public void goToLoginActivity() {
        if (getCtrl() != null) {
            getCtrl().goToLoginActivity();
        }
    }

    @Override
    public boolean checkLogin(View view) {
        if (getCtrl() != null) {
            return getCtrl().checkLogin(view);
        }
        return false;
    }


    @Override
    public void checkLogin(int activityName) {
        if (getCtrl() != null) {
            getCtrl().checkLogin(activityName);
        }
    }

    @Override
    public void checkLogin(int activityName, HashMap params) {
        if (getCtrl() != null) {
            getCtrl().checkLogin(activityName, params);
        }
    }

    /**
     * 页面跳转
     *
     * @param activityName
     * @param params
     */
    @Override
    public void startClass(String activityName, HashMap params) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName, params);
        }
    }

    /**
     * 页面跳转
     *
     * @param activityName
     * @param params
     */
    @Override
    public void startClass(int activityName, HashMap params) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName, params);
        }
    }

    /**
     * 页面跳转 带flag
     *
     * @param activityName
     * @param params
     * @param flags
     */
    @Override
    public void startClassWithFlag(String activityName, HashMap params, int... flags) {
        getCtrl().startClassWithFlag(activityName, params, flags);
    }

    /**
     * 页面跳转回调
     *
     * @param activityName
     * @param params
     * @param requestCode
     */
    @Override
    public void startClassForResult(String activityName, HashMap params, int requestCode) {
        if (getCtrl() != null) {
            getCtrl().startClassForResult(activityName, params, requestCode);
        }
    }

    /**
     * 获取上一个页面参数
     */
    @Override
    public HashMap getUrlParam() {
        if (getCtrl() != null) {
            return getCtrl().getUrlParam();
        }
        return null;
    }

    @Override
    public void showDialog(String msg, boolean canCancle, boolean isDelayedLoading, CallbackBase callbackBase) {
        if (getCtrl() != null) {
            getCtrl().showDialog(msg, canCancle, isDelayedLoading, callbackBase);
        }
    }

    /**
     * 关闭弹窗
     */
    @Override
    public void closeDialog() {
        if (getCtrl() != null) {
            getCtrl().closeDialog();
        }
    }

    /**
     * 弱提示
     *
     * @param msg   文字显示
     * @param imgId 图片id -1则不显示图片
     */
    @Override
    public void toast(String msg, int imgId) {
        if (getCtrl() != null) {
            getCtrl().toast(msg, imgId);
        }
    }

    /**
     * 弱提示
     *
     * @param msg 文字显示
     */
    public void toast(String msg) {
        if (getCtrl() != null) {
            getCtrl().toast(msg, -1);
        }
    }

    /**
     * 成功类提示
     *
     * @param msg
     */
    @Override
    public void toastSuccess(String msg) {
        if (getCtrl() != null) {
            getCtrl().toastSuccess(msg);
        }
    }

    /**
     * 信息类提示
     *
     * @param msg
     */
    @Override
    public void toastInfo(String msg) {
        if (getCtrl() != null) {
            getCtrl().toastInfo(msg);
        }
    }

    /**
     * 预警类提示
     *
     * @param msg
     */
    @Override
    public void toastWarning(String msg) {
        if (getCtrl() != null) {
            getCtrl().toastWarning(msg);
        }
    }

    /**
     * 错误类提示
     *
     * @param msg
     */
    @Override
    public void toastError(String msg) {
        if (getCtrl() != null) {
            getCtrl().toastError(msg);
        }
    }

    /**
     * 显示commondialog
     *
     * @param title    标题
     * @param msg      显示文字
     * @param leftStr  左边文字
     * @param rightStr 右边文字
     * @param left     左按钮点击
     * @param right    右边按钮点击
     */
    @Override
    public void showCommonDialog(String title, String msg, String leftStr, String rightStr, OnClickListener left, OnClickListener right) {
        if (getCtrl() != null) {
            getCtrl().showCommonDialog(title, msg, leftStr, rightStr, left, right);
        }
    }

    /**
     * 隐藏commondialog
     */
    @Override
    public void closeCommonDialog() {
        if (getCtrl() != null) {
            getCtrl().closeCommonDialog();
        }
    }

    /**
     * 页面跳转 带启动模式的
     *
     * @param activityName
     * @param params
     * @param checkLogin   是否校验登录
     * @param bundle       json 数据
     * @param flags        可多传
     */
    @Override
    public void startClass(String activityName, HashMap params, boolean checkLogin, Bundle bundle, int... flags) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName, params, checkLogin, bundle, flags);
        }
    }

    /**
     * 页面跳转回调
     *
     * @param activityName
     * @param params
     * @param requestCode
     * @param checkLogin   是否校验登录
     * @param bundle       复杂数据
     * @param flags        启动模式
     */
    @Override
    public void startClassForResult(String activityName, HashMap params, int requestCode, boolean checkLogin, Bundle bundle, int... flags) {
        if (getCtrl() != null) {
            getCtrl().startClassForResult(activityName, params, requestCode, checkLogin, bundle, flags);
        }
    }

    /**
     * 获取当前页面host
     */
    @Override
    public String getThisHost() {
        if (getCtrl() != null) {
            return getCtrl().getThisHost();
        }
        return "";
    }

    /**
     * 获取当前页面host以及参数
     */
    @Override
    public String getThisHostUrl() {
        if (getCtrl() != null) {
            return getCtrl().getThisHostUrl();
        }
        return "";
    }

    /**
     * 获取上一个页面host
     */
    @Override
    public String getLastHost() {
        if (getCtrl() != null) {
            return getCtrl().getLastHost();
        }
        return "";
    }

    /**
     * 获取上一个页面host以及参数
     */
    @Override
    public String getLastHostUrl() {
        if (getCtrl() != null) {
            return getCtrl().getLastHostUrl();
        }
        return "";
    }

    /**
     * 判断是否登录，未登录跳转登录页面
     *
     * @return 是否登录
     */
    @Override
    public boolean checkLogin() {
        if (getCtrl() != null) {
            return getCtrl().checkLogin();
        }
        return false;
    }

    @Override
    public boolean isLogin() {
        if (getCtrl() != null) {
            return getCtrl().isLogin();
        }
        return false;
    }

    public boolean isLoginToast() {
        boolean login = false;
        if (getCtrl() != null) {
            login = getCtrl().isLogin();
            if (!login) {
                // getCtrl().toast("请登录", -1);
                goToLoginActivity();
            }
        }
        return login;
    }

    /**
     * 判断是否登录，未登录跳转登录页面 登录成功后，跳到制定页面
     *
     * @param activityName 登录成功后跳转页面
     * @param params       登录成功后页面传参
     * @return 是否登录
     */
    @Override
    public void checkLogin(String activityName, HashMap params) {
        if (getCtrl() != null) {
            getCtrl().checkLogin(activityName, params);
        }
    }

    /**
     * 获取上一个activity传给这个activity的参数键值对
     *
     * @return
     */
    @Override
    public Bundle getBundleParams() {
        if (getCtrl() != null) {
            return getCtrl().getBundleParams();
        }
        return null;
    }

    /**
     * 重新setResult，规范传参
     *
     * @param resultCode
     * @param data
     */
    @Override
    public void setResult(int resultCode, Bundle data) {
        if (getCtrl() != null) {
            getCtrl().setResult(resultCode, data);
        }
    }

    private long lastClickTime = 0;

    /**
     * 广告埋点
     *
     * @param id
     */
    @Override
    public void advertTracker(String url, String id) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            if (CommonUtils.StringNotNull(url, id)) {
                if (getCtrl() != null) {
                    getCtrl().startClass(url);
                }
                AdvertTracker.advertTracker(this, id);
            }
        }
    }
}
