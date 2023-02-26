package com.kaiserkalep.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.kaiserkalep.R;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.utils.AdvertTracker;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.MobclickAgentUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 21:04
 * @Description:
 */
public abstract class ZZFragment extends FragmentBase implements ActivityIntentInterface {


    ActivityPresent activityPresent = new ActivityPresent(this);
    protected FragmentManager mFragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        initEventBus();
    }

    protected void initEventBus() {
        EventBusUtil.register(this);
    }


    @Override
    public void onDestroyView() {
        closeDialog();
        EventBusUtil.unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 弹窗提示
     *
     * @param msg       文字显示
     * @param canCancle 是否可以返回键关闭
     */
    @Override
    public void showDialog(String msg, boolean canCancle, boolean isDelayedLoading, CallbackBase callbackBase) {
        activityPresent.showDialog(msg, canCancle, isDelayedLoading, callbackBase);
    }

    /**
     * 关闭弹窗
     */
    @Override
    public void closeDialog() {
        activityPresent.closeDialog();
    }

    /**
     * 弱提示
     *
     * @param msg   文字显示
     * @param imgId 图片id -1则不显示图片
     */
    @Override
    public void toast(String msg, int imgId) {
        activityPresent.toast(msg, imgId);
    }

    /**
     * 弱提示
     *
     * @param msg 文字显示
     */
    public void toast(String msg) {
        toast(msg, -1);
    }

    /**
     * 成功类提示
     *
     * @param msg
     */
    @Override
    public void toastSuccess(String msg) {
        activityPresent.toastSuccess(msg);
    }

    /**
     * 信息类提示
     *
     * @param msg
     */
    @Override
    public void toastInfo(String msg) {
        activityPresent.toastInfo(msg);
    }

    /**
     * 预警类提示
     *
     * @param msg
     */
    @Override
    public void toastWarning(String msg) {
        activityPresent.toastWarning(msg);
    }

    /**
     * 错误类提示
     *
     * @param msg
     */
    @Override
    public void toastError(String msg) {
        activityPresent.toastError(msg);
    }

    public void showDialog() {
        showDialog("", false, false, null);
    }

    public void showDialog(String msg) {
        showDialog(msg, false, false, null);
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
    public void showCommonDialog(String title, String msg, String leftStr, String rightStr, View.OnClickListener left, View.OnClickListener right) {
        activityPresent.showCommonDialog(title, msg, leftStr, rightStr, left, right);
    }

    /**
     * 隐藏commondialog
     */
    @Override
    public void closeCommonDialog() {
        activityPresent.closeCommonDialog();
    }


    @Override
    public void onBefore() {

    }

    @Override
    public void beforeOnCreate() {

    }

    @Override
    public void goToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void startClass(String activityName) {
        activityPresent.startClass(activityName, null);
    }

    @Override
    public void startClass(int activityName) {
        activityPresent.startClass(getString(activityName), null);
    }

    @Override
    public void startClass(String activityName, HashMap params) {
        activityPresent.startClass(activityName, params);
    }

    @Override
    public void startClass(int activityName, HashMap params) {
        activityPresent.startClass(getString(activityName), params);
    }

    @Override
    public void startClassWithFlag(String activityName, HashMap params, int... flags) {
        activityPresent.startClass(activityName, params, flags);
    }

    @Override
    public void startClassForResult(String activityName, HashMap params, int requestCode) {
        activityPresent.startClassForResult(activityName, params, requestCode);
    }


    @Override
    public HashMap<String, String> getUrlParam() {
        return activityPresent.getParams();
    }

    @Override
    public Bundle getBundleParams() {
        return activityPresent.getBundleParams();
    }

    @Override
    public void setResult(int resultCode, Bundle data) {
        activityPresent.setResult(resultCode, data);
    }

    @Override
    public String getThisHost() {
        return activityPresent.getThisHost();
    }

    @Override
    public String getThisHostUrl() {
        return activityPresent.getThisHostUrl();
    }

    @Override
    public String getLastHost() {
        return activityPresent.getLastHost();
    }

    @Override
    public String getLastHostUrl() {
        return activityPresent.getLastHostUrl();
    }

    @Override
    public void startClassForResult(String activityName, HashMap params, int requestCode, boolean checkLogin, Bundle bundle, int... flags) {
        activityPresent.startClassForResult(activityName, params, requestCode, checkLogin, bundle, flags);
    }

    @Override
    public void startClass(String activityName, HashMap params, boolean checkLogin, Bundle bundle, int... flags) {
        activityPresent.startClass(activityName, params, checkLogin, bundle, flags);
    }

    @Override
    public void checkLogin(String activityName, HashMap params) {
        activityPresent.checkLogin(activityName, params);
    }

    @Override
    public void checkLogin(int activityName) {
        activityPresent.checkLogin(getString(activityName), null);
    }

    @Override
    public void checkLogin(int activityName, HashMap params) {
        activityPresent.checkLogin(getString(activityName), params);
    }

    @Override
    public boolean checkLogin() {
        return activityPresent.checkLogin();
    }

    @Override
    public boolean checkLogin(View view) {
        return activityPresent.checkLogin(view);
    }


    @Override
    public boolean isLogin() {
        return activityPresent.isLogin();
    }

    public boolean isLoginToast() {
        boolean login = activityPresent.isLogin();
        if (!login) {
            goToLoginActivity();
        }
        return login;
    }

    /**
     * 获取上个页面的数据
     *
     * @param s
     * @return
     */
    public String getStringParam(String s) {
        HashMap<String, String> urlParam = getUrlParam();
        if (CommonUtils.MapNotNull(urlParam)) {
            if (urlParam.containsKey(s)) {
                return urlParam.get(s);
            }
        }
        return "";
    }

    /**
     * webview页面
     *
     * @param url
     */
    public void gotoWebView(String url) {
        gotoWebView(url, "", false);
    }

    /**
     * webview页面
     *
     * @param url
     */
    public void gotoWebView(String url, boolean needService) {
        gotoWebView(url, "", needService);
    }

    /**
     * webview页面
     *
     * @param url
     */
    public void gotoWebView(String url, String title, boolean needService) {
        gotoWebViewPost(url, title,"", needService);
    }

    public void gotoWebViewPost(String url, String title, String params, boolean needService) {
        if (!CommonUtils.StringNotNull(url)) {
            return;
        }
        startClass(R.string.WebViewActivity, IntentUtils.getHashObj(new String[]{
                StringConstants.URL, url, StringConstants.TITLE, title, StringConstants.PARAMS, params,
                StringConstants.NEED_SERVICE, String.valueOf(needService)}));
    }

    /**
     * 列表请求完成,开启列表上拉及下拉加载
     */
    protected void openRefresh(RefreshLayout refreshLayout, LoadingLayout mLoadingLayout) {
        if (mLoadingLayout != null && mLoadingLayout.notLoading()) {
            if (refreshLayout != null) {
                refreshLayout.setEnablePureScrollMode(false);//是否启用纯滚动模式
//                refreshLayout.setEnableLoadMore(!mLoadingLayout.isEmpty());
            }
        }
    }

    /**
     * 开始页面统计
     */
    public void onPageStart(String name) {
        MobclickAgentUtils.onPageStart(name);
    }

    /**
     * 结束页面统计
     */
    public void onPageEnd(String name) {
        MobclickAgentUtils.onPageEnd(name);
    }


    /**
     * 设置友盟开始页面统计
     */
    public void setUmengStartPageStatistics() {
    }

    /**
     * 设置友盟停止页面统计
     */
    public void setUmengEndPageStatistics() {
    }

    /**
     * 设置友盟开始页面统计 带下标
     */
    public void setUmengStartPageStatistics(int index) {
    }

    /**
     * 设置友盟停止页面统计 带下标
     */
    public void setUmengEndPageStatistics(int index) {
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
                activityPresent.startClass(url);
                AdvertTracker.advertTracker(this, id);
            }
        }
    }
}

