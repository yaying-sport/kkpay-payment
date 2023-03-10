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
     * ????????????activity
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
     * ??????????????????????????????????????????????????????
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
     * ?????????????????????
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
     * ????????????
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
     * ????????????
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
     * ???????????? ???flag
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
     * ??????????????????
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
     * ???????????????????????????
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
     * ????????????
     */
    @Override
    public void closeDialog() {
        if (getCtrl() != null) {
            getCtrl().closeDialog();
        }
    }

    /**
     * ?????????
     *
     * @param msg   ????????????
     * @param imgId ??????id -1??????????????????
     */
    @Override
    public void toast(String msg, int imgId) {
        if (getCtrl() != null) {
            getCtrl().toast(msg, imgId);
        }
    }

    /**
     * ?????????
     *
     * @param msg ????????????
     */
    public void toast(String msg) {
        if (getCtrl() != null) {
            getCtrl().toast(msg, -1);
        }
    }

    /**
     * ???????????????
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
     * ???????????????
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
     * ???????????????
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
     * ???????????????
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
     * ??????commondialog
     *
     * @param title    ??????
     * @param msg      ????????????
     * @param leftStr  ????????????
     * @param rightStr ????????????
     * @param left     ???????????????
     * @param right    ??????????????????
     */
    @Override
    public void showCommonDialog(String title, String msg, String leftStr, String rightStr, OnClickListener left, OnClickListener right) {
        if (getCtrl() != null) {
            getCtrl().showCommonDialog(title, msg, leftStr, rightStr, left, right);
        }
    }

    /**
     * ??????commondialog
     */
    @Override
    public void closeCommonDialog() {
        if (getCtrl() != null) {
            getCtrl().closeCommonDialog();
        }
    }

    /**
     * ???????????? ??????????????????
     *
     * @param activityName
     * @param params
     * @param checkLogin   ??????????????????
     * @param bundle       json ??????
     * @param flags        ?????????
     */
    @Override
    public void startClass(String activityName, HashMap params, boolean checkLogin, Bundle bundle, int... flags) {
        if (getCtrl() != null) {
            getCtrl().startClass(activityName, params, checkLogin, bundle, flags);
        }
    }

    /**
     * ??????????????????
     *
     * @param activityName
     * @param params
     * @param requestCode
     * @param checkLogin   ??????????????????
     * @param bundle       ????????????
     * @param flags        ????????????
     */
    @Override
    public void startClassForResult(String activityName, HashMap params, int requestCode, boolean checkLogin, Bundle bundle, int... flags) {
        if (getCtrl() != null) {
            getCtrl().startClassForResult(activityName, params, requestCode, checkLogin, bundle, flags);
        }
    }

    /**
     * ??????????????????host
     */
    @Override
    public String getThisHost() {
        if (getCtrl() != null) {
            return getCtrl().getThisHost();
        }
        return "";
    }

    /**
     * ??????????????????host????????????
     */
    @Override
    public String getThisHostUrl() {
        if (getCtrl() != null) {
            return getCtrl().getThisHostUrl();
        }
        return "";
    }

    /**
     * ?????????????????????host
     */
    @Override
    public String getLastHost() {
        if (getCtrl() != null) {
            return getCtrl().getLastHost();
        }
        return "";
    }

    /**
     * ?????????????????????host????????????
     */
    @Override
    public String getLastHostUrl() {
        if (getCtrl() != null) {
            return getCtrl().getLastHostUrl();
        }
        return "";
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @return ????????????
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
                // getCtrl().toast("?????????", -1);
                goToLoginActivity();
            }
        }
        return login;
    }

    /**
     * ???????????????????????????????????????????????? ????????????????????????????????????
     *
     * @param activityName ???????????????????????????
     * @param params       ???????????????????????????
     * @return ????????????
     */
    @Override
    public void checkLogin(String activityName, HashMap params) {
        if (getCtrl() != null) {
            getCtrl().checkLogin(activityName, params);
        }
    }

    /**
     * ???????????????activity????????????activity??????????????????
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
     * ??????setResult???????????????
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
     * ????????????
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
