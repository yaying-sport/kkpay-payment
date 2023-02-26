package com.kaiserkalep.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AgentFormBean;
import com.kaiserkalep.bean.AgentMemberPayBean;
import com.kaiserkalep.bean.AuthenDialogData;
import com.kaiserkalep.bean.MerchantBean;
import com.kaiserkalep.bean.PayPassWordDialogData;
import com.kaiserkalep.bean.AgentBindInfoBean;
import com.kaiserkalep.bean.ShAccountDialogData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.MySmartRefreshLayout;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.king.zxing.Intents;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.OWN_ONE_STRING;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 商户存款
 */
public class DepositShActivity extends ZZActivity implements TextWatcher {

    public static final int REQUEST_CODE_ADD_DEPOSIT_SCAN = 0X0020;
    public static final int REQUEST_CODE_choose_DEPOSIT_SH = 0X0021;
    public static final int REQUEST_CODE_choose_DEPOSIT_ACCOUNT = 0X0022;

    @BindView(R.id.tv_statusbar)
    ImageView tvStatusbar;
    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_kk_acount)
    TextView tvKkAcount;
    @BindView(R.id.tv_kk_money)
    TextView tvKkMoney;
    @BindView(R.id.et_sh_account)
    CleanEditTextView etShAccount;
    @BindView(R.id.tv_acc_hint)
    TextView tvAccHint;
    @BindView(R.id.et_wj_account)
    CleanEditTextView etWjAccount;
    @BindView(R.id.tv_wj_hint)
    TextView tvWjHint;
    @BindView(R.id.et_money)
    CleanEditTextView etMoney;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.sl_btn)
    ShadowLayout slBtn;

    private String title = "";
    private String allMoney;//总余额
    private String orderNoAgentFrom = "";
    private String shAddress;//商户地址
    private String wjAccount;//玩家账号
    private String money = "";//充值金额
    private String depositshAllMontyTip;
    private String WalletTip;
    private MerchantBean.RowsDTO rowsdto;

    @Override
    public int getViewId() {
        return R.layout.activity_depositsh;
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
        int statusBarHeight = UIUtils.getStatusBarHeight(MyApp.getContext());
        UIUtils.setHeight(tvStatusbar, statusBarHeight);
        shAddress = Config.getUrlValue(getIntent().getStringExtra(StringConstants.DATA));
        title = MyApp.getLanguageString(getContext(), R.string.DepositSh_title);
        depositshAllMontyTip = MyApp.getLanguageString(getContext(), R.string.Depositsh_allMonty_tip);
        WalletTip = MyApp.getLanguageString(getContext(), R.string.home_wallet_tip);
        tvTitle.setText(title);
        refreshLayout.setOnRefreshListener(refreshLayout -> getorderAgentForm(false));
        tvKkAcount.setText(SPUtil.getNickName());
        if (CommonUtils.StringNotNull(shAddress)) {
            etShAccount.setEditText(shAddress);
            etShAccount.setSelection(shAddress.length());//将光标移至文字末尾
            etShAccount.clearFocus();
        }
        etShAccount.addTextChangedListener(this);
        etWjAccount.addTextChangedListener(this);
        etMoney.addTextChangedListener(this);
        getorderAgentForm(true);
    }


    /**
     * 获取存款表单/可存款总金额
     *
     * @param needDialog
     */
    private void getorderAgentForm(boolean needDialog) {
        reset();
        new FundImpl(new ZZNetCallBack<AgentFormBean>(this, AgentFormBean.class) {

            @Override
            public void onSuccess(AgentFormBean response) {
                if (null != response && null != tvKkMoney) {
                    String orderNo = response.getOrderNo();
                    String amount = response.getAmount();
                    if (CommonUtils.StringNotNull(orderNo, amount)) {
                        orderNoAgentFrom = orderNo;
                        allMoney = Config.subNotAndDot(amount);
                        tvKkMoney.setText(depositshAllMontyTip + "：" + allMoney + " " + WalletTip);
                    } else {
                        toast(MyApp.getLanguageString(DepositShActivity.this, R.string.Share_Data_Error) + ".");
                    }
                } else {
                    toast(MyApp.getLanguageString(DepositShActivity.this, R.string.Share_Data_Error));
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (null != refreshLayout) {
                    refreshLayout.finishRefresh();
                }
            }
        }.setNeedDialog(needDialog).setNeedToast(true)).orderAgentPayForm();
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
            startActivity(intent);
        }
        finish();
    }

    @OnClick({R.id.iv_top_left, R.id.iv_sh_right_icon, R.id.ll_scan_camera, R.id.iv_wj_right_icon, R.id.sl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_left:
                customBack();
                break;
            case R.id.iv_sh_right_icon://商户通讯录
                startClassForResult(MyApp.getLanguageString(getContext(), R.string.MerchantDirectoryActivity), null,
                        REQUEST_CODE_choose_DEPOSIT_SH, true, null, null);
                break;
            case R.id.ll_scan_camera:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                cameraCallBack();
                break;
            case R.id.iv_wj_right_icon://玩家通讯录
                if (CommonUtils.StringNotNull(shAddress)) {
                    if (null != rowsdto) {
                        toChooseWjAccount();
                    } else {
                        agentMemberBindInfo(ZERO_STRING, shAddress);
                    }
                } else {
                    toast(MyApp.getLanguageString(this, R.string.depositsh_input_sh_account_tip));
                }
                break;
            case R.id.sl_btn:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                showPassDialog(orderNoAgentFrom);
                break;
        }
    }

    /**
     * 根据钱包地址查找代理商信息
     *
     * @param type 0:选择充值玩家  , 1：扫码回调
     * @param acc
     */
    private void agentMemberBindInfo(String type, String acc) {
        new FundImpl(new ZZNetCallBack<AgentBindInfoBean>(this, AgentBindInfoBean.class) {
            @Override
            public void onSuccess(AgentBindInfoBean response) {
                if (null != response) {
                    rowsdto = new MerchantBean.RowsDTO();
                    rowsdto.setId(response.getId());
                    rowsdto.setAgentName(response.getAgentName());
                    rowsdto.setAgentAccount(response.getAgentAccount());
                    rowsdto.setAgentUsername("");
                    rowsdto.setWalletAddress(response.getWalletAddress());
                    rowsdto.setLogo(response.getLogo());
                    rowsdto.setType(ONE);
                    switch (type) {
                        case ZERO_STRING:
                            toChooseWjAccount();
                            break;
                        case ONE_STRING:
                            if (null != etShAccount) {
                                shAddress = rowsdto.getWalletAddress();
                                etShAccount.setEditText(shAddress);
                                etShAccount.setSelection(shAddress.length());//将光标移至文字末尾
                                etShAccount.clearFocus();
                            }
                            break;
                    }
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).agentMemberBindInfo(acc);
    }

    /**
     * 跳转玩家通讯录
     */
    private void toChooseWjAccount() {
        if (null != rowsdto) {
            Bundle bundle = new Bundle();
            bundle.putString(StringConstants.ID, rowsdto.getId());
            bundle.putString(StringConstants.ADDRESS, rowsdto.getWalletAddress());
            bundle.putString(StringConstants.TITLE, rowsdto.getAgentName());
            startClassForResult(MyApp.getLanguageString(getContext(), R.string.PlayerAddressActivity), null,
                    REQUEST_CODE_choose_DEPOSIT_ACCOUNT, true, bundle, null);
        }
    }

    /**
     * 支付密码
     */
    private void showPassDialog(String orderNoAgent) {
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.InputPassWordDialog,
                new PayPassWordDialogData(o -> {
                    if (null != o && CommonUtils.StringNotNull(o) && o.length() == SIX) {
                        orderAgentMemberPay(orderNoAgent, o);
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.please_input_passw_finish_tip));
                    }
                }, null));
    }

    private void orderAgentMemberPay(String orderNo, String pass) {
        new FundImpl(new ZZNetCallBack<AgentMemberPayBean>(this, AgentMemberPayBean.class) {
            @Override
            public void onSuccess(AgentMemberPayBean response) {
                if (null != response) {

                    String agent = response.getAgent();
                    String username = response.getUsername();
                    if (ZERO_STRING.equals(agent)) {
                        String addshTextOne = MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addsh_text_tip_one);
                        String addshTextTwo = MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addsh_text_tip_two);
                        showAddShDialog(ZERO_STRING, addshTextOne, addshTextTwo);
                    } else if (ZERO_STRING.equals(username)) {
                        String addwjTextOne = MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addwj_text_tip_one);
                        String addwjTextTwo = MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addwj_text_tip_two);
                        showAddShDialog(ONE_STRING, addwjTextOne, addwjTextTwo);
                    } else {
                        toast(MyApp.getLanguageString(DepositShActivity.this, R.string.DepositSh_success_tip));
                        DepositShActivity.this.finish();
                    }
                } else {
                    toast(MyApp.getLanguageString(DepositShActivity.this, R.string.Share_Data_Error));
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).orderAgentMemberPay(orderNo, money, pass, "", shAddress, wjAccount);
    }

    /**
     * 是否添加商户或玩家账号
     */
    private void showAddShDialog(String type, String one, String two) {
        if (CommonUtils.StringNotNull(shAddress, wjAccount)) {
            MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.AddShAccountDialog,
                    new ShAccountDialogData(one, two, v -> {
                        agentMemberAddUsername(type, shAddress, wjAccount);
                    }, v -> DepositShActivity.this.finish()));
        }
    }

    /**
     * @param type          0 添加商户 / 1 添加玩家
     * @param walletAddress
     * @param username
     */
    private void agentMemberAddUsername(String type, String walletAddress, String username) {
        new FundImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(ZERO_STRING.equals(type) ?
                        MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addsh_success_tip) :
                        MyApp.getLanguageString(DepositShActivity.this, R.string.addsh_addwj_success_tip));
                DepositShActivity.this.finish();
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                getorderAgentForm(true);
            }
        }.setNeedDialog(true).setNeedToast(true)).agentMemberAddUserName("", walletAddress, username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_DEPOSIT_SCAN://扫描回调结果
                    String dataStringExtra = data.getStringExtra(Intents.Scan.RESULT);
                    if (null != dataStringExtra && dataStringExtra.contains(Config.SCHEME) && dataStringExtra.contains(Config.SCHEME_SH_HOST) && dataStringExtra.contains(Config.SCHEME_SH_PARAM)) {
                        String result = Config.getUrlValue(dataStringExtra);
                        if (CommonUtils.StringNotNull(result)) {
                            agentMemberBindInfo(ONE_STRING, result);
                        } else {
                            toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                        }
                    }else{
                        toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                    }
                    break;
                case REQUEST_CODE_choose_DEPOSIT_SH://选择商户回调
                    String shjsonExtra = data.getStringExtra(StringConstants.DATA);
                    if (CommonUtils.StringNotNull(shjsonExtra)) {
                        rowsdto = BaseNetUtil.parseFromJson(shjsonExtra, MerchantBean.RowsDTO.class);
                        if (null != rowsdto) {
                            int type = rowsdto.getType();
                            shAddress = rowsdto.getWalletAddress();
                            etShAccount.setEditText(shAddress);
                            etShAccount.setSelection(shAddress.length());//将光标移至文字末尾
                            etShAccount.clearFocus();
                            if (ZERO == type) {//最近（需要设置玩家账号）
                                String agentUsername = rowsdto.getAgentUsername();
                                if (CommonUtils.StringNotNull(agentUsername)) {
                                    wjAccount = agentUsername;
                                    etWjAccount.setEditText(wjAccount);
                                    etWjAccount.setSelection(wjAccount.length());//将光标移至文字末尾
                                    etWjAccount.clearFocus();
                                }
                            } else {
                                wjAccount = "";
                                etWjAccount.setEditText(wjAccount);
                                etWjAccount.clearFocus();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_choose_DEPOSIT_ACCOUNT://选择玩家账号回调
                    String wjAccountExtra = data.getStringExtra(StringConstants.DATA);
                    if (CommonUtils.StringNotNull(wjAccountExtra)) {
                        wjAccount = wjAccountExtra;
                        etWjAccount.setEditText(wjAccount);
                        etWjAccount.setSelection(wjAccount.length());//将光标移至文字末尾
                        etWjAccount.clearFocus();
                    }
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    public void cameraCallBack() {
        Context context = getContext();
        if (null == context || JudgeDoubleUtils.isDoubleClick()) {
            return;
        }
        String RationaleCaneraMsg = MyApp.getLanguageString(context, R.string.Rationale_Canera_Msg);
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)//相机
                .subscribe(permission -> {
                    if (permission.granted) {
                        // All permissions are granted !
                        goToCamera();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // At least one denied permission without ask never again
                        toast(RationaleCaneraMsg);
                    } else {
                        // At least one denied permission with ask never again
                        // Need to go to the settings
                        showRejectCameraDialog(RationaleCaneraMsg);
                    }
                });
    }

    private void goToCamera() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(DepositShActivity.this, R.anim.in, R.anim.out);
        Intent intent = new Intent(DepositShActivity.this, CameraCaptureActivity.class);
        DepositShActivity.this.startActivityForResult(intent, REQUEST_CODE_ADD_DEPOSIT_SCAN, optionsCompat.toBundle());
    }

    private void showRejectCameraDialog(String msg) {
        Context context = getContext();
        if (null != context) {
            String tipAudioCaneraTitle = MyApp.getLanguageString(context, R.string.Rationale_Canera_title);
            String goToSettingTip = MyApp.getLanguageString(context, R.string.goTo_Setting_tip);
            String cancel = MyApp.getLanguageString(context, R.string.Share_cancel);
            new AlertDialog.Builder(context)
                    .setTitle(tipAudioCaneraTitle)
                    .setMessage(msg)
                    .setPositiveButton(goToSettingTip, (dialog, which) -> {
                        dialog.dismiss();
                        Uri packageURI = Uri.parse("package:" + context.getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }).setNegativeButton(cancel, (dialog, i) -> {
                dialog.dismiss();
            }).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable shaccount = etShAccount.getText();
        if (null != shaccount) {
            shAddress = shaccount.toString().trim();
            if (null != rowsdto && CommonUtils.StringNotNull(shAddress) && !shAddress.equals(rowsdto.getWalletAddress())) {
                rowsdto = null;
                wjAccount = "";
                etWjAccount.setEditText(wjAccount);
                etWjAccount.clearFocus();
            }
        } else {
            shAddress = "";
        }

        Editable wjaccount = etWjAccount.getText();
        if (null != wjaccount) {
            wjAccount = wjaccount.toString().trim();
        } else {
            wjAccount = "";
        }

        Editable etmoney = etMoney.getText();
        if (null != etmoney) {
            String trim = etmoney.toString().trim();
            if (CommonUtils.StringNotNull(trim)) {
                try {
                    double sell = Double.parseDouble(trim);
                    double amount = Math.floor(Double.parseDouble(allMoney));
                    if (sell > amount) {
                        etMoney.setText(allMoney);
                        etMoney.setSelection(allMoney.length());//将光标移至文字末尾
                        money = allMoney;
                    } else {
                        money = trim;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    money = "";
                }
            } else {
                money = "";
            }
        } else {
            money = "";
        }
        checkParam();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (null != etMoney) {
            Editable emoney = etMoney.getText();
            String tmoney = emoney.toString();
            int len = tmoney.length();
            if (len > ONE && tmoney.startsWith(ZERO_STRING)) {
                emoney.replace(ZERO, ONE, "");
            }
        }
    }

    /**
     * 重置数据
     */
    private void reset() {
        orderNoAgentFrom = "";
        allMoney = ZERO_STRING;
        if (null != etMoney) {
            etMoney.setEditText("");
            etMoney.clearFocus();
        }
    }

    /**
     * 判断金额是否大于零
     *
     * @param mo
     * @return
     */
    private boolean isGreaterZero(String mo) {
        boolean isNotEmpty = CommonUtils.StringNotNull(mo);
        if (null != tvHint) {
            tvHint.setVisibility(isNotEmpty ? View.GONE : View.VISIBLE);
        }
        if (isNotEmpty) {
            try {
                double m = Double.parseDouble(mo);
                return m > 0;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 效验确认按钮是否可点击
     */
    private void checkParam() {
        if (isGreaterZero(money)) {
            slBtn.setClickable(CommonUtils.StringNotNull(shAddress, wjAccount, orderNoAgentFrom));
        } else {
            slBtn.setClickable(false);
        }

        if (null != tvAccHint) {
            tvAccHint.setVisibility(CommonUtils.StringNotNull(shAddress) ? View.GONE : View.VISIBLE);
        }

        if (null != tvWjHint) {
            tvWjHint.setVisibility(CommonUtils.StringNotNull(wjAccount) ? View.GONE : View.VISIBLE);
        }
    }

}
