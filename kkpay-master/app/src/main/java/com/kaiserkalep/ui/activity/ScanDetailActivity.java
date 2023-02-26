package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ScanOrderDataBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.FillBlankView;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.SIX;

/**
 * 扫码跳转页面
 */
public class ScanDetailActivity extends ZZActivity implements View.OnClickListener {

    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_orderno)
    TextView tvOrderno;
    @BindView(R.id.tv_money)
    TextView tvMoney;

    @BindView(R.id.fbv_password)
    FillBlankView fbvPasspay;
    @BindView(R.id.sl_pay)
    ShadowLayout slPay;

    private String title = "";
    private String orderNo = "";

    @Override
    public int getViewId() {
        return R.layout.activity_scandetail;
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
        title = MyApp.getLanguageString(getContext(), R.string.Scan_Detail_title);
        orderNo = Config.getUrlValue(getIntent().getStringExtra(StringConstants.DATA));
        if (TextUtils.isEmpty(orderNo)) {
            toast(MyApp.getLanguageString(this, R.string.Share_Data_Error));
            return;
        }
        tvTitle.setText(title);
        tvOrderno.setText(orderNo);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        mLoading.setErrorText(MyApp.getLanguageString(getContext(), R.string.no_content));

        initListener();
        getOrderData(true);
    }

    private void initListener() {
        fbvPasspay.setOnTextChangedListener(length -> slPay.setClickable(length == SIX));
    }

    /**
     * 获取订单信息
     */
    private void getOrderData(boolean needDialog) {
        if (needDialog && null != mLoading) {
            mLoading.showLoading();
        }
        new FundImpl(new ZZNetCallBack<ScanOrderDataBean>(this, ScanOrderDataBean.class) {
            @Override
            public void onSuccess(ScanOrderDataBean response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(response.getAvatar()), ivPhoto, R.drawable.icon_kkpay_logo);
                    tvMoney.setText(Config.subOneAndDot(response.getAmount()));
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
        }.setNeedDialog(false).setNeedToast(true)).getPayOrder(orderNo);
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

    private void customBack() {
        if (!MyActivityManager.getActivityManager().isContains(MainActivity.class)) {
            Intent intent = new Intent(this, MainActivity.class);
            ScanDetailActivity.this.startActivity(intent);
        }
        ScanDetailActivity.this.finish();
    }

    @OnClick({R.id.iv_top_left, R.id.iv_copy_icon, R.id.sl_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_left:
                customBack();
                break;
            case R.id.iv_copy_icon:
                if (null != tvOrderno) {
                    String str = tvOrderno.getText().toString();
                    CommonUtils.copyText(getContext(), str);
                }

                break;
            case R.id.sl_pay:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                String allText = fbvPasspay.getAllText();
                if (CommonUtils.StringNotNull(allText) && allText.length() == SIX) {
                    getToPayOrder(allText);
                }
                break;
            case R.id.retry_button:
                getOrderData(true);
                break;
        }
    }

    /**
     * 支付订单
     *
     * @param pass
     */
    private void getToPayOrder(String pass) {
        new FundImpl(new ZZNetCallBack<ScanOrderDataBean>(this, ScanOrderDataBean.class) {
            @Override
            public void onSuccess(ScanOrderDataBean response) {
                toast(MyApp.getLanguageString(ScanDetailActivity.this, R.string.OrderNo_Pay_Success));
                customBack();
            }
        }.setNeedDialog(true).setNeedToast(true)).getAgentMemberOrderPay(orderNo, pass);
    }
}