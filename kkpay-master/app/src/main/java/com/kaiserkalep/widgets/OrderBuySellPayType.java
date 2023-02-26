package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PhotoViewPopuWindow;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ViewBase;
import com.kaiserkalep.bean.OrderDetailsBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.ui.activity.OrderDetailsActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.glide.GlideUtil;

import butterknife.BindView;

/**
 * 付款信息
 */
public class OrderBuySellPayType extends ViewBase {

    @BindView(R.id.iv_pay_photo)
    ImageView ivPayPhoto;
    @BindView(R.id.tv_pay_title)
    TextView tvPayTitle;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;

    @BindView(R.id.ll_one_parent)
    LinearLayout llOneParent;
    @BindView(R.id.tv_one_tip)
    TextView tvOneTip;
    @BindView(R.id.tv_one_text)
    TextView tvOneText;

    @BindView(R.id.ll_two_parent)
    LinearLayout llTwoParent;
    @BindView(R.id.tv_two_tip)
    TextView tvTwoTip;
    @BindView(R.id.tv_two_text)
    TextView tvTwoText;
    @BindView(R.id.iv_two_copy)
    ImageView ivTwoCopy;

    @BindView(R.id.ll_thr_parent)
    LinearLayout llThrParent;
    @BindView(R.id.tv_thr_tip)
    TextView tvThrTip;
    @BindView(R.id.tv_thr_text)
    TextView tvThrText;
    @BindView(R.id.iv_thr_copy)
    ImageView ivThrCopy;

    @BindView(R.id.ll_four_parent)
    LinearLayout llFourParent;
    @BindView(R.id.tv_four_tip)
    TextView tvFourTip;
    @BindView(R.id.tv_four_text)
    TextView tvFourText;
    @BindView(R.id.iv_four_copy)
    ImageView ivFourCopy;

    @BindView(R.id.coverIcon_IM)
    ImageView coverIcon_IM;

    private OrderDetailsBean.PayInfoDTO data;
    private PhotoViewPopuWindow photoviewpopuwindow;
    private String payType;

    public OrderBuySellPayType(Context context) {
        super(context);
    }

    public OrderBuySellPayType(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderBuySellPayType(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getViewId() {
        return R.layout.view_order_buysell_paytype;
    }

    @Override
    public void afterViews(View v) {
        initListener();
    }

    private void initListener() {
        ivTwoCopy.setOnClickListener(v -> {
            if (null != tvTwoText) {
                String string = tvTwoText.getText().toString();
//                if (CommonUtils.StringNotNull(string) && CommonUtils.setSystemCopyText(getContext(), string)) {
//                    toast(MyApp.getLanguageString(getContext(), R.string.System_copytext_Success));
//                }
                CommonUtils.copyText(getContext(), string);
            }
        });

        ivThrCopy.setOnClickListener(v -> {
            if (null != ivThrCopy) {
                String string = tvThrText.getText().toString();
//                if (CommonUtils.StringNotNull(string) && CommonUtils.setSystemCopyText(getContext(), string)) {
//                    toast(MyApp.getLanguageString(getContext(), R.string.System_copytext_Success));
//                }
                CommonUtils.copyText(getContext(), string);
            }
        });

        ivFourCopy.setOnClickListener(v -> {
            if (null != ivFourCopy) {
                String string = tvFourText.getText().toString();
//                if (CommonUtils.StringNotNull(string) && CommonUtils.setSystemCopyText(getContext(), string)) {
//                    toast(MyApp.getLanguageString(getContext(), R.string.System_copytext_Success));
//                }
                CommonUtils.copyText(getContext(), string);
            }
        });

        ivPayPhoto.setOnClickListener(v -> {
            if (null != data && !isNeedHide()) {
                if (!StringConstants.PAYTYPE_BANK.equals(payType)) {
                    showPhotoViewDialog(BaseNetUtil.jointUrl(data.getQrcode()));
                }
            }
        });
    }

    String status = "";
    boolean isBuyer = false;//是否是买家

    //状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)
    public void setPayStatus(String status, boolean isBuyer) {
        this.status = status;
        this.isBuyer = isBuyer;
    }

    boolean isNeedHide() {
        return Config.FOUR_STRING.equals(status) || (Config.TWO_STRING.equals(status) && isBuyer);
    }


    public void setViewdata(Context context, String payType, OrderDetailsBean.PayInfoDTO data) {
        this.data = data;
        this.payType = payType;
        if (null != data && null != context) {
            String buyPZ = MyApp.getLanguageString(context, R.string.Order_detail_buyjia_pingzeng_tip);
            String sellPZ = MyApp.getLanguageString(context, R.string.Order_detail_selljia_pingzeng_tip);
            String sellTitle = MyApp.getLanguageString(context, R.string.Order_detail_selljia_collenction_tip);
            String buyTitle = MyApp.getLanguageString(context, R.string.Order_detail_buyjia_collenction_tip);
            tvPayTitle.setText(data.isSell() ? sellTitle : buyTitle);
            switch (payType) {
                case StringConstants.PAYTYPE_WECHAT:
                    GlideUtil.loadNoPlaceImage(BaseNetUtil.jointUrl(data.getQrcode()), ivPayPhoto, R.drawable.icon_kkpay_logo);
                    GlideUtil.loadImage(R.drawable.icon_wechat_logo, ivIcon);
                    llFourParent.setVisibility(GONE);
                    setShowView(ivTwoCopy, llOneParent, llTwoParent, llThrParent);
                    setHideView(ivThrCopy, ivFourCopy);
                    tvOneTip.setText(MyApp.getLanguageString(context, R.string.Share_Nickname));
                    tvTwoTip.setText(MyApp.getLanguageString(context, R.string.Share_name));
                    tvThrTip.setText(MyApp.getLanguageString(context, R.string.Share_Pay_Qrcode));
                    tvOneText.setText(data.getUsername());
                    tvTwoText.setText(handleText(data.getRealName(), 1));
                    tvThrText.setText(data.isSell() ? sellPZ : buyPZ);
                    break;
                case StringConstants.PAYTYPE_ALI:
                    GlideUtil.loadNoPlaceImage(BaseNetUtil.jointUrl(data.getQrcode()), ivPayPhoto, R.drawable.icon_kkpay_logo);
                    GlideUtil.loadImage(R.drawable.icon_aliba_logo, ivIcon);
                    setShowView(ivTwoCopy, ivThrCopy, llFourParent, llOneParent, llTwoParent, llThrParent);
                    setHideView(ivFourCopy);
                    tvOneTip.setText(MyApp.getLanguageString(context, R.string.Share_Nickname));
                    tvTwoTip.setText(MyApp.getLanguageString(context, R.string.Share_Account));
                    tvThrTip.setText(MyApp.getLanguageString(context, R.string.Share_name));
                    tvFourTip.setText(MyApp.getLanguageString(context, R.string.Share_Pay_Qrcode));
                    tvOneText.setText(data.getUsername());
                    tvTwoText.setText(handleText2(data.getAccount(), 4));
                    tvThrText.setText(handleText(data.getRealName(), 1));
                    tvFourText.setText(data.isSell() ? sellPZ : buyPZ);
                    break;
                case StringConstants.PAYTYPE_BANK:
                    GlideUtil.loadNoPlaceImage(R.drawable.icon_paybank_nonormal_logo, ivPayPhoto, R.drawable.icon_kkpay_logo);
                    GlideUtil.loadImage(R.drawable.icon_bank_logo, ivIcon);
                    setShowView(ivTwoCopy, ivFourCopy, llFourParent, llOneParent, llTwoParent, llThrParent);
                    setHideView(ivThrCopy);
                    tvOneTip.setText(MyApp.getLanguageString(context, R.string.Share_Nickname));
                    tvTwoTip.setText(MyApp.getLanguageString(context, R.string.Share_name));
                    tvThrTip.setText(MyApp.getLanguageString(context, R.string.Share_bank));
                    tvFourTip.setText(MyApp.getLanguageString(context, R.string.Share_bank_no));
                    tvOneText.setText(data.getUsername());
                    tvTwoText.setText(handleText(data.getRealName(), 1));
                    tvThrText.setText(data.getBank());
                    tvFourText.setText(handleText(data.getAccount(), 4));
                    break;
            }
            coverIcon_IM.setVisibility(GONE);
            ivPayPhoto.setVisibility(VISIBLE);
            //如果是完成的订单
            if (isNeedHide()) {
                //隐藏所有复制按钮
                ivTwoCopy.setVisibility(GONE);
                ivThrCopy.setVisibility(GONE);
                ivFourCopy.setVisibility(GONE);
                if (StringConstants.PAYTYPE_WECHAT.equals(payType)
                        || StringConstants.PAYTYPE_ALI.equals(payType)
                ) {
                    coverIcon_IM.setVisibility(VISIBLE);
                    ivPayPhoto.setVisibility(GONE);
                }
            }
        }
    }

    String starWord = "*";

    //leftNum 保留最后的几位
    private String handleText(String name, int leftNum) {
        if (isNeedHide()) {
            String lastName = "";
            if (name.length() > leftNum) {
                lastName = name.substring(name.length() - leftNum);
                String newName = "";
                for (int i = 0; i < name.length() - leftNum; i++) {
                    newName = newName + starWord;
                }
                newName = newName + lastName;
                return newName;
            }
        }
        return name;
    }

    //leftNum 隐藏最前面的几位
    private String handleText2(String name, int leftNum) {
        if (isNeedHide()) {
            String lastName = "";
            if (name.length() > leftNum) {
                lastName = name.substring(leftNum);
                String newName = "";
                for (int i = 0; i < leftNum; i++) {
                    newName = newName + starWord;
                }
                newName = newName + lastName;
                return newName;
            } else {
                String newName = "";
                for (int i = 0; i < name.length(); i++) {
                    newName = newName + starWord;
                }
                return newName;
            }
        }
        return name;
    }

    private void setShowView(View... view) {
        for (int i = 0; i < view.length; i++) {
            View showView = view[i];
            if (null != showView) {
                showView.setVisibility(VISIBLE);
            }
        }
    }

    private void setHideView(View... view) {
        for (int i = 0; i < view.length; i++) {
            View showView = view[i];
            if (null != showView) {
                showView.setVisibility(GONE);
            }
        }
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
        photoviewpopuwindow.showImage(false, imgUrl);
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


