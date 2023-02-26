package com.kaiserkalep.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.SellDetailsBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.service.KKpayBackgroundService;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 卖单详情
 */
public class SellCoinDetailsActivity extends ZZActivity implements View.OnClickListener {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.sl_content)
    ShadowLayout slContent;
    @BindView(R.id.tv_statusbar)
    ImageView tvStatusbar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_top_title)
    TextView ivTopTitle;
    @BindView(R.id.iv_top_right_img)
    ImageView ivTopRightImg;
    @BindView(R.id.tv_top_right_text)
    TextView tvTopRightText;

    @BindView(R.id.tv_order_state)
    TextView tvOrderState;
    @BindView(R.id.iv_user_photo)
    ImageView ivUserPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_money_tip)
    TextView tvMoneyTip;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.ll_state_icon)
    LinearLayout llStateIcon;
    @BindView(R.id.tv_oderno)
    TextView tvOderno;

    @BindView(R.id.rl_twomoney_view)
    RelativeLayout rlTwomoneyView;
    @BindView(R.id.tv_two_money_tip)
    TextView tvTwoMoneyTip;
    @BindView(R.id.tv_two_money)
    TextView tvTwoMoney;
    @BindView(R.id.sl_revoke)
    ShadowLayout slRevoke;

    private String title = "";
    private String sellTitle;
    private String sellTitleSecond;
    private String money;
    private String amountDeal;
    private String amountCancel;
    private String amountFreeze;
    private String status;
    private String type;
    private String orderNo;
    private String CancellationType = "";//撤单字段-1取消/-2部分取消

    @Override
    public int getViewId() {
        return R.layout.activity_sellcoin_details;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
        setStatusBarTextColor(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.My_Sell_Order_details_title);
        ivTopTitle.setText(title);
        try {
            HashMap<String, String> urlParam = getUrlParam();
            if (null != urlParam && urlParam.size() > ZERO) {
                sellTitle = urlParam.get(StringConstants.TITLE);
                sellTitleSecond = urlParam.get(StringConstants.TITLE_SECOND);
                orderNo = urlParam.get(StringConstants.ORDERNO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivBack.setImageResource(R.drawable.icon_top_back_white);
        ivTopRightImg.setBackgroundResource(R.drawable.icon_home_msg);
        tvTopRightText.setText(MyApp.getLanguageString(this, R.string.trade_dispute));
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setMargins2(slContent, 0, UIUtils.dip2px(55) + statusBarHeight, 0, 0);
        UIUtils.setHeight(tvStatusbar, statusBarHeight);
        refreshLayout.setEnablePureScrollMode(false);
        refreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setOnRefreshListener(refreshLayout -> getOrderSellDetail(false));

        tvMoneyTip.setText(sellTitle);
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(SPUtil.getUserAvatar()), ivUserPhoto, R.drawable.icon_default_photo);
        tvUserName.setText(SPUtil.getRealName());
        tvNickname.setText(SPUtil.getNickName());
        tvOderno.setText(MyApp.getLanguageString(this, R.string.mysell_orderno) + ":" + orderNo);

        getOrderSellDetail(true);
    }

    /**
     * 获取卖单详情
     */
    private void getOrderSellDetail(boolean needDialog) {
        new FundImpl(new ZZNetCallBack<SellDetailsBean>(this, SellDetailsBean.class) {
            @Override
            public void onSuccess(SellDetailsBean response) {
                if (null != response) {
                    money = response.getAmount();
                    amountDeal = response.getAmountDeal();
                    amountCancel = response.getAmountCancel();
                    amountFreeze = response.getAmountFreeze();
                    status = response.getStatus();
                    type = response.getReceiveType();
                    if (Config.ZERO_STRING.equals(status)
                            || Config.ONE_STRING.equals(status)
                    ) {
                        MySellBean.RowsDTO r = new MySellBean.RowsDTO();
                        r.setOrderNo(response.getOrderNo());
                        r.setStatus(response.getStatus());
                        MyApp.pendingOrderMap.put(r.getOrderNo(), r);
                    } else {
                        MyApp.pendingOrderMap.remove(response.getOrderNo());
                        MyApp.getApplication().checkOrder();
                    }
                    initData();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog).setNeedToast(true)).getOrderSellDetail(orderNo);
    }

    private void initData() {
        tvOrderState.setText(MySellBean.getOrderStatusTitle(this, status));
        tvMoney.setText(Config.subOneAndDot(money));
        computeMoneyView();

        //设置支付icon
        if (llStateIcon.getChildCount() > ZERO) {
            llStateIcon.removeAllViews();
        }
        if (CommonUtils.StringNotNull(type)) {
            String[] split = type.split(StringConstants.comma);
            int wheight = UIUtils.dip2px(17);
            int Margin = UIUtils.dip2px(4);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wheight, wheight);
            layoutParams.rightMargin = Margin;
            for (int i = 0; i < split.length; i++) {
                int payStrIcon = WalletManageBean.getPayStrIcon(split[i]);
                if (Config.EGATIVE_ONE != payStrIcon) {
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(payStrIcon);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(layoutParams);
                    llStateIcon.addView(imageView);
                }
            }
        }
    }

    /**
     * 通过状态设置出售/可售/取消/完成 UI效果
     */
    private void computeMoneyView() {
        if (CommonUtils.StringNotNull(sellTitleSecond)) {//不为空时,为我的挂单进入
            tvTwoMoneyTip.setText(sellTitleSecond);
            rlTwomoneyView.setVisibility(View.VISIBLE);
            switch (status) {
                case Config.ZERO_STRING://未销售
                case Config.ONE_STRING://销售部分
                    ViewVisibility();
                    break;
                case Config.TWO_STRING://完成
                    slRevoke.setVisibility(View.GONE);
                    tvTwoMoney.setText(Config.subOneAndDot(amountDeal));
                    break;
                case Config.OWN_ONE_STRING://取消
                case Config.OWN_TWO_STRING://部分取消
                    slRevoke.setVisibility(View.GONE);
                    tvTwoMoney.setText(Config.subOneAndDot(amountCancel));
                    break;
                default:
                    tvTwoMoney.setText(ZERO_STRING);
                    slRevoke.setVisibility(View.GONE);
                    break;
            }
        } else {//第二标题为空时，为刚卖单时跳转。
            tvTwoMoney.setText("");
            tvTwoMoneyTip.setText("");
            ViewVisibility();
        }
    }

    private void ViewVisibility() {
        double dmoney = getDouble(money);
        double damountDeal = getDouble(amountDeal);//已成交金额
        double damountCancel = getDouble(amountCancel);//取消金额
        double damountFreeze = getDouble(amountFreeze);//冻结金额
        if (damountCancel > ZERO || damountFreeze > ZERO) {
            slRevoke.setVisibility(View.GONE);
            double subtraction = Config.subtraction(dmoney, damountDeal, damountCancel, damountFreeze);
            if (subtraction > ZERO) {
                tvTwoMoney.setText(Config.subOneAndDot("" + subtraction));
            } else {
                tvTwoMoney.setText(ZERO_STRING);
            }
        } else {
            slRevoke.setVisibility(View.VISIBLE);
            if (damountDeal > ZERO) {
                CancellationType = Config.OWN_TWO_STRING;
                double subtraction = Config.subtraction(dmoney, damountDeal);
                if (subtraction > ZERO) {
                    tvTwoMoney.setText(Config.subOneAndDot("" + subtraction));
                } else {
                    tvTwoMoney.setText(ZERO_STRING);
                }
            } else {
                CancellationType = Config.OWN_ONE_STRING;
                tvTwoMoney.setText(Config.subOneAndDot(money));
            }
        }
    }

    private double getDouble(String string) {
        double dStr = ZERO;
        try {
            dStr = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return dStr;
    }

    @OnClick({R.id.iv_back, R.id.ll_top_right, R.id.iv_copy_orderno, R.id.iv_copy_icon, R.id.sl_revoke})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_top_right:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                gotoService();
                break;
            case R.id.iv_copy_orderno:
                if (null != tvOderno) {
                    String string = tvOderno.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
            case R.id.iv_copy_icon:
                if (null != tvUserName) {
                    String string = tvUserName.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
            case R.id.sl_revoke:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (CommonUtils.StringNotNull(CancellationType)) {
                    revokeOrder("");
                } else {
                    toast(MyApp.getLanguageString(this, R.string.Share_Error));
                }
//                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.InputPassWordDialog,
//                        new PayPassWordDialogData(o -> {
//                            if (null != o && o.length() == SIX) {
//                                revokeOrder(o);
//                                MyDialogManager.getManager().disMissInputDialog();
//                            } else {
//                                toast(MyApp.getLanguageString(getContext(), R.string.please_input_passw_finish_tip));
//                            }
//                        }, v -> MyDialogManager.getManager().disMissInputDialog()));
                break;
        }
    }

    /**
     * 撤单
     *
     * @param pass
     */
    public void revokeOrder(String pass) {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                MyApp.pendingOrderMap.remove(orderNo);
                MyApp.getApplication().checkOrder();
                EventBusUtil.post(new OrderStatusNoticeBean());
                SellCoinDetailsActivity.this.finish();
                toast(MyApp.getLanguageString(SellCoinDetailsActivity.this, R.string.order_Revoke_Success));
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                getOrderSellDetail(true);
                if (null != slRevoke) {
                    slRevoke.setVisibility(View.GONE);
                }
            }
        }).setChangeStatus(orderNo, StringConstants.SELL, CancellationType, "", pass);
    }
}
