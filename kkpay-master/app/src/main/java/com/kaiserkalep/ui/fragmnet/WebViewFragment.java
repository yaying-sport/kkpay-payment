package com.kaiserkalep.ui.fragmnet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZFragment;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.YYJavaScript;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.IWebViewInterface;
import com.kaiserkalep.mydialog.CommonDialog;
import com.kaiserkalep.ui.activity.WebViewActivity;
import com.kaiserkalep.utils.BASE64Utils;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.HTTPSCerUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.WebViewUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.WebViewTabLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * webview
 *
 * @Auther: Administrator
 * @Date: 2019/6/10 0010 17:31
 * @Description:
 */
public class WebViewFragment extends ZZFragment implements View.OnClickListener,
        WebViewTabLayout.WebViewTabListener, YYJavaScript.YYJavaScriptListener {


    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.loading)
    LoadingLayout loading;
    //    @BindView(R.id.web_tab)
//    WebViewTabLayout webTab;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    private String myUrl = "";
    IWebViewInterface listener;

    private String title;
    private String params;
    //    private int tabHeight;
//    private ValueAnimator valueShow;
//    private int tsbStatus;//tab状态 0,初始 1,进入过第二级链接
    private String userAgent = "";
    private static final int CHOOSE_REQUEST_CODE = 10012;
    private ValueCallback<Uri[]> uploadFiles;
    private Map<String, String> mUrlHost = new HashMap<>();//key:原域名，value：新ip地址


    @Override
    public int getViewId() {
        return R.layout.fragment_web_view;
    }

    @Override
    protected void afterViews() {
        if (null == getActivity()) {
            return;
        }
//        LanguageUtil.upDataAppLanguage(getActivity(), SPUtil.getStringValue(LANGUAGE_TYPE));

        List<String> defaultDomainDnsUrl = SPUtil.getDefaultDomainDnsUrl();
        List<String> defaultDomainUrl = SPUtil.getDefaultDomainUrl();
        for (int i = 0; i < defaultDomainUrl.size(); i++) {
            if (i < defaultDomainDnsUrl.size() && null != defaultDomainDnsUrl.get(i)) {
                String sYm = defaultDomainUrl.get(i);
                String httpHost = Config.getHttpHost(sYm);
                mUrlHost.put(httpHost, defaultDomainDnsUrl.get(i));
            }
        }

        loading.setRetryListener(this);
        loading.showContent();
        userAgent = " " + MyApp.getMyString(R.string.user_agent);

        Bundle arguments = getArguments();
        if (arguments != null) {
            myUrl = arguments.getString(StringConstants.URL);
            title = arguments.getString(StringConstants.TITLE);
            params = arguments.getString(StringConstants.PARAMS);
        }

        setWebSetting();
        loadUrl();
    }

    private void loadUrl() {
        if (CommonUtils.StringNotNull(myUrl)) {
            setWebSetting();
            if (null != params && CommonUtils.StringNotNull(params)) {
                webView.loadUrl(myUrl + "?" + "payUrl=" + params);
            } else {
                webView.loadUrl(myUrl);
            }
        } else {
            loading.showEmpty();
        }
    }

    private void loadUrl(WebView webView, String url) {
        if (webView != null) {
            HashMap<String, String> headMap = new HashMap<>();
            headMap.put(StringConstants.X_AUTH_TOKEN, SPUtil.getToken());
            webView.loadUrl(url, headMap);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebSetting() {

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。 这个取决于setSupportZoom(), 若setSupportZoom(false)，则该WebView不可缩放，这个不管设置什么都不能缩放。
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        settings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置是否允许 JS 开启新窗口(function window.open())
        settings.setJavaScriptEnabled(true);
//        webView.removeJavascriptInterface("searchBoxJavaBridge_");
//        Intent intent = new Intent();
//        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
//        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
//        settings.setBlockNetworkImage(true);
        String userAgentString = settings.getUserAgentString();
        if (CommonUtils.StringNotNull(userAgentString) && !userAgentString.contains(userAgent)) {
            settings.setUserAgentString(userAgentString + userAgent);
        }
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        settings.setSaveFormData(true);
        // 自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //https http混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //cookie设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(initWebViewClient());
        webView.setWebChromeClient(initWebChromeClient());

        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            // 处理下载事件
            IntentUtils.gotoDefaultWeb(WebViewFragment.this, url);
        });

        WebViewUtils.getInstance().addYYJavaScript(webView, this, this);
    }


    protected WebChromeClient initWebChromeClient() {
        return new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                CommonDialog commonDialog = new CommonDialog(WebViewFragment.this);
                commonDialog.show(new CommDialogData(MyApp.getLanguageString(getContext(), R.string.webviewfragment_DialogBox), message,
                        null, MyApp.getLanguageString(getContext(), R.string.Share_certain), null, null, false, false));
                result.confirm();// 因为没有绑定事1件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //超时找不到网页
                if (title != null && title.equals("找不到网页")) {
                    //网络错误title设置空
                    if (listener != null) {
                        listener.setTitle("");
                    }
                }
            }

            // For Android  >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }

            private void openFileChooseProcess() {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Choose"), CHOOSE_REQUEST_CODE);
            }
        };
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.d("requestCode===", requestCode + "====");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_REQUEST_CODE) {
                if (null != uploadFiles) {
                    Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                            : data.getData();
                    uploadFiles.onReceiveValue(new Uri[]{result});
                    uploadFiles = null;
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (null != uploadFiles) {
                uploadFiles.onReceiveValue(null);
                uploadFiles = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //恢复pauseTimers状态
        try {
            if (webView != null && !isHidden()) {
                webView.onResume();
                webView.resumeTimers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        //它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
        try {
            if (webView != null) {
                webView.onPause();
                webView.pauseTimers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != loading) {
            loading.removeAllViews();
        }
        if (null != webView) {
            webView.destroy();
            webView = null;
        }

        if (null != mUrlHost) {
            mUrlHost.clear();
        }

    }

    private WebResourceResponse getNewResponse(String url, String host, Map<String, String> headers, String method) {
        try {
            OkHttpClient.Builder okhttpclientbuilde = new OkHttpClient().newBuilder();
            HTTPSCerUtils.setTrustAllCertificate(okhttpclientbuilde);
            OkHttpClient build = okhttpclientbuilde.build();
            Request.Builder builder = new Request.Builder().url(url.trim());
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                builder.addHeader(key, headers.get(key));
            }
            if ("POST".equalsIgnoreCase(method)) {
                String bodyStr = headers.get("h5-body");
                RequestBody body;
                if (CommonUtils.StringNotNull(bodyStr)) {
                    String str = BASE64Utils.decryptBASE64(bodyStr);
                    body = RequestBody.create(BaseNetUtil.mediaType, CommonUtils.StringNotNull(str) ? str : "");
                } else {
                    body = RequestBody.create(BaseNetUtil.mediaType, "");
                }
                builder.post(body);
            }
            builder.addHeader(Config.HOST, host);
            Request request = builder.build();
            Response response = build.newCall(request).execute();
            String conentType = response.header("Content-Type", response.body().contentType().type());
            String temp = conentType.toLowerCase();
            if (temp.contains("charset=utf-8")) {
                conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");//不区分大小写的替换
            }
            if (conentType.contains(";")) {
                conentType = conentType.replaceAll(";", "");
                conentType = conentType.trim();
            }
            return new WebResourceResponse(
                    conentType,
                    response.header("Content-Encoding", "utf-8"),
                    response.body().byteStream()
            );

        } catch (Exception e) {
        }
        return null;

    }

    protected WebViewClient initWebViewClient() {
        return new WebViewClient() {

            // API 21及之后使用此方法
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request != null && request.getUrl() != null) {
                    Uri url = request.getUrl();
                    String host = url.getHost();
                    String scheme = url.getScheme().trim();
                    if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                        String sVlaue = mUrlHost.get(host);
                        if (BaseNetUtil.getOpenDns() && null != sVlaue && CommonUtils.StringNotNull(sVlaue) && !Config.ZERO_STRING.equals(sVlaue)) {
                            WebResourceResponse newResponse = getNewResponse(url.toString().replaceFirst(host, sVlaue), host, request.getRequestHeaders(), request.getMethod());
                            if (null != newResponse) {
                                return newResponse;
                            } else {
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                try {
                    if (url.startsWith(getString(R.string.scheme))) {//路由跳转
                        LogUtils.d(url);
                        startClass(url);
                        return true;
                    }
                    if (!url.startsWith("http") && !url.startsWith("https")) {
                        return true;
                    }
                } catch (Exception e) {
                    return true;
                }
                LogUtils.e("webView", "shouldOverrideUrlLoading_" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d("HtmlUrl  onPageStarted__" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.d("HtmlUrl onPageFinished__" + url + "__getThisHost=" + getThisHost());
                // html加载完成之后，添加监听js函数
                String title = view.getTitle();//回退后改变title
                if (listener != null) {
                    title = "about:blank".equalsIgnoreCase(title) ? "" : title;
                    listener.setTitle(title);
                }
                if (view.getProgress() == 100) {
                    if (listener != null) {
                        listener.fishing();
                    }
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (loading != null) {
                    loading.showError();
                }
            }


            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                Log.e("webView", "onReceivedError 2__");
                if (loading != null && webResourceRequest.isForMainFrame()) {
                    loading.showError();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                LogUtils.e("web", "页面加载ssl onReceivedSslError = " + error.toString());
                handler.proceed();//忽略证书的错误继续Load页面内容，不会显示空白页面
            }
        };
    }

    public void setListener(IWebViewInterface listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        loadNetData();
    }

    public WebView getWebView() {
        return webView;
    }

    @Override
    public void back() {
        if (webView != null /*&& (webView.canGoBack() && !myUrl.equals(webView.getPic()))*/) {
            webView.stopLoading();
            webView.goBack();
        }
    }

    @Override
    public void advance() {
        if (webView != null /*&& webView.canGoForward()*/) {
            webView.stopLoading();
            webView.goForward();
        }
    }

    @Override
    public void refresh() {
        if (webView != null) {
            webView.stopLoading();
            webView.reload();
        }
    }

    public void errorrRefresh() {
        if (null != webView) {
            loadNetData();
        }
    }

    private void loadNetData() {
        if (loading != null) {
            loading.showContent();
        }
        loadUrl();
    }


    /**
     * 设置副标题js回调
     *
     * @param mainTitle 主标题
     * @param subhead   副标题
     * @param url       副标题跳转url
     */
    @Override
    public void setTitle(String mainTitle, String subhead, String url) {
        FragmentActivity activity = getActivity();
        if (activity instanceof WebViewActivity) {
            WebViewActivity viewActivity = (WebViewActivity) activity;
            viewActivity.runOnUiThread(() -> {
                if (viewActivity.commTitle != null) {
                    viewActivity.commTitle.init(CommonUtils.StringNotNull(mainTitle) ? mainTitle : title,
                            "", subhead, 0, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String str = BaseNetUtil.jointWebViewUrl(url);
                                    gotoWebView(str);
                                }
                            });
                }
            });
        }
    }

    /**
     * 显示客服按钮
     */
    @Override
    public void showCustomer() {
        FragmentActivity activity = getActivity();
        if (activity instanceof WebViewActivity) {
            WebViewActivity viewActivity = (WebViewActivity) activity;
            viewActivity.runOnUiThread(() -> {
                if (viewActivity.commTitle != null) {
                    viewActivity.commTitle.setTvFunction("", R.drawable.icon_web_service,
                            v -> viewActivity.gotoService());
                }
            });
        }
    }

}
