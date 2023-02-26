package com.kaiserkalep.bean;

import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZFragment;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.TokenTimeOutEvent;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.ui.activity.LoginActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.WebViewActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.ImageUtil;
import com.kaiserkalep.utils.JSONUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;

/**
 * @Auther: Jack
 * @Date: 2021/1/20 15:06
 * @Description:
 */
public class YYJavaScript {

    YYJavaScriptListener listener;
    ActivityIntentInterface activityIntentInterface;

    public YYJavaScript() {
    }

    public YYJavaScript(ActivityIntentInterface activityIntentInterface, YYJavaScriptListener listener) {
        this.listener = listener;
        this.activityIntentInterface = activityIntentInterface;
    }


    public interface YYJavaScriptListener {
        /**
         * 设置副标题js回调
         *
         * @param mainTitle 主标题
         * @param subhead   副标题
         * @param url       副标题跳转url
         */
        void setTitle(String mainTitle, String subhead, String url);
        /**
         * 显示客服按钮
         */
        void showCustomer();
    }

    /**
     * js回传副标题及跳转链接  window.yyJavaScript.setTitle(主标题,副标题,url);
     *
     * @param mainTitle 主标题
     * @param subhead   副标题
     * @param url       跳转链接
     */
    @JavascriptInterface
    public void setTitle(String mainTitle, String subhead, String url) {
        MyApp.post(() -> {
            if (listener != null) {
                listener.setTitle(mainTitle, subhead, url);
            }
        });
    }


    /**
     * 保存推荐好友二维码  window.yyJavaScript.saveRecommendQR(url);
     *
     * @param url 二维码下载链接
     */
    @JavascriptInterface
    public void saveRecommendQR(String url) {
        if (!CommonUtils.StringNotNull(url)) {
            return;
        }
        MyApp.post(() -> {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CommonDialog,
                    new CommDialogData("提示", "保存图片到本地",
                            "取消", "保存", null, v -> {
                        if (activityIntentInterface != null) {
                            ImageUtil.savePhoneByLinkUrl((ZZActivity) activityIntentInterface.getActivity(), url);
                        }
                    }));
        });
    }

    /**
     * 显示客服按钮  window.yyJavaScript.showCustomer();
     */
    @JavascriptInterface
    public void showCustomer() {
        MyApp.post(() -> {
            if (listener != null) {
                listener.showCustomer();
            }
        });
    }

    /**
     * js回调登录失效  window.yyJavaScript.loginOut();
     */
    @JavascriptInterface
    public void loginOut() {
        loginOut("");
    }

    /**
     * js回调登录失效  window.yyJavaScript.loginOut(失效文描);
     *
     * @param msg 失效
     */
    @JavascriptInterface
    public void loginOut(String msg) {
        MyApp.post(() -> {
            EventBusUtil.post(new TokenTimeOutEvent(false, false, ""));
            MyActivityManager.getActivityManager().popAllActivityExceptMore(MainActivity.class, LoginActivity.class);
            if (activityIntentInterface != null) {
                activityIntentInterface.toast(CommonUtils.StringNotNull(msg) ?
                        msg : MyApp.getLanguageString(activityIntentInterface.getContext(),R.string.token_out), -1);
                activityIntentInterface.startClass(MyApp.getMyString(R.string.LoginActivity), IntentUtils.getHashObj(new String[]{
                        StringConstants.ONLY_FINISH, String.valueOf(true)}), false, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
    }

    /**
     * js回调开启新webView页面
     *
     * @param url window.yyJavaScript.goToNewWeb(url);
     */
    @JavascriptInterface
    public void goToNewWeb(String url) {
        MyApp.post(() -> {
            if (CommonUtils.StringNotNull(url) && activityIntentInterface != null
                    && activityIntentInterface instanceof ZZFragment) {
                ZZFragment fragment = (ZZFragment) activityIntentInterface;
                fragment.gotoWebView(BaseNetUtil.jointWebViewUrl(url));
            }
        });
    }

    /**
     * js回调关闭当前页面
     * <p>
     * window.yyJavaScript.finish();
     */
    @JavascriptInterface
    public void finish() {
        MyApp.post(() -> {
            if (activityIntentInterface != null && activityIntentInterface.getActivity() != null) {
                activityIntentInterface.getActivity().finish();
            }
        });
    }

}
