package com.kaiserkalep.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.R;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.mydialog.CommonDialog;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Activity 跳转工具类
 *
 * @Auther: Administrator
 * @Date: 2019/4/4 0004 20:51
 * @Description:
 */
public class ActivityPresent extends NetPresenterBase {
    private static int MIN_CLICK_DELAY_TIME = 250;//1秒防快速点击跳转
    public static long lastClickTime = 0;
    public static View needClickView;//未登录点击操作view,在登录后触发点击

    public ActivityIntentInterface getCtrl() {
        return (ActivityIntentInterface) getiBaseView();
    }

    protected ActivityIntentInterface anInterface;
    static final String TAG = "ActivityIntentUtils";

    public ActivityPresent(ActivityIntentInterface anInterface) {
        super(anInterface);
        this.anInterface = anInterface;
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean isUseable() {
        return anInterface != null;
    }

    public FragmentActivity getContext() {
        if (!isUseable())
            return null;
        return anInterface.getActivity();
    }

    public Intent getIntent() {
        if (!isUseable() || getContext() == null)
            return null;
        return anInterface.getActivity().getIntent();
    }


    /**
     * 跳转到登录页面
     */
    public void goToLoginActivity() {
        startClass(getString(R.string.LoginActivity), null, Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    /**
     * 页面跳转
     *
     * @param activityName
     */
    public void startClass(String activityName) {
        startClass(activityName, null, null);
    }

    /**
     * 页面跳转
     *
     * @param activityName
     * @param params
     */
    public void startClass(String activityName, HashMap params) {
        startClass(activityName, params, null);
    }

    /**
     * 页面跳转 带启动模式的
     *
     * @param activityName
     * @param params
     * @param flags        可多传
     */
    public void startClass(String activityName, HashMap params, int... flags) {
        startClass(activityName, params, true, flags);

    }

    /**
     * 页面跳转 带启动模式的
     *
     * @param activityName
     * @param params
     * @param checkLogin   是否校验登录
     * @param flags        可多传
     */
    public void startClass(String activityName, HashMap params, boolean checkLogin, int... flags) {
        startClass(activityName, params, checkLogin, null, flags);

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
    public void startClass(String activityName, HashMap params, boolean checkLogin, Bundle bundle, int... flags) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        FragmentActivity context = getContext();
        //Math.abs:修改系统时间,上次点击超过当前
        if (null != context && Math.abs(currentTime - lastClickTime) > MIN_CLICK_DELAY_TIME) {//防快速点击
            lastClickTime = currentTime;
            Intent in = initIntent(activityName, params, checkLogin, bundle, flags);
            if (in != null) {
                if (context.getPackageManager().resolveActivity(in, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    try {
                        context.startActivity(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e(TAG, "传值异常，无法跳转activityName=" + activityName);
                    }
                } else {
                    LogUtils.e(TAG, "无此页面，无法跳转activityName=" + activityName);
                }
            } else {
                LogUtils.e(TAG, "传值异常，无法跳转activityName=" + activityName);
            }
        }
    }

    /**
     * 生成intent
     *
     * @param activityName
     * @param params
     * @param checkLogin   是否校验登录
     * @param bundle       复杂数据
     * @param flags        启动模式
     * @return
     */
    private Intent initIntent(String activityName, HashMap params, boolean checkLogin, Bundle bundle, int... flags) {
        FragmentActivity context = getContext();
        if (!CommonUtils.StringNotNull(activityName) && null != context) {
            return null;
        }
        Intent in = IntentUtils.getIntent(context, activityName, params);
        if (in != null) {
            in = intentFilter(in, checkLogin, activityName);
            if (in == null)
                return null;
            String thisHost = getThisHost();
            String thisHostUrl = getThisHostUrl();
            in.putExtra(getString(R.string.thisHost), TextUtils.isEmpty(thisHost) ? getString(R.string.MainActivity) : thisHost);
            in.putExtra(getString(R.string.thisUrl), TextUtils.isEmpty(thisHostUrl) ? getString(R.string.MainActivity) : thisHostUrl);
            if (bundle != null) {
                in.putExtra(getString(R.string.jsonBundle), bundle);
            }
            if (in.getDataString() != null) {
                in.putExtra(getString(R.string.nextHost), in.getData().getHost());
                in.putExtra(getString(R.string.nextUrl), in.getDataString());
            }
            if (flags != null && flags.length > 0) {
                if (flags.length > 1) {
                    for (int flag : flags) {
                        in.addFlags(flag);
                    }
                } else {
                    in.setFlags(flags[0]);
                }
            }
        } else {
            LogUtils.e(TAG, "传值异常，无法跳转activityName=" + activityName);
        }
        return in;
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
    public void startClassForResult(String activityName, HashMap params, int requestCode, boolean checkLogin, Bundle bundle, int... flags) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        FragmentActivity context = getContext();
        //Math.abs:修改系统时间,上次点击超过当前
        if (null != context && Math.abs(currentTime - lastClickTime) > MIN_CLICK_DELAY_TIME) {//防快速点击
            lastClickTime = currentTime;
            Intent in = initIntent(activityName, params, checkLogin, bundle, flags);
            if (in != null) {
                try {
                    context.startActivityForResult(in, requestCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "传值异常，无法跳转activityName=" + activityName);
                }
            } else {
                LogUtils.e(TAG, "传值异常，无法跳转activityName=" + activityName);
            }
        }
    }

    /**
     * 页面跳转回调
     *
     * @param activityName
     * @param params
     * @param requestCode
     */
    public void startClassForResult(String activityName, HashMap params, int requestCode) {
        startClassForResult(activityName, params, requestCode, true, null, null);
    }

    /**
     * 获取上一个页面参数
     */
    public HashMap getParams() {
        return IntentUtils.getUrlParam(getContext());
    }

    /**
     * 获取上一个页面参数
     */
    public Bundle getBundleParams() {
        return IntentUtils.getBundleParams(getContext());
    }

    /**
     * 获取startClassForResult中回传的bundle数据
     */
    public Bundle getResultBundleParams(Intent in) {
        return IntentUtils.getBundleParams(getContext(), in);
    }

    /**
     * 重新setResult，规范传参
     *
     * @param resultCode
     * @param data
     */
    public void setResult(int resultCode, Bundle data) {
        FragmentActivity context = getContext();
        if (null != context) {
            Intent in = IntentUtils.getResultBundleIntent(context, data);
            context.setResult(resultCode, in);
        }

    }

    /**
     * 获取当前页面host
     */
    public String getThisHost() {
        String thisHost = "";
        if (getIntent() != null) {
            if (getIntent().getData() != null) {
                thisHost = getIntent().getData().getHost();
            } else {
                thisHost = getThisHostUrl();
            }
        }
        return thisHost;
    }

    /**
     * 获取当前页面host以及参数
     */
    public String getThisHostUrl() {
        String thisHost = "";
        if (getIntent() != null && getIntent().getDataString() != null) {
            try {
                thisHost = getIntent().getDataString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return thisHost;
    }

    /**
     * 获取上一个页面host
     */
    public String getLastHost() {

        String thisHost = "";
        if (getIntent() != null) {
            thisHost = getIntent().getStringExtra(getString(R.string.thisHost));
        }
        return thisHost;
    }

    /**
     * 获取上一个页面host以及参数
     */
    public String getLastHostUrl() {

        String thisHost = "";
        if (getIntent() != null) {
            thisHost = getIntent().getStringExtra(getString(R.string.thisUrl));
        }
        return thisHost;
    }

    /**
     * 对 intent进行筛选或者一切定制化操作
     * 加之前请跟 master管理者沟通确认，尽量少加
     *
     * @param in
     * @return
     */
    public Intent intentFilter(Intent in, boolean checkLogin, String activityName) {
        FragmentActivity context = getContext();
        if (in != null && null != context) {
            Bundle bundle = IntentUtils.getActivityBundle(context, in);
            if (bundle != null) {
                if (checkLogin && !SPUtil.isLogin() && isNeedLogin(bundle))//需要登录，并且没有登录 走登录流程
                {
                    return getLoginIntent(activityName);
                }
            }
            Uri uri = in.getData();
            in.setData(uri);
        }
        return in;
    }

    /**
     * 判断是否需要登录
     *
     * @return
     */
    public boolean isNeedLogin(Bundle bundle) {
        return bundle != null && bundle.getBoolean(getString(R.string.loginTag));
    }

    /**
     * 获取登录意图
     *
     * @return
     */
    public Intent getLoginIntent(String activityName) {
        HashMap<String, String> params = new HashMap();
        if (CommonUtils.StringNotNull(activityName)) {
            params.put(StringConstants.CALLBACK, activityName);
        }
        return IntentUtils.getIntent(getContext(), getString(R.string.LoginActivity), params);
    }

    /**
     * 判断是否登录，未登录跳转登录页面
     *
     * @return 是否登录
     */
    public boolean checkLogin() {
        if (!SPUtil.isLogin()) {
            goToLoginActivity();
            return false;
        }
        return true;
    }

    /**
     * 判断是否登录
     *
     * @return 是否登录
     */
    public boolean isLogin() {
        return SPUtil.isLogin();
    }

    /**
     * 判断是否登录，已登录直接跳转,未登录跳转登录页面 登录成功后，跳到指定页面
     *
     * @param activityName 登录成功后跳转页面
     * @param params       登录成功后页面传参
     */
    public void checkLogin(String activityName, HashMap params) {
        checkLogin(activityName, params, null);
    }

    /**
     * 判断是否登录，已登录直接跳转,未登录跳转登录页面 登录成功后，跳到指定页面
     *
     * @param activityName 登录成功后跳转页面
     * @param params       登录成功后页面传参params
     * @param params       登录成功后页面传参bundle
     */
    public void checkLogin(String activityName, HashMap params, Bundle bundle) {
        if (!SPUtil.isLogin()) {
            if (params == null) {
                params = new HashMap();
            }
            if (CommonUtils.StringNotNull(activityName)) {
                params.put(StringConstants.CALLBACK, activityName);
            }
            startClass(getString(R.string.LoginActivity), params, false, bundle);
        } else {
            startClass(activityName, params, false, bundle);
        }
    }

    /**
     * 判断是否登录，已登录直接操作(如预约等接口操作等),未登录跳转登录页面 登录成功后，点击对应view
     *
     * @param view 登录成功后触发view点击
     * @return
     */
    public boolean checkLogin(View view) {
        if (!SPUtil.isLogin()) {
            needClickView = view;
            startClass(getString(R.string.LoginActivity), null);
            return false;
        }
        needClickView = null;
        return true;
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
    public void showCommonDialog(String title, String msg, String leftStr, String rightStr, View.OnClickListener left, View.OnClickListener right) {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                new CommDialogData(title, msg, leftStr, rightStr, left, right));
    }

    /**
     * 隐藏commondialog
     */
    public void closeCommonDialog() {
        DialogCommBase currentDialog = getCommonDialog();
        if (currentDialog != null) {
            currentDialog.dismiss();
        }
    }


    /**
     * 获取通用弹框
     *
     * @return
     */
    public CommonDialog getCommonDialog() {
        DialogCommBase currentDialog = MyDialogManager.getManager().currentDialog;
        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof CommonDialog) {
            return ((CommonDialog) currentDialog);
        }
        return null;
    }

//    /**
//     * 获取通用输入弹框
//     *
//     * @return
//     */
//    public CommonInputDialog getCommonInputDialog() {
//        DialogCommBase currentDialog = MyDialogManager.getManager().currentDialog;
//        if (currentDialog != null && currentDialog.isShowing() && currentDialog instanceof CommonInputDialog) {
//            return ((CommonInputDialog) currentDialog);
//        }
//        return null;
//    }

    /**
     * 获取上个页面的数据
     *
     * @param s
     * @return
     */
    public String getStringParam(String s) {
        HashMap<String, String> urlParam = getParams();
        if (CommonUtils.MapNotNull(urlParam)) {
            if (urlParam.containsKey(s)) {
                return urlParam.get(s);
            }
        }
        return "";
    }

}

