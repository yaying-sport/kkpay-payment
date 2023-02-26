package com.kaiserkalep.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MySellPayTypeAdapter;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.bean.PayTypeBean;
import com.kaiserkalep.bean.SellDetailsBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.OnItemClickListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MyNestRecycleView;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.indicatorseekbar.IndicatorSeekBar;
import com.kaiserkalep.widgets.indicatorseekbar.OnSeekChangeListener;
import com.kaiserkalep.widgets.indicatorseekbar.SeekParams;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 订单详情
 */
public class BuyCoinActivity extends ZZActivity implements View.OnClickListener {

    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.sl_user_parent_view)
    ShadowLayout slUserParentView;
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

    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.ll_state_icon)
    LinearLayout llStateIcon;

    @BindView(R.id.tv_can_money)
    TextView tvCanMoney;
    @BindView(R.id.tv_all_money_top_tip)
    TextView tvAllMoneyTopTip;
    @BindView(R.id.tv_all_money_left_tip)
    TextView tvAllMoneyLeftTip;
    @BindView(R.id.tv_all_money_left_fh_tip)
    TextView tvAllMoneyLeftFhTip;
    @BindView(R.id.tv_all_money)
    TextView tvAllMoney;

    @BindView(R.id.rg_chai)
    RadioGroup rgChai;
    @BindView(R.id.rb_zero)
    RadioButton rbZero;
    @BindView(R.id.rb_one)
    RadioButton rbOne;

    @BindView(R.id.sl_scroll_parent_view)
    ShadowLayout slScrollParentView;
    @BindView(R.id.tv_seek_money)
    TextView tvSeekMoney;
    @BindView(R.id.isb_progress)
    IndicatorSeekBar isbProgress;
    @BindView(R.id.tv_seek_money_progress)
    TextView tvSeekMoneyProgress;

    @BindView(R.id.rec_pay_type)
    MyNestRecycleView recPayType;
    @BindView(R.id.sl_buy)
    ShadowLayout slBuy;

    private String title = "";
    private MySellPayTypeAdapter mySellPaytypeadapter;
    private List<PayTypeBean> payTypeList = new ArrayList<>();
    private int chaiNum = ZERO;//0,不支持   1,支持
    private String placeOrderNo;//下单订单号
    private String sellOrderNo;//卖单订单号
    private String type = "";//bank-银行卡提款，ali-支付宝，wechat-支付宝
    private String memberBankId = "";
    private SellDetailsBean selldetailsbean;
    private String canMoney = ZERO_STRING;//可售金额
    private String orderAmount = ZERO_STRING;//下单金额

    @Override
    public int getViewId() {
        return R.layout.activity_buycoin;
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
        title = MyApp.getLanguageString(getContext(), R.string.order_buy_title);
        ivTopTitle.setText(title);
        try {
            HashMap<String, String> urlParam = getUrlParam();
            if (null != urlParam && urlParam.size() > ZERO) {
                placeOrderNo = urlParam.get(StringConstants.ORDERNO);
                sellOrderNo = urlParam.get(StringConstants.SELLORDERNO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivBack.setImageResource(R.drawable.icon_top_back_white);
        ivTopRightImg.setBackgroundResource(R.drawable.icon_home_msg);
        tvTopRightText.setText(MyApp.getLanguageString(this, R.string.trade_dispute));
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setMargins2(slUserParentView, 0, UIUtils.dip2px(50) + statusBarHeight, 0, 0);
        UIUtils.setHeight(tvStatusbar, statusBarHeight);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        isbProgress.setIndicatorTextFormat("${PROGRESS} %");
        getOrderSellDetail(true);
        initRecycleView();
        initLintener();
    }

    /**
     * 获取卖单详情
     */
    private void getOrderSellDetail(boolean isStart) {
        if (null != mLoading) {
            mLoading.setErrorText("");
            if (isStart) {
                mLoading.showLoading();
            }
        }
        new FundImpl(new ZZNetCallBack<SellDetailsBean>(this, SellDetailsBean.class) {
            @Override
            public void onSuccess(SellDetailsBean response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    selldetailsbean = response;
                    setData(response);
                } else {
                    if (null != mLoading) {
                        mLoading.setErrorText(MyApp.getLanguageString(BuyCoinActivity.this, R.string.Share_Data_Error));
                        mLoading.showError();
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                if (null != mLoading) {
                    mLoading.setErrorText(msg);
                    mLoading.showError();
                }
            }
        }.setNeedDialog(!isStart)).getOrderSellDetail(sellOrderNo);
    }

    private void setData(SellDetailsBean response) {
        isbProgress.setProgress(ZERO);
        type = "";
        orderAmount = ZERO_STRING;
        canMoney = ZERO_STRING;
        slBuy.setClickable(false);

        String amount = response.getAmount();
        String amountDeal = response.getAmountDeal();
        String amountCancel = response.getAmountCancel();
        String amountFreeze = response.getAmountFreeze();
        String percentageMoney = ZERO_STRING;//待售比例
        try {
            double vAmount = Double.parseDouble(amount);
            double vAmountDeal = Double.parseDouble(amountDeal);
            double vamountCancel = Double.parseDouble(amountCancel);
            double vamountFreeze = Double.parseDouble(amountFreeze);
            if (vAmount <= ZERO) {
                toast(MyApp.getLanguageString(BuyCoinActivity.this, R.string.buy_Less_than_zero_tip));
            }
            double cMoney = Config.subtraction(vAmount, vAmountDeal, vamountCancel, vamountFreeze);
            if (cMoney > ZERO) {
                canMoney = Config.subNotAndDot(String.valueOf(cMoney));
                double multiplication = Config.multiplication(Config.divisionOperation(cMoney, vAmount), 100);
                if (multiplication > ZERO) {
                    percentageMoney = Config.subNotAndDot("" + multiplication);
                }
            } else {
                percentageMoney = "100";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ZERO_STRING.equals(response.getCanSplit())) {//订单不允许拆分购买
            tvAllMoneyTopTip.setVisibility(View.GONE);
            tvAllMoneyLeftTip.setVisibility(View.GONE);
            tvAllMoneyLeftFhTip.setVisibility(View.GONE);
            tvAllMoney.setVisibility(View.GONE);
            slScrollParentView.setVisibility(View.GONE);
            rbZero.setVisibility(View.VISIBLE);
            rbOne.setVisibility(View.GONE);
            tvCanMoney.setText(canMoney);
            orderAmount = canMoney;
        } else {//订单允许拆分购买,默认设置不拆分
            orderAmount = canMoney; //默认不拆分,取可下单金额
            tvCanMoney.setText(canMoney);
            String yishou = MyApp.getLanguageString(this, R.string.buy_coin_percentage_tip);
            tvAllMoneyTopTip.setText(yishou + percentageMoney + "%");
            tvAllMoney.setText(Config.subNotAndDot(amount));
            tvSeekMoneyProgress.setText(ZERO_STRING + "/" + canMoney);
            rbZero.setVisibility(View.VISIBLE);
            rbOne.setVisibility(View.VISIBLE);
        }
        ((RadioButton) rgChai.getChildAt(ZERO)).setChecked(true);
        tvNickname.setText(response.getUsername());
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(response.getAvatar()), ivPhoto, R.drawable.icon_default_photo);
        if (llStateIcon.getChildCount() > ZERO) {
            llStateIcon.removeAllViews();
        }
        String receiveType = response.getReceiveType();
        if (CommonUtils.StringNotNull(receiveType)) {
            String[] split = receiveType.split(StringConstants.comma);
            int wheight = UIUtils.dip2px(17);
            int Margin = UIUtils.dip2px(4);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wheight, wheight);
            layoutParams.rightMargin = Margin;
            layoutParams.gravity = Gravity.CENTER;
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
        initPayTypeList();
    }

    private void initRecycleView() {
        recPayType.closeDefaultAnimator();
        recPayType.setNestedScrollingEnabled(false);
        recPayType.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recPayType.setLayoutManager(new GridLayoutManager(this, 2));
        mySellPaytypeadapter = new MySellPayTypeAdapter(this, payTypeList, PayTypeListener);
        recPayType.setAdapter(mySellPaytypeadapter);
    }

    private void initPayTypeList() {
        if (null != selldetailsbean) {
            if (payTypeList.size() > ZERO) {
                payTypeList.clear();
            }

//            String receiveType = selldetailsbean.getReceiveType();
//            String[] split = receiveType.split(StringConstants.comma);
//            for (int i = 0; i < split.length; i++) {
//                String s = split[i];
//                switch (s) {
//                    case StringConstants.PAYTYPE_WECHAT:
//                        bindPayWechat = SPUtil.getBindPayWechat();
//                        break;
//                    case StringConstants.PAYTYPE_ALI:
//                        bindPayAlipay = SPUtil.getBindPayAlipay();
//                        break;
//                    case StringConstants.PAYTYPE_BANK:
//                        bindPayBank = SPUtil.getBindPayBank();
//                        break;
//                }
//            }
//            payTypeList.add(new PayTypeBean(bindPayWechat, StringConstants.PAYTYPE_WECHAT, MyApp.getLanguageString(BuyCoinActivity.this, R.string.Wechat_name)));
//            payTypeList.add(new PayTypeBean(bindPayAlipay, StringConstants.PAYTYPE_ALI, MyApp.getLanguageString(BuyCoinActivity.this, R.string.Alipay_name)));
//            payTypeList.add(new PayTypeBean(bindPayBank, StringConstants.PAYTYPE_BANK, MyApp.getLanguageString(BuyCoinActivity.this, R.string.Bank_name)));

            //
            boolean wechatEnable = false;
            boolean aliEnable = false;
            boolean bankEnable = false;
            String receiveType = selldetailsbean.getReceiveType();
            String[] split = receiveType.split(StringConstants.comma);
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                switch (s) {
                    case StringConstants.PAYTYPE_WECHAT:
                        wechatEnable = true;
                        break;
                    case StringConstants.PAYTYPE_ALI:
                        aliEnable = true;
                        break;
                    case StringConstants.PAYTYPE_BANK:
                        bankEnable = true;
                        break;
                }
            }
            //
            List<WalletManageBean> myWalletList = SPUtil.getPaymentList();
//            boolean bindPayWechat = false;
//            boolean bindPayAlipay = false;
//            boolean bindPayBank = false;
            for (WalletManageBean w : myWalletList) {
                switch (w.getType()) {
                    case 0: {
                        PayTypeBean b = new PayTypeBean(wechatEnable, StringConstants.PAYTYPE_WECHAT, MyApp.getLanguageString(getContext(), R.string.Wechat_name));
                        b.setChannelIcon(w.getIcon());
                        payTypeList.add(b);
                        break;
                    }
                    case 1: {
                        PayTypeBean b = new PayTypeBean(aliEnable, StringConstants.PAYTYPE_ALI, MyApp.getLanguageString(getContext(), R.string.Alipay_name));
                        b.setChannelIcon(w.getIcon());
                        payTypeList.add(b);
                        break;
                    }
                    case 2: {
                        PayTypeBean b = new PayTypeBean(bankEnable, StringConstants.PAYTYPE_BANK, w.getBankName());
                        b.setChannelIcon(w.getIcon());
                        b.setMemberBankId(w.getId());
                        payTypeList.add(b);
                        break;
                    }
                }
            }
            if (null != mySellPaytypeadapter) {
                mySellPaytypeadapter.changeDataUi(payTypeList);
            }
        }
    }

    /**
     * 取款方式选择
     */
    private OnItemClickListener PayTypeListener = (view, position) -> {
        type = "";
        if (CommonUtils.ListNotNull(payTypeList)) {
            for (int i = 0; i < payTypeList.size(); i++) {
                PayTypeBean paytypebean = payTypeList.get(i);
                if (paytypebean != null) {
                    if (i == position && paytypebean.isSelect()) {
                        paytypebean.setSelect(false);
                    } else if (i == position) {
                        paytypebean.setSelect(true);
                        type = paytypebean.getChannelType();
                        memberBankId = paytypebean.getMemberBankId();
                    } else {
                        paytypebean.setSelect(false);
                    }
                }
            }
        }
        if (null != mySellPaytypeadapter) {
            mySellPaytypeadapter.changeDataUi(payTypeList);
        }
        checkParams();
    };

    private void initLintener() {
        rgChai.setOnCheckedChangeListener((group, checkedId) -> {
            int i = rgChai.indexOfChild(group.findViewById(checkedId));
            if (i == ZERO) {
                chaiNum = ZERO;
                orderAmount = canMoney;
                tvAllMoneyTopTip.setVisibility(View.GONE);
                tvAllMoneyLeftTip.setVisibility(View.GONE);
                tvAllMoneyLeftFhTip.setVisibility(View.GONE);
                tvAllMoney.setVisibility(View.GONE);
                slScrollParentView.setVisibility(View.GONE);
            } else {
                chaiNum = ONE;
                orderAmount = ZERO_STRING;
                tvAllMoneyTopTip.setVisibility(View.VISIBLE);
                tvAllMoneyLeftTip.setVisibility(View.VISIBLE);
                tvAllMoneyLeftFhTip.setVisibility(View.VISIBLE);
                tvAllMoney.setVisibility(View.VISIBLE);
                slScrollParentView.setVisibility(View.VISIBLE);
            }
        });

        isbProgress.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (null != tvSeekMoney && null != tvSeekMoneyProgress) {
                    try {
                        double v = Config.divisionOperation(seekParams.progress, 100);
                        double canMoneyNum = Double.parseDouble(canMoney);
                        if (canMoneyNum <= ZERO) {
                            orderAmount = ZERO_STRING;//可拆分,Seek滑动时设置金额
                            tvSeekMoney.setText(ZERO_STRING);
                            tvSeekMoneyProgress.setText(ZERO_STRING);
                        } else {
                            double multiplication = Config.multiplication(v, canMoneyNum);
                            String sMoney = Config.subNotAndDot("" + multiplication);
                            orderAmount = sMoney;//可拆分,Seek滑动时设置金额
                            tvSeekMoney.setText(sMoney);
                            tvSeekMoneyProgress.setText(sMoney + "/" + canMoney);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        tvSeekMoney.setText(ZERO_STRING);
                        tvSeekMoneyProgress.setText(ZERO_STRING);
                    }
                    checkParams();
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

    }

    private void buyOrder() {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(BuyCoinActivity.this, R.string.order_Buy_Success));
                EventBusUtil.post(new OrderStatusNoticeBean());
                Intent intent = new Intent(BuyCoinActivity.this, OrderDetailsActivity.class);
                intent.putExtra(StringConstants.ORDERNO, placeOrderNo);
                BuyCoinActivity.this.startActivity(intent);
                BuyCoinActivity.this.finish();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);

            }
        }.setNeedDialog(true).setNeedToast(true)).myBuy(sellOrderNo, placeOrderNo, orderAmount, type, String.valueOf(chaiNum), memberBankId);
    }

    private void checkParams() {
        double amount = ZERO;
        try {
            amount = Double.parseDouble(orderAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (amount > ZERO) {
            slBuy.setClickable(CommonUtils.StringNotNull(type));
        } else {
            slBuy.setClickable(false);
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_top_right, R.id.ll_refresh, R.id.sl_buy})
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
            case R.id.ll_refresh:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    toast(MyApp.getLanguageString(this, R.string.Frequent_operations));
                    return;
                }
                getOrderSellDetail(false);
                break;
            case R.id.sl_buy:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                buyOrder();
                break;
            case R.id.retry_button:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getOrderSellDetail(true);
                break;
        }
    }
}
