package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.ChannelDetailsBean;
import com.kaiserkalep.bean.FilePathBean;
import com.kaiserkalep.bean.PayPassWordDialogData;
import com.kaiserkalep.bean.SelectBankDialogData;
import com.kaiserkalep.bean.SimpleDialogData;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SelectBankInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.PickImageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.king.zxing.QrReadPhotoCallBackListener;
import com.king.zxing.util.QRCodeHelpers;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.base.BaseNetUtil.singleTime;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.StringConstants.BANK_ID;
import static com.kaiserkalep.constants.StringConstants.BANK_NO;
import static com.kaiserkalep.constants.StringConstants.PAYPWD;


/**
 * 添加收付款
 *
 * @Auther: Jack
 * @Date: 2021/1/1 16:42
 * @Description:
 */
public class AddWalletManageActivity extends ZZActivity implements TextWatcher, View.OnClickListener {

    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.ll_wechat_view)
    LinearLayout llWechatView;
    @BindView(R.id.cb_wechat)
    CheckBox cbWechat;
    @BindView(R.id.ll_alipay_view)
    LinearLayout llAlipayView;
    @BindView(R.id.cb_alipay)
    CheckBox cbAlipay;
    @BindView(R.id.ll_bank_view)
    LinearLayout llBankView;
    @BindView(R.id.cb_bank)
    CheckBox cbBank;

    @BindView(R.id.tv_read_name)
    TextView tvReadName;
    @BindView(R.id.ll_pay_account_view)
    LinearLayout llPayAccountView;
    @BindView(R.id.tv_pay_account_tip)
    TextView tvPayAccountTip;
    @BindView(R.id.et_pay_account)
    CleanEditTextView etPayAccount;
    @BindView(R.id.et_bank_account)
    CleanEditTextView etBankAccount;
    @BindView(R.id.tv_upload_type_tip)
    TextView tvUploadTypeTip;
    @BindView(R.id.rl_add_qrcode)
    RelativeLayout rlAddQrcode;
    @BindView(R.id.rl_add_bank)
    LinearLayout rlAddBank;
    @BindView(R.id.tv_now_bank)
    TextView tvNowBank;
    @BindView(R.id.iv_upload_rear)
    ImageView ivUploadRear;

    @BindView(R.id.btn_shadow)
    ShadowLayout btnShadow;

    private String title = "";
    private int paytype = 0; // 0:微信； 1：alipay； 2:bank;
    private String wechatQrcode = ""; // 微信二维码
    private String alipayQrcode = ""; // 支付宝二维码
    private String aliAccount = ""; // 支付账号
    private String bankAccount = ""; // 银行账号
    private ChannelDetailsBean channeldetailsbean; // 选中银行信息

    /**
     * 选择银行列表
     */
    private List<ChannelDetailsBean> bankList = new ArrayList<>();
    private String realName;
    private boolean showWeChat = true;
    private boolean showAlipay = true;
    private boolean showBank = true;
    private List<WalletManageBean> myWalletList;

    @Override
    public int getViewId() {
        return R.layout.activity_addwallet_manage;
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
        title = MyApp.getLanguageString(getContext(), R.string.addwallet_title);
        commTitle.init(title);
        try {
//            HashMap<String, String> urlParam = getUrlParam();
//            if (null != urlParam && urlParam.size() > ZERO) {
//                String s1 = urlParam.get(StringConstants.WALLET_WECHAT);
//                showWeChat = Config.ZERO_STRING.equals(s1);
//                String s2 = urlParam.get(StringConstants.WALLET_ALIPAY);
//                showAlipay = Config.ZERO_STRING.equals(s2);
//                String s3 = urlParam.get(StringConstants.WALLET_BANK);
//                showBank = Config.ZERO_STRING.equals(s3);
//            }

            realName = SPUtil.getRealName();
            LogUtils.e(TAG, "PAYMENT_MODE_LIST  " + SPUtil.getStringValue(SPUtil.PAYMENT_MODE_LIST));
            myWalletList = SPUtil.getPaymentList();
            int i = 0;
            for (WalletManageBean w : myWalletList) {
                switch (w.getType()) {
                    case 0:
                        showWeChat = false;
                        break;
                    case 1:
                        showAlipay = false;
                        break;
                    case 2:
                        i++;
                        break;
                }
            }
            if (i >= 4) {
                showBank = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG + "  " + e.toString());
        }
        init();
        initListener();
    }

    private void init() {
        tvReadName.setText(realName);
        etPayAccount.addTextChangedListener(this);
        etBankAccount.addTextChangedListener(this);
        llWechatView.setVisibility(showWeChat ? View.VISIBLE : View.GONE);
        llAlipayView.setVisibility(showAlipay ? View.VISIBLE : View.GONE);
        llBankView.setVisibility(showBank ? View.VISIBLE : View.GONE);
        showView(showWeChat ? ZERO : showAlipay ? ONE : showBank ? TWO : ZERO);
    }

    @OnClick({R.id.rl_add_qrcode, R.id.rl_add_bank, R.id.btn_shadow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_add_qrcode:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                openImageDialog();
                break;
            case R.id.rl_add_bank:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (bankList.size() > ZERO) {
                    selectBankList();
                } else {
                    getBankList();
                }
                break;
            case R.id.btn_shadow:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.InputPassWordDialog,
                        new PayPassWordDialogData(o -> {
                            if (null != o && CommonUtils.StringNotNull(o) && o.length() == SIX) {
                                uploadWallet(o);
                            } else {
                                toast(MyApp.getLanguageString(getContext(), R.string.please_input_passw_finish_tip));
                            }
                        }, null));
                break;
        }
    }

    public void uploadWallet(String pass) {
        HashMap<String, String> params = new HashMap<>();
        params.put(StringConstants.TYPE, "" + paytype);
        params.put(PAYPWD, pass);
        switch (paytype) {
            case ZERO:
//                new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
//                    @Override
//                    public void onSuccess(Object response) {
//                        AddWalletManageActivity.this.finish();
//                    }
//                }.setNeedDialog(true)).updateWxCode(wechatQrcode, pass);
                params.put(StringConstants.QRCODE, wechatQrcode);
                break;
            case ONE:
//                new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
//                    @Override
//                    public void onSuccess(Object response) {
//                        AddWalletManageActivity.this.finish();
//                    }
//                }.setNeedDialog(true)).updateZFB(aliAccount, alipayQrcode, pass);
                params.put(StringConstants.BANK_NO, aliAccount);
                params.put(StringConstants.QRCODE, alipayQrcode);
                break;
            case TWO:
//                new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
//                    @Override
//                    public void onSuccess(Object response) {
//                        AddWalletManageActivity.this.finish();
//                    }
//                }.setNeedDialog(true)).updateBank(channeldetailsbean.getId(), bankAccount, pass);
                params.put(BANK_NO, bankAccount);
                params.put(BANK_ID, channeldetailsbean.getId());
                break;
        }
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                AddWalletManageActivity.this.finish();
            }
        }.setNeedDialog(true)).updateBank(params);
    }

    private void getBankList() {
        new FundImpl(new ZZNetCallBack<List<ChannelDetailsBean>>(this, ChannelDetailsBean.class) {
            @Override
            public void onSuccess(List<ChannelDetailsBean> response) {
                if (null != response && response.size() > ZERO) {
                    if (bankList.size() > ZERO) {
                        bankList.clear();
                    }
                    //过滤掉我已经添加过的银行
//                    if (myWalletList.size() > 0) {
//                        for (WalletManageBean wallet : myWalletList) {
//                            if (!TextUtils.isEmpty(wallet.getBankId())) {
//                                for (int i = 0; i < response.size(); i++) {
//                                    ChannelDetailsBean bean = response.get(i);
//                                    if (bean.getId().equals(wallet.getBankId())) {
//                                        response.remove(bean);
//                                        i--;
//                                    }
//                                }
//                            }
//                        }
//                    }
                    bankList.addAll(response);
                    selectBankList();
                }
            }


        }.setNeedDialog(true).setNeedToast(true)).getBankList();
    }

    /**
     * 显示选择银行卡列表
     */
    private void selectBankList() {
        String title = MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_choose_bank);
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SelectBankDialog,
                new SelectBankDialogData<ChannelDetailsBean>(title, R.drawable.icon_bank_logo, bankList, (o, position) -> {
                    if (Config.EGATIVE_ONE != position) {
                        AddWalletManageActivity.this.channeldetailsbean = o;
                        checkBtn();
                        for (int j = 0; j < bankList.size(); j++) {
                            bankList.get(j).setSelect(position == j);
                        }
                    }
                }));
    }

    /**
     * 上传二维码
     */
    private void openImageDialog() {
        int color = getResources().getColor(R.color.face_text_bold_color);
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SimpleDialog,
                new SimpleDialogData(MyApp.getLanguageString(getContext(), R.string.open_camera), color,
                        MyApp.getLanguageString(getContext(), R.string.open_photo), color,
                        o -> {
                            if (ONE_STRING.equals(o)) {
                                PickImageUtils.openCamera(AddWalletManageActivity.this, false, ONE, ONE, this::judgeQrPhoto);
                            } else if (TWO_STRING.equals(o)) {
                                PickImageUtils.openGallery(AddWalletManageActivity.this, true, ONE, ONE, this::judgeQrPhoto);
                            }
                        }));
    }

    /**
     * 解析二维码
     *
     * @param result
     */
    private void judgeQrPhoto(List<LocalMedia> result) {
        if (null != result && result.size() >= ONE) {
            LocalMedia localMedia = result.get(ZERO);
            if (localMedia != null && CommonUtils.StringNotNull(localMedia.getCutPath())) {
                String cutPath = localMedia.getCutPath();
                //启动线程完成图片扫码
                new QRCodeHelpers.QrCodeAsyncTask(cutPath, str -> {
                    Log.e("answer", "图片内容：" + str);
                    if (CommonUtils.StringNotNull(str)) {
                        ivUploadRear.setVisibility(View.VISIBLE);
                        GlideUtil.loadLocalImage(cutPath, ivUploadRear);
                        getUploadToken(cutPath);
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                    }
                }).execute(cutPath);
            } else {
                toast(MyApp.getLanguageString(getContext(), R.string.Share_Error));
            }
        } else {
            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + ".");
        }
    }

    private void getUploadToken(String Path) {
        new UserImpl(new ZZNetCallBack<FilePathBean>(this, FilePathBean.class) {
            @Override
            public void onSuccess(FilePathBean response) {
                if (null != response) {
                    String path = response.getPath();
                    if (paytype == ZERO) {
                        wechatQrcode = path;
                        GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(wechatQrcode), ivUploadRear);
                    } else if (paytype == ONE) {
                        alipayQrcode = path;
                        GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(alipayQrcode), ivUploadRear);
                    }
                    checkBtn();
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
            }
        }.setNeedDialog(true).setTimeOut(singleTime)).uploadQrCode(paytype == ZERO ? TWO_STRING : paytype == ONE ? THREE_STRING : "", Path);
    }


    private void checkBtn() {
        switch (paytype) {
            case ZERO:
                btnShadow.setClickable(CommonUtils.StringNotNull(wechatQrcode));
                break;
            case ONE:
                btnShadow.setClickable(CommonUtils.StringNotNull(alipayQrcode) && CommonUtils.StringNotNull(aliAccount));
                break;
            case TWO:
                boolean isBeanNotNull = null != channeldetailsbean;
                btnShadow.setClickable(isBeanNotNull && CommonUtils.StringNotNull(bankAccount) && bankAccount.length() >= 16);
                tvNowBank.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tvNowBank.setText(isBeanNotNull ? AddWalletManageActivity.this.channeldetailsbean.getName() : MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_choose_bank));
                break;
        }
    }

    /**
     * 监听收款方式
     */
    private void initListener() {
        cbWechat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                showView(ZERO);
            }
        });
        cbAlipay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                showView(ONE);
            }
        });
        cbBank.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                showView(TWO);
            }
        });
    }

    private void showView(int num) {
        switch (num) {
            case ZERO:
                paytype = ZERO;
                cbWechat.setChecked(true);
                cbAlipay.setChecked(false);
                cbBank.setChecked(false);
                llPayAccountView.setVisibility(View.GONE);
                rlAddQrcode.setVisibility(View.VISIBLE);
                rlAddBank.setVisibility(View.GONE);
                tvUploadTypeTip.setText(MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_upload_Receipt_Code));
                if (CommonUtils.StringNotNull(wechatQrcode)) {
                    ivUploadRear.setVisibility(View.VISIBLE);
                    GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(wechatQrcode), ivUploadRear);
                } else {
                    ivUploadRear.setVisibility(View.GONE);
                }
                checkBtn();
                break;
            case ONE:
                paytype = ONE;
                cbWechat.setChecked(false);
                cbAlipay.setChecked(true);
                cbBank.setChecked(false);
                llPayAccountView.setVisibility(View.VISIBLE);
                rlAddQrcode.setVisibility(View.VISIBLE);
                rlAddBank.setVisibility(View.GONE);
                etPayAccount.setVisibility(View.VISIBLE);
                etBankAccount.setVisibility(View.GONE);
                tvUploadTypeTip.setText(MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_upload_Receipt_Code));
                tvPayAccountTip.setText(MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_alipay_title));
                if (CommonUtils.StringNotNull(alipayQrcode)) {
                    ivUploadRear.setVisibility(View.VISIBLE);
                    GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(alipayQrcode), ivUploadRear);
                } else {
                    ivUploadRear.setVisibility(View.GONE);
                }
                checkBtn();
                break;
            case TWO:
                paytype = TWO;
                cbWechat.setChecked(false);
                cbAlipay.setChecked(false);
                cbBank.setChecked(true);
                llPayAccountView.setVisibility(View.VISIBLE);
                rlAddQrcode.setVisibility(View.GONE);
                rlAddBank.setVisibility(View.VISIBLE);
                etPayAccount.setVisibility(View.GONE);
                etBankAccount.setVisibility(View.VISIBLE);
                tvUploadTypeTip.setText(MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_choose_bank));
                tvPayAccountTip.setText(MyApp.getLanguageString(AddWalletManageActivity.this, R.string.wallet_bank_title));
                checkBtn();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable paytext = etPayAccount.getText();
        if (null != paytext) {
            aliAccount = paytext.toString().trim();
        } else {
            aliAccount = "";
        }
        Editable banktext = etBankAccount.getText();
        if (null != banktext) {
            bankAccount = banktext.toString().trim();
        } else {
            bankAccount = "";
        }

        checkBtn();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
