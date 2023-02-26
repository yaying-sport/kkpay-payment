package com.kaiserkalep.utils;

import android.annotation.SuppressLint;
import android.webkit.WebView;

import com.kaiserkalep.bean.YYJavaScript;
import com.kaiserkalep.interfaces.ActivityIntentInterface;

/**
 * webview工具类
 *
 * @Auther: Jack
 * @Date: 2020/12/18 11:19
 * @Description:
 */
public class WebViewUtils {

    private static WebViewUtils instance;

    private WebViewUtils() {
    }

    public static WebViewUtils getInstance() {
        if (instance == null) {
            instance = new WebViewUtils();
        }
        return instance;
    }


    /**
     * 添加Javascript
     */
    @SuppressLint("JavascriptInterface")
    public void addYYJavaScript(WebView webView, ActivityIntentInterface activityIntentInterface, YYJavaScript.YYJavaScriptListener listener) {
        if (webView != null) {
            webView.addJavascriptInterface(new YYJavaScript(activityIntentInterface, listener), "yyJavaScript");
        }
    }
}
