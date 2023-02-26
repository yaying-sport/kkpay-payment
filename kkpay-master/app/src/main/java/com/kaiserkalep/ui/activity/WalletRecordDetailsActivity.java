package com.kaiserkalep.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 钱包记录详情
 */
public class WalletRecordDetailsActivity extends ZZActivity {

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

    @BindView(R.id.tv_statue_title)
    TextView tvStatueTitle;
    @BindView(R.id.tv_isCanSell)
    TextView tvIsCanSell;
    @BindView(R.id.tv_trade_money)
    TextView tvTradeMoney;
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.tv_orderno)
    TextView tvOrderno;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.merchantName_RL)
    RelativeLayout merchantName_RL;
    @BindView(R.id.merchantName_TV)
    TextView merchantName_TV;


    private String title = "";
    private String walletTitle = "";
    private String money;
    private String amount;
    private String billNo;
    private String billType;
    private String amountType;
    private String createTime;
    private String tradeType;
    private String agentName;

    @Override
    public int getViewId() {
        return R.layout.activity_orderdetails;
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
        title = MyApp.getLanguageString(getContext(), R.string.wallet_record_details_title);
        ivTopTitle.setText(title);
        try {
            HashMap<String, String> urlParam = getUrlParam();
            if (null != urlParam && urlParam.size() > ZERO) {
                walletTitle = urlParam.get(StringConstants.TITLE);
                money = urlParam.get(StringConstants.MONEY);
                amount = urlParam.get(StringConstants.AMOUNT);
                billNo = urlParam.get(StringConstants.BILLNO);
                billType = urlParam.get(StringConstants.BILLTYPE);
                amountType = urlParam.get(StringConstants.AMOUNTTYPE);
                createTime = urlParam.get(StringConstants.CREATETIME);
                tradeType = urlParam.get(StringConstants.TRADETYPE);
                agentName = urlParam.get(StringConstants.AGENTUSERNAME);
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

        tvStatueTitle.setText(walletTitle);
        if (ZERO_STRING.equals(amountType)) {
            tvTradeMoney.setTextColor(getResources().getColor(R.color.wallet_record_out_text_color));
            tvTradeMoney.setText("-" + Config.subOneAndDot(money));
        } else if (ONE_STRING.equals(amountType)) {
            tvTradeMoney.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvTradeMoney.setText("+" + Config.subOneAndDot(money));
        } else {
            switch (billType) {
                case Config.ZERO_STRING://买币
                case Config.THREE_STRING://从商户提现加币
                case Config.FIVE_STRING://手动充币
                    tvTradeMoney.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvTradeMoney.setText("+" + Config.subOneAndDot(money));
                    break;
                case Config.ONE_STRING://卖币
                case Config.TWO_STRING://向商户充值扣币
                case Config.SIX_STRING://手动扣币
                    tvTradeMoney.setTextColor(getResources().getColor(R.color.wallet_record_out_text_color));
                    tvTradeMoney.setText("-" + Config.subOneAndDot(money));
                    break;
            }
        }
        if (ONE_STRING.equals(tradeType)) {
            tvIsCanSell.setText(MyApp.getLanguageString(this, R.string.wallet_not_sell_money_tip));
            tvIsCanSell.setTextColor(getResources().getColor(R.color.wallet_detauked_color));
        } else if (TWO_STRING.equals(tradeType)) {
            tvIsCanSell.setText(MyApp.getLanguageString(this, R.string.wallet_can_sell_money_tip));
            tvIsCanSell.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvIsCanSell.setText("");
            tvIsCanSell.setTextColor(getResources().getColor(R.color.white));
        }
        if (!TextUtils.isEmpty(agentName)) {
            merchantName_RL.setVisibility(View.VISIBLE);
            merchantName_TV.setText(agentName);
        } else {
            merchantName_RL.setVisibility(View.GONE);
        }
        tvYue.setText(Config.subOneAndDot(amount));
        tvOrderno.setText(billNo);
        tvTime.setText(createTime);
    }

    @OnClick({R.id.iv_back, R.id.ll_top_right, R.id.iv_orderno_copy_icon})
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
            case R.id.iv_orderno_copy_icon:
                if (null != tvOrderno) {
                    String string = tvOrderno.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
        }
    }
}
