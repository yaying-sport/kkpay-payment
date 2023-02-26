package com.kaiserkalep.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AuthenDialogData;
import com.kaiserkalep.bean.CommDialogData;
import com.kaiserkalep.bean.FilePathBean;
import com.kaiserkalep.bean.MyOrderBean;
import com.kaiserkalep.bean.OrderStatusNoticeBean;
import com.kaiserkalep.bean.OrderDetailsBean;
import com.kaiserkalep.bean.PayPassWordDialogData;
import com.kaiserkalep.bean.SimpleDialogData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.service.KKpayBackgroundService;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.PickImageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.OrderBuySellPayType;
import com.kaiserkalep.widgets.OrderProgressStatus;
import com.kaiserkalep.widgets.OrderStatusView;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.base.BaseNetUtil.singleTime;
import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.NINE;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.OWN_ONE_STRING;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.SIXTEEN;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 订单详情
 * 通过正常Intent启动
 */
public class OrderDetailsActivity extends ZZActivity implements View.OnClickListener {

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.sl_order_content)
    ShadowLayout slOrderContent;
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
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_orderno)
    TextView tvOrderno;

    @BindView(R.id.ll_call_other)
    LinearLayout llCallOther;
    @BindView(R.id.ll_ordering_details)
    LinearLayout llOrderingDetails;
    @BindView(R.id.ops_progress)
    OrderProgressStatus opsProgress;
    @BindView(R.id.osv_Status)
    OrderStatusView osvStatus;
    @BindView(R.id.obspt_frist)
    OrderBuySellPayType obsptFrist;
    @BindView(R.id.obspt_second)
    OrderBuySellPayType obsptSecond;

    //订单已经取消
    @BindView(R.id.ll_order_cancel)
    LinearLayout llOrderCancel;
    @BindView(R.id.tv_order_create_time)
    TextView tvOrderCreateTime;
    @BindView(R.id.tv_order_cancel_time)
    TextView tvOrderCancelTime;

    @BindView(R.id.monetCopy_IM)
    ImageView monetCopy_IM;

    @BindView(R.id.orderTip_TV)
    TextView orderTip_TV;

    private String title = "";
    //    private String payzm_yzz_tip = "";
    private String orderNo = "";
    private String payProof = "";//买家付款凭证
    List<LocalMedia> selectionData = new ArrayList<>();
    private OrderDetailsBean orderdetailsbean;

    @Override
    public int getViewId() {
        return R.layout.activity_wallet_record_details;
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
        Intent intent = getIntent();
        if (null != intent) {
            orderNo = intent.getStringExtra(StringConstants.ORDERNO);
        }
        title = MyApp.getLanguageString(getContext(), R.string.order_details_title);
//        payzm_yzz_tip = MyApp.getLanguageString(this, R.string.Order_detail_buyjia_upload_payzm_yzz_tip);
        ivTopTitle.setText(title);
        ivBack.setImageResource(R.drawable.icon_top_back_white);
        ivTopRightImg.setBackgroundResource(R.drawable.icon_home_msg);
        tvTopRightText.setText(MyApp.getLanguageString(this, R.string.trade_dispute));
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setMargins2(slOrderContent, 0, UIUtils.dip2px(50) + statusBarHeight, 0, 0);
        UIUtils.setHeight(tvStatusbar, statusBarHeight);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        refreshLayout.setOnRefreshListener(refreshLayout -> getOrderBuyDetail(false));

        getOrderBuyDetail(true);
        initListener();
    }

    private void initListener() {
        osvStatus.addOnClickListener(o -> {
            if (null != o) {
                switch (o) {
                    case R.id.uploadPayinfo_LL://买家上传支付凭证
                        if (JudgeDoubleUtils.isDoubleClick()) {
                            return;
                        }
                        //最多上传3张
//                        int maxPhotSize = 3;
//                        int havePhotoNum = 0;
//                        String[] payProofList = payProof.split(StringConstants.comma);
//                        for (String p : payProofList) {
//                            if (!TextUtils.isEmpty(p)) {
//                                havePhotoNum++;
//                            }
//                        }
//                        openImageDialog(maxPhotSize - havePhotoNum);
                        PickImageUtils.openGallery(OrderDetailsActivity.this,
                                2,
                                selectionData,
                                this::selectPhoto);
                        break;
                    case R.id.btn_pay_yizhuanzhang://请点击,我已经转账
                        if (CommonUtils.StringNotNull(payProof)) {
                            changeOrder("", TWO_STRING, payProof);
                        } else {
                            toast(MyApp.getLanguageString(getContext(), R.string.please_buyjia_upload_pay_pingzeng_tip));
                        }
                        break;
                    case R.id.btn_cancel://取消购买
                    case R.id.btn_cancel_order://取消交易
                        if (JudgeDoubleUtils.isDoubleClick()) {
                            return;
                        }
                        showCoinCancelDialog();
                        break;
                    case R.id.btn_ok_order://确认交易
                        if (JudgeDoubleUtils.isDoubleClick()) {
                            return;
                        }
                        changeOrder("", ONE_STRING, "");
                        break;
                    case R.id.btn_image://买家点击查看支付凭证
                        if (CommonUtils.StringNotNull(payProof)) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(StringConstants.TITLE, MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Order_detail_photo_detail_title));
                            params.put(StringConstants.DATA, payProof);
                            startClass(R.string.PhotoDetailActivity, params);
                        }
                        break;
                    case R.id.btn_check_pingzheng://卖家点击查看支付凭证
                        if (null != orderdetailsbean && CommonUtils.StringNotNull(orderdetailsbean.getPayProof())) {
                            String payproof = orderdetailsbean.getPayProof();
                            HashMap<String, String> params = new HashMap<>();
                            params.put(StringConstants.TITLE, MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Order_detail_photo_detail_title));
                            params.put(StringConstants.DATA, payproof);
                            startClass(R.string.PhotoDetailActivity, params);
                        }
                        break;
                    case R.id.btn_pasue_coin://暂停打币
                        if (JudgeDoubleUtils.isDoubleClick()) {
                            return;
                        }
                        showCoinDialog();
                        break;
                    case R.id.btn_certain_coin://确认打币
                        if (JudgeDoubleUtils.isDoubleClick()) {
                            return;
                        }
                        showPassDialog();
                        break;
                }
            }
        });
    }

    /**
     * 暂停打币
     */
    private void showCoinDialog() {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CoinConfirmPauseDialog,
                new AuthenDialogData(v -> {
                    changeOrder("", THREE_STRING, "");
                }, null));
    }

    /**
     * 卖家取消订单
     * 卖家取消交易
     */
    private void showCoinCancelDialog() {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.CoinCancelDialog,
                new AuthenDialogData(v -> {
                    changeOrder("", OWN_ONE_STRING, "");
                }, null));
    }

    /**
     * 支付密码
     */
    private void showPassDialog() {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.InputPassWordDialog,
                new PayPassWordDialogData(o -> {
                    if (null != o && CommonUtils.StringNotNull(o) && o.length() == SIX) {
                        changeOrder(o, FOUR_STRING, "");
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.please_input_passw_finish_tip));
                    }
                }, null));
    }


    /**
     * 改变订单状态
     *
     * @param pass   密码
     * @param status 要改成成的状态
     * @param proof  支付凭证
     */
    public void changeOrder(String pass, String status, String proof) {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Share_Operate_Success));
                if (OWN_ONE_STRING.equals(status) || THREE_STRING.equals(status) || FOUR_STRING.equals(status)) {//取消订单/暂停打币/确认打币
                    EventBusUtil.post(new OrderStatusNoticeBean());
                }
                getOrderBuyDetail(true);
            }
        }).setChangeStatus(orderNo, StringConstants.BUY, status, proof, pass);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            String sExtra = intent.getStringExtra(StringConstants.ORDERNO);
            if (CommonUtils.StringNotNull(sExtra)) {
                orderNo = "";
                payProof = "";
                selectionData.clear();
                orderdetailsbean = null;
                orderNo = sExtra;
                obsptFrist.setVisibility(View.GONE);
                obsptSecond.setVisibility(View.GONE);
                getOrderBuyDetail(true);
            }
        }
    }

    /**
     * 获取买单详情
     */
    private void getOrderBuyDetail(boolean needDialog) {
        new FundImpl(new ZZNetCallBack<OrderDetailsBean>(this, OrderDetailsBean.class) {
            @Override
            public void onSuccess(OrderDetailsBean response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    orderdetailsbean = response;
                    payProof = "";
                    selectionData.clear();
                    //保存或删除 全局的进行中的订单
                    //状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)
                    if (ZERO_STRING.equals(response.getStatus())
                            || ONE_STRING.equals(response.getStatus())
                            || TWO_STRING.equals(response.getStatus())
                            || THREE_STRING.equals(response.getStatus())) {
                        MyOrderBean.RowsDTO r = new MyOrderBean.RowsDTO();
                        r.setOrderNo(response.getOrderNo());
                        r.setStatus(response.getStatus());
                        MyApp.getApplication().orderMap.put(r.getOrderNo(), r);
                    } else if (FOUR_STRING.equals(response.getStatus())
                            || OWN_ONE_STRING.equals(response.getStatus())) {
                        MyApp.getApplication().orderMap.remove(response.getOrderNo());
                        MyApp.getApplication().checkOrder();
                    }
                    setDate(response);
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

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog)).getOrderBuyDetail(orderNo);
    }

    /**
     * 填充数据
     *
     * @param response
     */
    @SuppressLint("SetTextI18n")
    private void setDate(OrderDetailsBean response) {
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(response.getAvatar()), ivPhoto, R.drawable.icon_default_photo);
        tvNickname.setText(response.getUsername());
        tvMoney.setText(Config.subOneAndDot(response.getAmount()));
        tvOrderno.setText(response.getOrderNo());
        String status = response.getStatus();
        tvOrderState.setText(OrderDetailsBean.getOrderDetailStatusTitle(this, status));
        String memberId = response.getMemberId();
        String createTime = response.getCreateTime();
        String confirmTime = response.getConfirmTime();
        String transferTime = response.getTransferTime();
        String payTime = response.getPayTime();
        String cancelTime = response.getCancelTime();
//        int isBuyer = 0;//1买家 2 卖家
        boolean isBuyer = true;
        if (Config.OWN_ONE_STRING.equals(status)) {
            llOrderCancel.setVisibility(View.VISIBLE);
            llOrderingDetails.setVisibility(View.GONE);
            tvOrderCreateTime.setText(MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Order_detail_createTime_tip) + "：" + createTime);
            tvOrderCancelTime.setText(MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Order_detail_CancelTime_tip) + "：" + cancelTime);
        } else {
            llOrderCancel.setVisibility(View.GONE);
            llOrderingDetails.setVisibility(View.VISIBLE);
            String userId = SPUtil.getUserId();
            String payType = response.getPayType();
            OrderDetailsBean.PayInfoDTO buyUserInfo = response.getBuyUserInfo();
            OrderDetailsBean.PayInfoDTO sellUserInfo = response.getSellUserInfo();
            obsptFrist.setPayStatus(status, userId.equals(buyUserInfo.getMemberId()));
            obsptSecond.setPayStatus(status, userId.equals(buyUserInfo.getMemberId()));
            if (null != buyUserInfo && null != sellUserInfo) {
                obsptFrist.setVisibility(View.VISIBLE);
                obsptSecond.setVisibility(View.VISIBLE);
                if (userId.equals(buyUserInfo.getMemberId())) {
                    obsptFrist.setViewdata(OrderDetailsActivity.this, payType, buyUserInfo.setSell(memberId));
                    obsptSecond.setViewdata(OrderDetailsActivity.this, payType, sellUserInfo.setSell(memberId));
                } else if (userId.equals(sellUserInfo.getMemberId())) {
                    obsptFrist.setViewdata(OrderDetailsActivity.this, payType, sellUserInfo.setSell(memberId));
                    obsptSecond.setViewdata(OrderDetailsActivity.this, payType, buyUserInfo.setSell(memberId));
                } else {
                    obsptFrist.setViewdata(OrderDetailsActivity.this, payType, buyUserInfo.setSell(memberId));
                    obsptSecond.setViewdata(OrderDetailsActivity.this, payType, sellUserInfo.setSell(memberId));
                }
            } else if (null != buyUserInfo) {
                obsptFrist.setVisibility(View.VISIBLE);
                obsptSecond.setVisibility(View.GONE);
                obsptFrist.setViewdata(OrderDetailsActivity.this, payType, buyUserInfo.setSell(memberId));
            } else if (null != sellUserInfo) {
                obsptFrist.setVisibility(View.VISIBLE);
                obsptSecond.setVisibility(View.GONE);
                obsptFrist.setViewdata(OrderDetailsActivity.this, payType, sellUserInfo.setSell(memberId));
            } else {
                obsptFrist.setVisibility(View.GONE);
                obsptSecond.setVisibility(View.GONE);
            }
            setDateTime(ZERO, createTime);
            setDateTime(ONE, confirmTime);
            setDateTime(TWO, transferTime);
            setDateTime(THREE, payTime);
            opsProgress.setOrderProgress(Config.ZERO_STRING.equals(status) ? 0 :
                    Config.ONE_STRING.equals(status) ? 35 :
                            (Config.TWO_STRING.equals(status) || Config.THREE_STRING.equals(status)) ? 65 :
                                    Config.FOUR_STRING.equals(status) ? 100 : 0);
            boolean equals = userId.equals(memberId);
            isBuyer = !userId.equals(memberId);
            if (equals) {
                llCallOther.setVisibility(ONE_STRING.equals(status) ? View.VISIBLE : View.GONE);//自己下的单---1卖家已确定
                monetCopy_IM.setVisibility(View.GONE);
            } else {
                llCallOther.setVisibility((ZERO_STRING.equals(status) || TWO_STRING.equals(status)) ? View.VISIBLE : View.GONE);//别人的订单 -- 0买家下单/2买家转账
                monetCopy_IM.setVisibility(View.VISIBLE);
            }
            osvStatus.setStatus(this, status, equals, response.getTime());
        }

        if (Config.ZERO_STRING.equals(status)
                || Config.ONE_STRING.equals(status)
                || Config.TWO_STRING.equals(status)) {
            orderTip_TV.setVisibility(View.VISIBLE);
        } else {
            orderTip_TV.setVisibility(View.GONE);
        }

        //状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)
        switch (status) {
            case Config.ZERO_STRING:
                if (isBuyer) {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_buyer_order_tip_0), response.getTime()));
                } else {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_seller_order_tip_0), response.getTime()));
                }
                break;
            case Config.ONE_STRING:
                if (isBuyer) {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_buyer_order_tip_1), response.getTime()));
                } else {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_seller_order_tip_1), response.getTime()));
                }
                break;
            case Config.TWO_STRING:
                if (isBuyer) {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_buyer_order_tip_2), response.getTime()));
                } else {
                    orderTip_TV.setText(String.format(MyApp.getLanguageString(getContext(), R.string.Order_detail_seller_order_tip_2), response.getTime()));
                }
                break;
            case Config.THREE_STRING:
            case Config.FOUR_STRING:
            case Config.OWN_ONE_STRING:
                orderTip_TV.setVisibility(View.GONE);
                break;
        }

    }

    private void setDateTime(int index, String string) {
        if (CommonUtils.StringNotNull(string)) {
            int i = string.indexOf(" ");
            if (i > ZERO) {
                String date = string.substring(ZERO, i);
                String time = string.substring(i + ONE, string.length());
                opsProgress.setBottomDateTitle(index, date).setBottomTimeTitle(index, time);
            } else {
                opsProgress.setBottomDateTitle(index, string).setBottomTimeTitle(index, "");
            }
        } else {
            opsProgress.setBottomDateTitle(index, "").setBottomTimeTitle(index, "");
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_top_right, R.id.iv_copy_icon, R.id.ll_call_other, R.id.monetCopy_IM})
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
            case R.id.iv_copy_icon:
                if (null != tvOrderno) {
                    String string = tvOrderno.getText().toString();
                    CommonUtils.copyText(getContext(), string);
                }
                break;
            case R.id.monetCopy_IM:
                String moneyText = tvMoney.getText().toString();
                CommonUtils.copyText(getContext(), moneyText);
                break;
            case R.id.ll_call_other:
                if (JudgeDoubleUtils.isCallOtherClick()) {
                    toast(MyApp.getLanguageString(this, R.string.Share_Operate_Frequently));
                    return;
                }
                setCallOther();
                break;
            case R.id.retry_button:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getOrderBuyDetail(true);
                break;
        }
    }

    private void setCallOther() {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(OrderDetailsActivity.this, R.string.Share_Notified_Success));
            }

        }.setNeedDialog(true).setNeedToast(true)).setAddNoticeToOther(orderNo);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != obsptFrist) {
            obsptFrist.release();
        }

        if (null != obsptSecond) {
            obsptSecond.release();
        }
    }

    /**
     * 支付凭证
     */
    private void openImageDialog(int leftPhotoNum) {
//        int color = getResources().getColor(R.color.face_text_bold_color);
//        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SimpleDialog,
//                new SimpleDialogData(MyApp.getLanguageString(getContext(), R.string.open_camera), color,
//                        MyApp.getLanguageString(getContext(), R.string.open_photo), color,
//                        o -> {
//                            if (ONE_STRING.equals(o)) {
//                                PickImageUtils.openCamera(OrderDetailsActivity.this, false, NINE, SIXTEEN, this::shootPhoto);
//                            } else if (TWO_STRING.equals(o)) {
////                                PickImageUtils.openGallery(OrderDetailsActivity.this, true, false, NINE, SIXTEEN, this::judgeQrPhoto);
//                                PickImageUtils.openGallery(OrderDetailsActivity.this, true, false, NINE, SIXTEEN, leftPhotoNum, false, selectionData, this::selectPhoto);
//                            }
//                        }));
    }

    int photoSize = 0;

    private void selectPhoto(List<LocalMedia> result) {
        if (result != null && result.size() > 0) {
            photoSize = result.size();
            selectionData = result;
            payProof = "";
            currentIndex = 0;
            getUploadToken();
//            for (LocalMedia localMedia : result) {
////                LocalMedia localMedia = result.get(ZERO);
//                if (localMedia != null && CommonUtils.StringNotNull(localMedia.getCompressPath())) {
//                    getUploadToken(localMedia.getCompressPath());
//                } else {
//                    toast(MyApp.getLanguageString(getContext(), R.string.Share_Error));
//                }
//            }
        } else {
            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + ".");
        }
    }

//    private void shootPhoto(List<LocalMedia> result) {
//        if (result != null && result.size() > 0) {
//            selectionData.addAll(result);
////            for (LocalMedia localMedia : result) {
//            LocalMedia localMedia = result.get(ZERO);
//            if (localMedia != null && CommonUtils.StringNotNull(localMedia.getCompressPath())) {
//                getUploadToken(localMedia.getCompressPath());
//            } else {
//                toast(MyApp.getLanguageString(getContext(), R.string.Share_Error));
//            }
////            }
//        } else {
//            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + ".");
//        }
//    }

    int currentIndex = 0;

    //mode 0 拍照 1 选照片
    private void getUploadToken() {
        showDialog();
        String path = selectionData.get(currentIndex).getCompressPath();
        new UserImpl(new ZZNetCallBack<FilePathBean>(this, FilePathBean.class) {
            @Override
            public void onSuccess(FilePathBean response) {
                if (null != response) {
                    if (TextUtils.isEmpty(payProof)) {
                        payProof = response.getPath();
                    } else {
                        payProof = payProof + "," + response.getPath();
                    }
                    osvStatus.setUploadPzSuccess();//图片上传成功,隐藏买家取消按钮
                    currentIndex++;
                    if (selectionData.size() > currentIndex) {
                        getUploadToken();
                    } else {
                        closeDialog();
                    }
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                closeDialog();
                getOrderBuyDetail(false);
            }
        }.setNeedDialog(false).setTimeOut(singleTime)).uploadQrCode(Config.FIVE_STRING, path);
    }

}
