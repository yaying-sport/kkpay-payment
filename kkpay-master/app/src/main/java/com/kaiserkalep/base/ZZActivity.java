package com.kaiserkalep.base;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.service.KKpayBackgroundService;
import com.kaiserkalep.ui.activity.BuyCoinActivity;
import com.kaiserkalep.ui.activity.DepositShActivity;
import com.kaiserkalep.ui.activity.OrderDetailsActivity;
import com.kaiserkalep.ui.activity.SellCoinDetailsActivity;
import com.kaiserkalep.ui.activity.ServiceActivity;
import com.kaiserkalep.ui.activity.SettingActivity;
import com.kaiserkalep.ui.activity.WalletRecordDetailsActivity;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.umeng.analytics.MobclickAgent;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.AppFrontBackEvent;
import com.kaiserkalep.eventbus.LoginSuccessEvent;
import com.kaiserkalep.eventbus.TokenTimeOutEvent;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.StartActivity;
import com.kaiserkalep.utils.AdvertTracker;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.KeyBoardUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDataManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.NumberUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.StatusBarUtil;
import com.kaiserkalep.utils.update.utils.Md5Util;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.UpdateDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Activity base
 *
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 19:47
 * @Description:
 */
public abstract class ZZActivity extends ActivityBase implements ActivityIntentInterface {
    public final String TAG = getClass().getSimpleName();
    ActivityPresent activityPresent = new ActivityPresent(this);
    public CommTitle commTitle;
    /**
     * ???????????????????????????
     */
    protected boolean isStatusBar = true;

    /**
     * ??????????????????DecorView??????
     */
    protected boolean isDecorViewBg = true;

    /**
     * ??????????????? true,???????????????  false,???????????????
     */
    public boolean isFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEventBus();
        beforeOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBar();
        if (isDecorViewBg) {
            setDecorViewBg(true);
        }
        KKpayBackgroundService.stopService(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean isAppShow = CommonUtils.isAppShow(MyApp.getContext());
        //??????????????????
        if (!isAppShow) {
            //?????????????????????????????????
            if (MyApp.getApplication().pendingOrderMap.size() > 0
                    || MyApp.getApplication().orderMap.size() > 0) {
                KKpayBackgroundService.startService(getContext());
            }
        }
    }

    @Override
    protected void onDestroy() {
        closeDialog();
        EventBusUtil.unregister(this);
        super.onDestroy();
    }


    public void setStatusBar() {
        if (isStatusBar) {
            if (this instanceof MainActivity ||
                    this instanceof ServiceActivity ||
                    this instanceof WalletRecordDetailsActivity ||
                    this instanceof OrderDetailsActivity ||
                    this instanceof SellCoinDetailsActivity ||
                    this instanceof DepositShActivity ||
                    this instanceof BuyCoinActivity) {
                noStatus();
            } else if (this instanceof StartActivity) {
                startActivitynoStatus();
            } else {
                setWhiteStatus();
            }
        }
    }

    @Override
    public void finish() {
        closeDialog();
        super.finish();
    }

    @Override
    public void superViews() {
        super.superViews();
        try {
            commTitle = findViewById(R.id.comm_title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LoginSuccessEvent event) {
        if (event != null && this instanceof MainActivity) {
            SPUtil.setLoginSuccess(event.userData);
            ClientInfo.updata(MyApp.getContext());
        }
        loginSuccessEvent(event);
    }

    public void loginSuccessEvent(LoginSuccessEvent event) {

    }


    /**
     * ?????? ??????token ??????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenLoss(TokenTimeOutEvent event) {
        try {
            if (this instanceof MainActivity || this instanceof StartActivity || this instanceof SettingActivity) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime > 1000) {
                    lastTime = currentTime;
                    LogUtils.e("loginOut", String.valueOf(event.errorMsg) + "name=" + this.getComponentName().getClassName() + "time=" + currentTime);
                    tokenTimeOut(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void tokenTimeOut(TokenTimeOutEvent event) {
        if (event == null)
            return;
        SPUtil.setLoginOut();
        ClientInfo.initHeadParams();
        MobclickAgent.onProfileSignOff();
        if (event.isGoToLogin()) {
            if (!MyActivityManager.getActivityManager().isContains(LoginActivity.class)) {
                ActivityPresent.lastClickTime = 0;
                goToLoginActivity();
                if (event.isShowToast()) {
                    toast(CommonUtils.StringNotNull(event.errorMsg) ? event.errorMsg : MyApp.getLanguageString(getContext(), R.string.token_out));
                }
            }
            MyActivityManager.getActivityManager().popAllActivityExceptMore(LoginActivity.class);
        }
    }


    protected int getOtherHeight() {
        return 0;
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    public void beforeOnCreate() {

    }

    @Override
    public void goToLoginActivity() {
        startClassWithFlag(getString(R.string.LoginActivity), null, Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    public void startClass(String activityName) {
        activityPresent.startClass(activityName);
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


    public int getIntParam(String s, int def) {
        String stringParam = getStringParam(s);
        return CommonUtils.StringNotNull(stringParam) ? NumberUtils.stringToInt(stringParam) : def;
    }

    public long getLongParam(String s, int def) {
        String stringParam = getStringParam(s);
        return CommonUtils.StringNotNull(stringParam) ? NumberUtils.stringToLong(stringParam) : def;
    }

    /**
     * ???????????????????????????
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

    protected void initEventBus() {
        EventBusUtil.register(this);
    }

    /**
     * ????????????
     *
     * @param msg       ????????????
     * @param canCancle ???????????????????????????
     */
    @Override
    public void showDialog(String msg, boolean canCancle, boolean isDelayedLoading, CallbackBase callbackBase) {
        activityPresent.showDialog(msg, canCancle, isDelayedLoading, callbackBase);
    }

    /**
     * ????????????
     */
    @Override
    public void closeDialog() {
        activityPresent.closeDialog();
    }

    /**
     * ?????????
     *
     * @param msg   ????????????
     * @param imgId ??????id -1??????????????????
     */
    @Override
    public void toast(String msg, int imgId) {
        activityPresent.toast(msg, imgId);
    }

    /**
     * ?????????
     *
     * @param msg ????????????
     */
    public void toast(String msg) {
        toast(msg, -1);
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void toastSuccess(String msg) {
        activityPresent.toastSuccess(msg);
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void toastInfo(String msg) {
        activityPresent.toastInfo(msg);
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void toastWarning(String msg) {
        activityPresent.toastWarning(msg);
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void toastError(String msg) {
        activityPresent.toastError(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public ZZActivity getZZActivity() {
        return this;
    }

    public void showDialog() {
        showDialog("", false, false, null);
    }

    public void showDialog(String msg) {
        showDialog(msg, false, false, null);
    }


    /**
     * ???????????????????????????????????????login???singleTop???????????????????????????
     * ??????????????????????????????20?????????
     * singleInstance????????????
     */
    private static long lastTime = 0;

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
    public void showCommonDialog(String title, String msg, String leftStr, String rightStr, View.OnClickListener left, View.OnClickListener right) {
        activityPresent.showCommonDialog(title, msg, leftStr, rightStr, left, right);
    }

    /**
     * ??????commondialog
     */
    @Override
    public void closeCommonDialog() {
        activityPresent.closeCommonDialog();
    }


    @Override
    public void onBefore() {

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
                activityPresent.startClass(url);
                AdvertTracker.advertTracker(this, id);
            }
        }
    }


    /**
     * ??????????????????,????????????
     */
    protected void setWhiteStatus() {
        setStatusBarColor(MyApp.getMyColor(R.color.status_bar_color), 1);
        if ("0".equals(MyApp.getMyString(R.string.status_bar_text_color))) {//BlackText
            StatusBarUtil.setOPPOStatusTextColor(true, this);
            StatusBarUtil.setOPPOStatusTextColor(true, this);
        } else {//WhiteText
            StatusBarUtil.setOPPOStatusTextColor(false, this);
        }
    }

    /**
     * ?????????????????????
     * type 0 ??????  1 ??????
     */
    public void setStatusBarTextColor(int type) {
        if (type == 0) {
            StatusBarUtil.setOPPOStatusTextColor(true, this);
        } else if (type == 1) {
            StatusBarUtil.setOPPOStatusTextColor(false, this);
        }
    }


    /**
     * ?????????,?????????????????????
     */
    protected void noStatus() {
        setStatusBarColor(MyApp.getMyColor(R.color.no_color), 1);
        if ("0".equals(MyApp.getMyString(R.string.status_bar_text_color))) {//BlackText
            StatusBarUtil.setOPPOStatusTextColor(true, this);
        } else {//WhiteText
            StatusBarUtil.setOPPOStatusTextColor(false, this);
        }
        hintStatusBar();
    }

    /**
     * ???????????????StartActivity
     * ?????????,?????????????????????
     */
    protected void startActivitynoStatus() {
        setStatusBarColor(MyApp.getMyColor(R.color.startWelcome), 1);
        if ("0".equals(MyApp.getMyString(R.string.status_bar_text_color))) {//BlackText
            StatusBarUtil.setOPPOStatusTextColor(true, this);
        } else {//WhiteText
            StatusBarUtil.setOPPOStatusTextColor(false, this);
        }
        hintStatusBar();
    }

    /**
     * ???????????????,?????????????????????
     */
    public void hintStatusBar() {
        ViewGroup contentView = findViewById(android.R.id.content);
        if (contentView != null) {
            contentView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * ?????????????????????
     *
     * @param color
     */
    public void setStatusBarColor(@ColorInt int color) {
        StatusBarUtil.setColor(this, color);
    }


    /**
     * ?????????????????????
     *
     * @param color
     * @param statusBarAlpha ?????????
     */
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }

    /**
     * webview??????
     *
     * @param url
     */
    public void gotoWebView(String url) {
        gotoWebView(url, "", false);
    }

    /**
     * webview??????
     *
     * @param url
     */
    public void gotoWebView(String url, boolean needService) {
        gotoWebView(url, "", needService);
    }

    /**
     * webview??????
     *
     * @param url
     */
    public void gotoWebView(String url, String title, boolean needService) {
        gotoWebViewPost(url, title, "", needService);
    }

    /**
     * webview??????
     *
     * @param url
     */
    public void gotoWebViewPost(String url, String title, String params, boolean needService) {
        if (!CommonUtils.StringNotNull(url)) {
            return;
        }
        startClass(R.string.WebViewActivity, IntentUtils.getHashObj(new String[]{
                StringConstants.URL, url, StringConstants.TITLE, title, StringConstants.PARAMS, params,
                StringConstants.NEED_SERVICE, String.valueOf(needService)}));
    }

    /**
     * ????????????
     */
    public void gotoService() {
        if (JudgeDoubleUtils.isDoubleClick()) {
            return;
        }
        checkLogin(R.string.ServiceActivity);
    }

    //??????????????????????????????????????????
    private long firstTime = 0;

    /**
     * ??????????????????
     */
    public boolean exitBy2Click(Context context) {
        if (System.currentTimeMillis() - firstTime >= 2000 && context != null) {
            toast(context.getString(R.string.toast_next_press_exit));
            firstTime = System.currentTimeMillis();
            return false;
        } else {
            return true;
        }
    }


    /**
     * ??????????????????,?????????????????????????????????
     */
    protected void openRefresh(RefreshLayout refreshLayout, LoadingLayout mLoadingLayout) {
        if (mLoadingLayout != null && mLoadingLayout.notLoading()) {
            if (refreshLayout != null) {
                refreshLayout.setEnablePureScrollMode(false);//???????????????????????????
            }
        }
    }

    /**
     * ??????app
     *
     * @param downloadId ????????????id
     */
    public void installApp(long downloadId) {
        try {
            Context context = getContext();
            if (downloadId == -1 || context == null) {
                return;
            }
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = downloadManager.query(query.setFilterById(downloadId));
            if (cursor != null && cursor.moveToFirst()) {
                String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String path = Uri.parse(fileUri).getPath();
                cursor.close();
                if (!TextUtils.isEmpty(path)) {
                    File apkFile = null;
                    //????????????????????????
                    String versionName = MyDataManager.GetApkVersionName(context, path);
                    if (CommonUtils.StringNotNull(versionName)) {//????????????????????????????????????
                        apkFile = new File(path);
                    }
                    if (apkFile == null || !apkFile.exists() || !apkFile.isFile()) {
                        downError();
                        return;
                    }
                    //??????md5
                    String md5 = SPUtil.getUpdateMd5();
                    String downBrowserUrl = SPUtil.getDownBrowserUrl();
                    if (CommonUtils.StringNotNull(md5)) {
                        boolean md5IsRight = Md5Util.checkFileMd5(md5, apkFile);
                        if (!md5IsRight) {//MD5???????????????,?????????????????????
                            MyDataManager.deleteUpdateApk(context);
                            Intent webIntent = IntentUtils.getWebIntent(downBrowserUrl);
                            if (webIntent != null) {
                                toast("????????????????????????????????????????????????????????????????????????");
                                context.startActivity(webIntent);
                            }
                            return;
                        }
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    } else {
                        //Android7.0????????????uri??????contentProvider
                        Uri apkUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileProvider", apkFile);
                        //Granting Temporary Permissions to a URI
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {//???????????????????????????????????????,????????????,????????????????????????
                UpdateDialog dialog = MyDialogManager.getManager().getUpdateDialog();
                if (dialog != null) {
                    dialog.initStatus();
                    dialog.cancelUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downError() {
        UpdateDialog dialog = MyDialogManager.getManager().getUpdateDialog();
        if (dialog != null) {
            dialog.cancelUpdate();
            dialog.clickUpdate();
        }
    }

    /**
     * ???????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AppFrontBackEvent event) {
        if (event != null) {
            appFrontBackEvent(event.isFront);
        }
    }

    public void appFrontBackEvent(boolean isFront) {
        this.isFront = isFront;
    }

    /**
     * ??????DecorView??????
     *
     * @param isOpen true,??????????????????  false,?????????????????????
     */
    public void setDecorViewBg(boolean isOpen) {
        if (getWindow() != null) {
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.setBackgroundColor(getResources().getColor(isOpen ? R.color.no_color : R.color.activity_bg));
        }
    }

    /**
     * ????????????
     */
    public void hideInput() {
        View focus = getCurrentFocus();
        if (focus != null) {
            KeyBoardUtils.hideInput(this, focus);
        }
    }


}
