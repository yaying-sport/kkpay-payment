package com.kaiserkalep.ui.activity;

import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.StringConstants.ACCOUNT;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ScanBindData;
import com.kaiserkalep.bean.ScanOrderDataBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.FillBlankView;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 扫码跳转页面
 */
public class ScanBindActivity extends ZZActivity implements View.OnClickListener {

    @BindView(R.id.loading)
    LoadingLayout mLoading;

    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.name_TV)
    TextView name_TV;


    @BindView(R.id.confirm_SL)
    ShadowLayout confirm_SL;
    @BindView(R.id.cancel_SL)
    ShadowLayout cancel_SL;

    private String title = "";
//    private String orderNo = "";

    String a = "";
    String u = "";
    String c = "";

    @Override
    public int getViewId() {
        return R.layout.activity_merchant_bind;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    public void afterViews() {
        AndroidBug5497Workaround.assistActivity(this).setNeedStatusBar(false);
        title = MyApp.getLanguageString(getContext(), R.string.merchant_bind);
        String data = getIntent().getStringExtra(StringConstants.DATA);
        if (TextUtils.isEmpty(data)) {
            toast(UIUtils.getString(R.string.Share_Data_Error));
            finish();
            return;
        }
        data = data.replace("@", "&");//兼容ios版本 做特殊处理
        Uri uri = Uri.parse(data);
        a = uri.getQueryParameter(StringConstants.A);
        u = uri.getQueryParameter(StringConstants.U);
        c = uri.getQueryParameter(StringConstants.C);

//        tvTitle.setText(title);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        mLoading.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));

        getData(true);
    }


    /**
     * 获取订单信息
     */
    private void getData(boolean needDialog) {
        if (needDialog && null != mLoading) {
            mLoading.showLoading();
        }
        new FundImpl(new ZZNetCallBack<ScanBindData>(this, ScanBindData.class) {
            @Override
            public void onSuccess(ScanBindData response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    GlideUtil.loadRoundedImage(
                            getContext(),
                            BaseNetUtil.jointUrl(response.getLogo()),
                            ivPhoto,
                            UIUtils.dip2px(getContext(), 10),
                            R.drawable.icon_kkpay_logo);
                    name_TV.setText(Config.subOneAndDot(response.getAgentName()));
                } else {
                    if (null != mLoading) {
                        mLoading.showError();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (null != mLoading) {
                    mLoading.showError();
                }
            }
        }.setNeedDialog(false).setNeedToast(true)).getAgentMemberInfo(a);
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            customBack();
        }
        return false;
    }

    @OnClick(R.id.cancel_SL)
    void customBack() {
        if (!MyActivityManager.getActivityManager().isContains(MainActivity.class)) {
            Intent intent = new Intent(this, MainActivity.class);
            ScanBindActivity.this.startActivity(intent);
        }
        ScanBindActivity.this.finish();
    }

    @OnClick({R.id.close_IM})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_IM:
                customBack();
                break;
            case R.id.retry_button:
                getData(true);
                break;
        }
    }

    @OnClick(R.id.confirm_SL)
    void back() {
        if (JudgeDoubleUtils.isDoubleClick()) {
            return;
        }
        bindAccount();
    }

    private void bindAccount() {
        new FundImpl(new ZZNetCallBack<ScanOrderDataBean>(this, ScanOrderDataBean.class) {
            @Override
            public void onSuccess(ScanOrderDataBean response) {
                toast(MyApp.getLanguageString(ScanBindActivity.this, R.string.bind_success));
                customBack();
            }
        }.setNeedDialog(true).setNeedToast(true)).bindAgentMember(a, u, c);
    }
}