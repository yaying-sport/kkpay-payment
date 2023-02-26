package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.MySellPayTypeAdapter;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AmountInfo;
import com.kaiserkalep.bean.CoinNoticeBean;
import com.kaiserkalep.bean.OrderFromBean;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.bean.PayTypeBean;
import com.kaiserkalep.bean.AuthenDialogData;;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.OnItemClickListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MyNestRecycleView;
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
 * 卖币中心
 */
public class SellCoinCenterActivity extends ZZActivity implements View.OnClickListener {


    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.tv_userAmount)
    TextView tvUserAmount;
    @BindView(R.id.sl_etmoney_parent)
    ShadowLayout slEtmoneyParent;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.et_sellnum)
    EditText etSellNum;
    @BindView(R.id.rec_pay_type)
    MyNestRecycleView recPayType;
    @BindView(R.id.rg_chai)
    RadioGroup rgChai;
    @BindView(R.id.sl_sell)
    ShadowLayout slSell;

    private MySellPayTypeAdapter mySellPaytypeadapter;
    private String title = "";
    /**
     * 取款方式集合
     */
    private List<PayTypeBean> payTypeList = new ArrayList<>();
    private String type = "";//bank-银行卡提款，ali-支付宝，wechat-支付宝
    private String bankId = "";
    private int chaiNum = ONE;//0,不支持   1,支持
    private String sellAmount = "";//出售金额
    private String userAmount = "";//余额

    @Override
    public int getViewId() {
        return R.layout.activity_sellcoincenter;
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
        initView();
        getUserInfo();
        geBankListInfo();
        initRecycleView();
        initLintener();
    }

    private void initView() {
        title = MyApp.getLanguageString(this, R.string.sell_center_title);
        commTitle.init(title);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        ((RadioButton) rgChai.getChildAt(ZERO)).setChecked(true);
    }

    private void initRecycleView() {
        recPayType.closeDefaultAnimator();
        recPayType.setNestedScrollingEnabled(false);
        recPayType.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recPayType.setLayoutManager(new GridLayoutManager(this, 2));
        mySellPaytypeadapter = new MySellPayTypeAdapter(this, payTypeList, PayTypeListener);
        recPayType.setAdapter(mySellPaytypeadapter);
    }

    /**
     * 获取个人信息
     */
    private void getUserInfo() {
        new UserImpl(new ZZNetCallBack<AmountInfo>(this, AmountInfo.class) {
            @Override
            public void onSuccess(AmountInfo response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    userAmount = Config.subOneAndDot(response.getCanSellAmount());
                    tvUserAmount.setText(userAmount);
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
        }.setNeedDialog(true)).getAmountInfo();
    }

    private void geBankListInfo() {
        new UserImpl(new ZZNetCallBack<List<WalletManageBean>>(this, WalletManageBean.class) {
            @Override
            public void onSuccess(List<WalletManageBean> response) {
                if (response != null) {
                    String bankListString = new Gson().toJson(response);
                    SPUtil.setStringValue(SPUtil.PAYMENT_MODE_LIST, bankListString);
                    initPayTypeList(response);
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        }.setNeedDialog(false)).getPayModeList();
    }


    private void initPayTypeList(List<WalletManageBean> response) {
        if (payTypeList.size() > ZERO) {
            payTypeList.clear();
        }
        boolean wechat_bind = false;
        boolean ali_bind = false;
        boolean bank_bind = false;
        for (WalletManageBean w : response) {
            switch (w.getType()) {
                case 0: {
                    wechat_bind = true;
                    PayTypeBean b = new PayTypeBean(true, StringConstants.PAYTYPE_WECHAT, MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.Wechat_name));
                    b.setChannelIcon(w.getIcon());
                    payTypeList.add(b);
                    break;
                }
                case 1: {
                    ali_bind = true;
                    PayTypeBean b = new PayTypeBean(true, StringConstants.PAYTYPE_ALI, MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.Alipay_name));
                    b.setChannelIcon(w.getIcon());
                    payTypeList.add(b);
                    break;
                }
                case 2: {
                    bank_bind = true;
//                    payTypeList.add(new PayTypeBean(bank, StringConstants.PAYTYPE_BANK, MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.Bank_name)));
                    PayTypeBean b = new PayTypeBean(true, StringConstants.PAYTYPE_BANK, w.getBankName());
                    b.setMemberBankId(w.getId());
                    b.setChannelIcon(w.getIcon());
                    payTypeList.add(b);
                    break;
                }
            }
        }
//        if (!bank) {
//            payTypeList.add(new PayTypeBean(bank, StringConstants.PAYTYPE_BANK, MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.Bank_name)));
//        }
        if (null != mySellPaytypeadapter) {
            mySellPaytypeadapter.changeDataUi(payTypeList);
        }
        if (!wechat_bind && !ali_bind && !bank_bind) {
            checkPayDialog();
        }
    }

    private void checkPayDialog() {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.AddPayTypeDialog,
                new AuthenDialogData(v -> {
                    startClass(MyApp.getLanguageString(getContext(), R.string.WalletManageActivity));
                    SellCoinCenterActivity.this.finish();
                }, v -> {
                    toast(MyApp.getLanguageString(getContext(), R.string.add_pay_dialog_title_tip));
                }));
    }

    PayTypeBean lastSelectBankBean = null;
    /**
     * 取款方式选择
     */
    private OnItemClickListener PayTypeListener = (view, position) -> {
        type = "";
        StringBuilder str = new StringBuilder();
        if (CommonUtils.ListNotNull(payTypeList)) {
            PayTypeBean payTypeBean = payTypeList.get(position);
            if (payTypeBean != null) {
                boolean select = payTypeBean.isSelect();
                payTypeBean.setSelect(!select);
                //如果选择了银行
                if (StringConstants.PAYTYPE_BANK.equals(payTypeBean.getChannelType())) {
                    if (payTypeBean.isSelect()) {
                        bankId = payTypeBean.getMemberBankId();
                        //则 取消上次选择的银行
                        if (lastSelectBankBean != null && !lastSelectBankBean.getMemberBankId().equals(payTypeBean.getMemberBankId())) {
                            lastSelectBankBean.setSelect(false);
                        }
                        lastSelectBankBean = payTypeBean;
                    } else {
                        bankId = "";
                    }
                }
            }
            for (int i = 0; i < payTypeList.size(); i++) {
                PayTypeBean paytypebean = payTypeList.get(i);
                if (paytypebean != null && paytypebean.isSelect()) {
                    str.append(paytypebean.getChannelType()).append(StringConstants.comma);
                }
            }
        }
        String string = str.toString();
        int i = string.lastIndexOf(StringConstants.comma);
        if (i > ZERO) {
            string = string.substring(ZERO, string.length() - ONE);
        }
        type = string;
        if (null != mySellPaytypeadapter) {
            mySellPaytypeadapter.changeDataUi(payTypeList);
        }
        checkData();
    };

    private void initLintener() {
        rgChai.setOnCheckedChangeListener((group, checkedId) -> {
            int i = rgChai.indexOfChild(group.findViewById(checkedId));
            chaiNum = i == ZERO ? ONE : ZERO;
        });

        etSellNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = etSellNum.getText();
                if (null != text) {
                    String trim = text.toString().trim();
                    if (CommonUtils.StringNotNull(trim)) {
                        try {
                            double sell = Double.parseDouble(trim);
                            double amount = Math.floor(Double.parseDouble(userAmount));
                            if (sell > amount) {
                                String str = Config.getInteger(amount);
                                etSellNum.setText(str);
                                etSellNum.setSelection(str.length());//将光标移至文字末尾
                                sellAmount = str;
                            } else {
                                sellAmount = trim;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            sellAmount = "";
                        }
                    } else {
                        sellAmount = "";
                    }
                } else {
                    sellAmount = "";
                }
                checkData();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int len = s.toString().length();
                if (len > ONE && text.startsWith(ZERO_STRING)) {
                    s.replace(ZERO, ONE, "");
                }
            }
        });

        etSellNum.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Config.hideSoftKeyboard(SellCoinCenterActivity.this);
                return true;
            }
            return false;
        });
    }

    private void checkData() {
        boolean isNotEmpty = CommonUtils.StringNotNull(sellAmount);
        if (isNotEmpty) {
            tvHint.setVisibility(View.GONE);
            slEtmoneyParent.setSelected(true);
        } else {
            tvHint.setVisibility(View.VISIBLE);
            slEtmoneyParent.setSelected(false);
        }

        double amount = ZERO;
        if (isNotEmpty) {
            try {
                amount = Double.parseDouble(sellAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (amount > ZERO) {
            slSell.setClickable(CommonUtils.StringNotNull(type));
        } else {
            slSell.setClickable(false);
        }
    }

    @OnClick({R.id.sl_sell})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sl_sell:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                try {
                    double sell = Double.parseDouble(sellAmount);
                    if (sell < Config.TEN) {
                        toast(MyApp.getLanguageString(this, R.string.sell_money_cannot_less_ten_tip));
                    } else {
                        getOrderNo(sell);
                    }
                } catch (Exception e) {
                }
                break;
            case R.id.retry_button:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getUserInfo();
                geBankListInfo();
                break;
        }
    }

    /**
     * 获取下注订单号/金额
     */
    private void getOrderNo(double sell) {
        String sellDialogTipTitle = MyApp.getLanguageString(this, R.string.sell_dialog_tip_title);
        String content;
        String sellOrderNotice = SPUtil.getSellOrderNotice();
        if (CommonUtils.StringNotNull(sellOrderNotice)) {
            content = sellOrderNotice;
        } else {
            String sellDialogTipOne = MyApp.getLanguageString(this, R.string.sell_dialog_tip_one);
            String sellDialogTipTwo = MyApp.getLanguageString(this, R.string.sell_dialog_tip_two);
            String sellDialogTipThr = MyApp.getLanguageString(this, R.string.sell_dialog_tip_thr);
            String sellDialogTipFour = MyApp.getLanguageString(this, R.string.sell_dialog_tip_four);
            content = sellDialogTipOne + "\n\n" + sellDialogTipTwo + "\n\n" + sellDialogTipThr + "\n\n" + sellDialogTipFour;
        }
        new FundImpl(new ZZNetCallBack<OrderFromBean>(this, OrderFromBean.class) {
            @Override
            public void onSuccess(OrderFromBean response) {
                if (null != response) {
                    String amount = response.getAmount();
                    String orderNo = response.getOrderNo();
                    if (CommonUtils.StringNotNull(amount) && sell > ZERO) {
                        try {
                            double intAmount = Double.parseDouble(amount);
                            if (intAmount >= sell) {
                                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
                                        MyDialogManager.CoinNoticeDialog,
                                        new CoinNoticeBean(sellDialogTipTitle, content, dialog -> {
                                            MyDialogManager.getManager().disMissCoinNoticeDialog();
                                            getOrderSell(orderNo);

                                        }));
                            } else {
                                sellAmount = "";
                                etSellNum.setText("");
                                SellCoinCenterActivity.this.toast(MyApp.getLanguageString(getContext(), R.string.Money_Error_reEnter));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).getOrderFrom();
    }

    /**
     * 卖币
     */
    private void getOrderSell(String orderno) {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.sell_center_success_tip));
                EventBusUtil.post(new OrderStatusNoticeBean());
                String languageString = MyApp.getLanguageString(SellCoinCenterActivity.this, R.string.sell_finish_details_money_two_tip);
                HashMap<String, String> params = new HashMap<>();
                params.put(StringConstants.TITLE, languageString);
                params.put(StringConstants.TITLE_SECOND, "");
                params.put(StringConstants.ORDERNO, orderno);
                SellCoinCenterActivity.this.startClass(R.string.SellCoinDetailsActivity, params);
                SellCoinCenterActivity.this.finish();
            }

        }.setNeedDialog(true).setNeedToast(true)).getOrderSell(orderno, sellAmount, type, String.valueOf(chaiNum), bankId);
    }
}
