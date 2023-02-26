package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.IWebViewInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.ui.fragmnet.WebViewFragment;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;

import androidx.annotation.Nullable;


/**
 * @Auther: Jack
 * @Date: 2020/1/9 19:57
 * @Description:
 */
public class WebViewActivity extends ZZActivity implements IWebViewInterface {


    TextView tvTitle;
    SucceedCallBackListener listener;
    private WebViewFragment webViewFragment;
    private String url;

    @Override
    public int getViewId() {
        return R.layout.activity_web_view;
    }

    public static int SET_BIRTHDAY_CODE = 1120;
    public String WEB_TITLE = "";


    @Override
    public void afterViews() {
        AndroidBug5497Workaround.assistActivity(this).setNeedStatusBar(false);
        WEB_TITLE = MyApp.getLanguageString(getContext(), R.string.webviewa_title);
        String title = getStringParam(StringConstants.TITLE);
        String params = getStringParam(StringConstants.PARAMS);
        url = getStringParam(StringConstants.URL);
        commTitle.init(CommonUtils.StringNotNull(title) ? title : "", "", "",
                R.drawable.icon_refresh_web, (View.OnClickListener) this::onClickRefresh);

        if (!CommonUtils.StringNotNull(title)) {//无title传参,设置网页内title
            tvTitle = commTitle.getTvTitle();
        }

        webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StringConstants.URL, url);
        bundle.putString(StringConstants.TITLE, title);
        bundle.putString(StringConstants.PARAMS, params);
        Bundle bundleParams = getBundleParams();
        if (bundleParams != null) {
            bundle.putParcelable(StringConstants.SALE_DATA, bundleParams.getParcelable(StringConstants.SALE_DATA));
        }
        webViewFragment.setArguments(bundle);
        try {
            replace(R.id.fl_content, webViewFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        onPageStart(WEB_TITLE);
    }


    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(WEB_TITLE);
    }

    @Override
    public void setTitle(String text) {
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    @Override
    public void fishing() {
        if (listener != null) {
            listener.succeedCallBack(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != webViewFragment) {
            webViewFragment.onDestroy();
            webViewFragment = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null &&
                requestCode == SET_BIRTHDAY_CODE && webViewFragment != null) {//跳转个人资料页,设置生日后,返回刷新页面
            webViewFragment.refresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onClickRefresh(View view) {
        if (null != webViewFragment){
            webViewFragment.errorrRefresh();
        }
    }
}

