package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.animation.content.Content;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ViewBase;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.ui.activity.MainActivity;


import butterknife.BindView;

/**
 * 通过订单状态，显示对应操作页面
 * <p>
 * 状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消
 */
public class OrderStatusView extends ViewBase implements View.OnClickListener {

    //买家上传凭证
    @BindView(R.id.ll_maijia_pingzheng)
    LinearLayout llMaijia_Pingzheng;
    //    @BindView(R.id.tv_buypayorder_tip)
//    TextView tvBuypPayOrderTip;
//    @BindView(R.id.btn_up_pay_pingzheng)
//    Button uploadPayinfo_TV;
    @BindView(R.id.uploadPayinfo_LL)
    LinearLayout uploadPayinfo_LL;
    @BindView(R.id.uploadPayinfo_TV)
    TextView uploadPayinfo_TV;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_image)
    Button btnImage;

    @BindView(R.id.btn_pay_yizhuanzhang)
    Button btnPay_Yizhuanzhang;

    //取消交易/确认交易
    @BindView(R.id.ll_cancel_ok_order)
    LinearLayout llCancelOk_Order;
    //    @BindView(R.id.tv_orderStart_tip)
//    TextView tvOrderStartTip;
    @BindView(R.id.btn_cancel_order)
    Button btnCancel_Order;
    @BindView(R.id.btn_ok_order)
    Button btnOk_Order;

    //查看凭证/输入密码/确定打币/暂停打币
    @BindView(R.id.ll_pause_or_topay)
    LinearLayout llPauseOrTopay;
    @BindView(R.id.btn_check_pingzheng)
    Button btnCheck_Pingzheng;
    @BindView(R.id.btn_pasue_coin)
    Button btnPasue_Coin;
    @BindView(R.id.btn_certain_coin)
    Button btnCertain_Coin;
    private SucceedCallBackListener<Integer> succeedCallBackListener;


    public OrderStatusView(Context context) {
        super(context);
    }

    public OrderStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getViewId() {
        return R.layout.view_order_status_layout;
    }

    @Override
    public void afterViews(View v) {
        llMaijia_Pingzheng.setVisibility(GONE);
        llCancelOk_Order.setVisibility(GONE);
        llPauseOrTopay.setVisibility(GONE);

        initListener();
    }

    public void addOnClickListener(SucceedCallBackListener<Integer> succeedCallBackListener) {
        this.succeedCallBackListener = succeedCallBackListener;
    }

    private void initListener() {
//        uploadPayinfo_TV.setOnClickListener(this);
        uploadPayinfo_LL.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnPay_Yizhuanzhang.setOnClickListener(this);

        btnCancel_Order.setOnClickListener(this);
        btnOk_Order.setOnClickListener(this);

        btnCheck_Pingzheng.setOnClickListener(this);
        btnPasue_Coin.setOnClickListener(this);
        btnCertain_Coin.setOnClickListener(this);
    }

    /**
     * (当前状态，页面展示下一个状态)
     * <p>
     * 状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消
     * (-1取消状态，上层已经拦截)
     *
     * @param status
     */
    public void setStatus(Context context, String status, boolean isMe, String time) {
        if (isMe) {
            llMaijia_Pingzheng.setVisibility(GONE);
            switch (status) {
                case Config.ZERO_STRING:
//                    tvOrderStartTip.setText(String.format(MyApp.getLanguageString(context, R.string.Order_detail_selljia_order_tip), time));
                    llCancelOk_Order.setVisibility(VISIBLE);
                    llPauseOrTopay.setVisibility(GONE);
                    break;
                case Config.ONE_STRING:
                case Config.THREE_STRING:
                case Config.FOUR_STRING:
                    llCancelOk_Order.setVisibility(GONE);
                    llPauseOrTopay.setVisibility(GONE);
                    break;
                case Config.TWO_STRING:
                    llCancelOk_Order.setVisibility(GONE);
                    llPauseOrTopay.setVisibility(VISIBLE);
                    break;
            }
        } else {
            llCancelOk_Order.setVisibility(GONE);
            llPauseOrTopay.setVisibility(GONE);
            switch (status) {
                case Config.ZERO_STRING:
                case Config.TWO_STRING:
                case Config.THREE_STRING:
                case Config.FOUR_STRING:
                    llMaijia_Pingzheng.setVisibility(GONE);
                    break;
                case Config.ONE_STRING:
                    uploadPayinfo_TV.setText(MyApp.getLanguageString(getContext(), R.string.Order_detail_buyjia_upload_payzm_tip));
                    btnCancel.setVisibility(VISIBLE);
                    btnImage.setVisibility(GONE);
                    llMaijia_Pingzheng.setVisibility(VISIBLE);
//                    tvBuypPayOrderTip.setText(String.format(MyApp.getLanguageString(context, R.string.Order_detail_buyjia_payMoney_tip), time));
                    break;
            }
        }

    }

    /**
     * 买家上传支付凭证成功
     */
    public void setUploadPzSuccess() {
        if (null != uploadPayinfo_TV) {
            uploadPayinfo_TV.setText(MyApp.getLanguageString(getContext(), R.string.Order_detail_buyjia_upload_payzm_yzz_tip));
        }
        if (null != btnCancel) {
            btnCancel.setVisibility(GONE);
        }
        if (null != btnImage) {
            btnImage.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (null != succeedCallBackListener) {
            succeedCallBackListener.succeedCallBack(v.getId());
        }
    }
}
