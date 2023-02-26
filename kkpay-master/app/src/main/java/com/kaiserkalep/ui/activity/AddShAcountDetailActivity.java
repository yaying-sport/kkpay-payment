package com.kaiserkalep.ui.activity;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.qrcode.encoder.QRCode;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PhotoViewPopuWindow;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AgentBindInfoBean;
import com.kaiserkalep.bean.PlayerAddBean;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 商户详情
 */
public class AddShAcountDetailActivity extends ZZActivity {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.tv_account_tip)
    TextView tvAccountTip;
    @BindView(R.id.et_account)
    CleanEditTextView etAccount;
    @BindView(R.id.sl_btn)
    ShadowLayout slBtn;

    private String title = "";
    private PhotoViewPopuWindow photoviewpopuwindow;
    private AgentBindInfoBean agentbindinfobean;
    private String id = "";
    private String address = "";
    private String wjAccount = "";
    private String type = "";
    private Bitmap qrCodeBitmap;

    @Override
    public int getViewId() {
        return R.layout.activity_addshacountdetail;
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
        id = getIntent().getStringExtra(StringConstants.ID);
        address = getIntent().getStringExtra(StringConstants.ADDRESS);
        type = getIntent().getStringExtra(StringConstants.TYPE);
        title = MyApp.getLanguageString(getContext(), R.string.setManagesh_view_title);
        commTitle.init(title);

        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> agentMemberBindInfo(false));
        agentMemberBindInfo(true);
        initlistener();
    }

    private void initlistener() {
        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = etAccount.getText();
                if (null != text) {
                    wjAccount = text.toString().trim();
                } else {
                    wjAccount = "";
                }
                checkParam();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 根据钱包地址查找代理商信息
     */
    private void agentMemberBindInfo(boolean needDialog) {
        new FundImpl(new ZZNetCallBack<AgentBindInfoBean>(this, AgentBindInfoBean.class) {
            @Override
            public void onSuccess(AgentBindInfoBean response) {
                if (null != response) {
                    agentbindinfobean = response;
                    initData();
                } else {
                    toast(MyApp.getLanguageString(AddShAcountDetailActivity.this, R.string.Share_Data_Error));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }

        }.setNeedDialog(needDialog).setNeedToast(true)).agentMemberBindInfo(address);
    }

    private void initData() {
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(agentbindinfobean.getLogo()), ivLogo, R.drawable.icon_default_photo);
        recycleBitmap();
        PhotoViewPopuWindow.createQRCode(getContext(), agentbindinfobean.getQrCode(), o -> {
            if (null != o) {
                runOnUiThread(() -> {
                    qrCodeBitmap = o;
                    ivQrcode.setImageBitmap(qrCodeBitmap);
                });
            }
        });
        String agentName = agentbindinfobean.getAgentName();
        tvAccountTip.setText(String.format(MyApp.getLanguageString(this, R.string.depositsh_wj_title), agentName));
        tvName.setText(agentName);
        tvAddress.setText(agentbindinfobean.getWalletAddress());
    }

    @OnClick({R.id.rl_qrcode_parent, R.id.sl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_qrcode_parent:
                if (null != agentbindinfobean && CommonUtils.StringNotNull(agentbindinfobean.getQrCode())) {
                    showPhotoViewDialog(BaseNetUtil.jointUrl(agentbindinfobean.getQrCode()));
                }
                break;
            case R.id.sl_btn:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                agentMemberAddUsername(address, wjAccount);
                break;
        }
    }

    /**
     * 添加商户和玩家
     *
     * @param walletAddress
     * @param username
     */
    private void agentMemberAddUsername(String walletAddress, String username) {
        slBtn.setClickable(false);
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                if (ONE_STRING.equals(type)) {
                    toast(MyApp.getLanguageString(AddShAcountDetailActivity.this, R.string.addsh_addwj_success_tip));
                }else{
                    toast(MyApp.getLanguageString(AddShAcountDetailActivity.this, R.string.addsh_addsh_success_tip));
                }
                AddShAcountDetailActivity.this.finish();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                slBtn.setClickable(true);
            }
        }.setNeedDialog(true).setNeedToast(true)).agentMemberAddUserName("", walletAddress, username);
    }


    private void checkParam() {
        if (null != slBtn) {
            slBtn.setClickable(CommonUtils.StringNotNull(wjAccount) && null != agentbindinfobean);
        }
    }

    /**
     * 释放二维码bitmap资源
     * 置空控件
     */
    private void recycleBitmap() {
        if (null != qrCodeBitmap) {
            qrCodeBitmap = null;
        }
        if (null != ivQrcode) {
            ivQrcode.setImageBitmap(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleBitmap();
        release();
    }

    /**
     * 点击查看图片详情
     */
    private void showPhotoViewDialog(String imgUrl) {
        if (photoviewpopuwindow == null) {
            photoviewpopuwindow = new PhotoViewPopuWindow(getActivity());
        }
        if (photoviewpopuwindow.isShowing()) {
            photoviewpopuwindow.dismiss();
        } else {
            View parent = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
            photoviewpopuwindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        }
        photoviewpopuwindow.showImage(true, imgUrl);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (null != photoviewpopuwindow) {
            photoviewpopuwindow.release();
        }
    }
}
