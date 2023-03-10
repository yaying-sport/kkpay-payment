package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityPresent;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CaptchaImageData;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.SliderValidationDialogData;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.constants.DxServerConfig;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.LoginSuccessEvent;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.mydialog.CaptchaDialog;
import com.kaiserkalep.mydialog.RegistrNoticeDialog;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.KeyBoardUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.FillBlankView;
import com.kaiserkalep.widgets.PasswordEditTextView;
import com.kaiserkalep.widgets.UpdateDialog;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ELEVEN;
import static com.kaiserkalep.constants.Config.FOUR;
import static com.kaiserkalep.constants.Config.NINE;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;


/**
 * ????????????:  ?????????
 *
 * @auther: Jack
 * @date: 2020/8/12 14:52
 */
public class RegisterActivity extends PhoneCodeActivity implements TextWatcher {

    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.reg_et_input_pass)
    PasswordEditTextView regEtInputPass;
    @BindView(R.id.reg_iv_pass_right)
    ImageView regIvPassRight;
    @BindView(R.id.reg_et_input_passtwo)
    PasswordEditTextView regEtInputPasstwo;
    @BindView(R.id.reg_iv_passtwo_right)
    ImageView regIvPasstwoRight;
    @BindView(R.id.reg_ll_passpay_layout)
    FillBlankView regLlPasspayLayout;
    @BindView(R.id.reg_sl_login)
    ShadowLayout regSlLogin;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @BindView(R.id.ll_edit_phone_layout)
    RelativeLayout llEditPhoneLayout;

    private String mRegisterPassword = "";
    private String mPasswordConfirm = "";
    private boolean[] isChangeStatus = {true, true};//??????????????????,????????????,??????????????????
    private EditText[] passwordEt;
    private ImageView[] passwordIv;
    /**
     * ????????????,?????????????????????
     */
    private HashMap<EditText, Integer> inputMap = new HashMap<>();
    private CaptchaDialog mCaptDialog;
    private String title;
    private String paypwd;

    @Override
    public int getViewId() {
        return R.layout.activity_register;
    }

    @Override
    public void afterViews() {
        super.afterViews();
        AndroidBug5497Workaround.assistActivity(this).setNeedStatusBar(false);
        title = MyApp.getLanguageString(getContext(), R.string.register_btn_text);
        commTitle.init("");
        llEditPhoneLayout.setVisibility(View.VISIBLE);
        tvVersion.setText(MyApp.getLanguageString(getContext(), R.string.app_version) + "???\tv" + ClientInfo.VER);
        initParams();
        initEdit(regEtInputPass, regEtInputPasstwo, etPhone);
        MyApp.postDelayed(this::checkInput, 300);

        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.ONE.value,
                MyDialogManager.RegistrNoticeDialog, (DialogInterface.OnDismissListener) dialog -> {
                    if (dialog instanceof RegistrNoticeDialog) {


                    }
                });

        regLlPasspayLayout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                paypwd = regLlPasspayLayout.getAllText();
                checkInput();
            }
        });
    }

    @Override
    protected String getPhone() {
        return mPhone;
    }

    @Override
    public void onResume() {
        super.onResume();
        pagePv(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pagePv(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handlerUpDateDialog();
    }

    /**
     * ??????????????????
     */
    private void handlerUpDateDialog() {
        MyApp.postDelayed(() -> {
            UpdateDialog dialog = MyDialogManager.getManager().getUpdateDialog();
            if (dialog != null) {
                Activity activity = dialog.getActivity();
                UpdateDate updateDate = dialog.getUpdateDate();
                if (!(activity instanceof LoginActivity) && updateDate != null) {
                    LogUtils.d("????????????????????????????????????");
                    dialog.dismiss();
                    MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.ONE.value,
                            MyDialogManager.UpdateDialog, updateDate);
                }
            }
        }, 150);
    }

    /**
     * ??????pv??????
     *
     * @param isStart
     */
    private void pagePv(boolean isStart) {
        if (isStart) {
            onPageStart(title);
        } else {
            onPageEnd(title);
        }
    }

    /**
     * ???????????????
     *
     * @param editText
     */
    private void initEdit(CleanEditTextView... editText) {
        for (int i = 0; i < editText.length; i++) {
            CleanEditTextView text = editText[i];
            if (text != null) {
                text.addTextChangedListener(this);
                text.setMyFocusChangeListener(this);
                inputMap.put(text, i);
            }
        }
    }

    /**
     * ??????????????????
     */
    private void initParams() {
        passwordEt = new EditText[]{regEtInputPass, regEtInputPasstwo};
        passwordIv = new ImageView[]{regIvPassRight, regIvPasstwoRight};
    }

    /**
     * ????????????????????????
     *
     * @param Index ???????????????
     */
    private void changePwVisible(int Index) {
        isChangeStatus[Index] = !isChangeStatus[Index];
        if (isChangeStatus[Index]) {
            passwordIv[Index].setImageResource(R.drawable.icon_pass_hide);
            //????????????????????????????????????????????????
            passwordEt[Index].setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            passwordIv[Index].setImageResource(R.drawable.icon_pass_show);
            //????????????????????????????????????????????????
            passwordEt[Index].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        try {
            passwordEt[Index].requestFocus();
            KeyBoardUtils.setCursorRight(passwordEt[Index]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????
     *
     * @param msg ????????????
     */
    public void loginToast(String msg) {
        toast(msg);
    }

    private void register() {
        getCaptcha(true, null);
    }

    /**
     * ??????????????????????????????
     * <p>
     * ??????
     *
     * @param listener
     */
    private void getCaptcha(boolean needDialog, SucceedCallBackListener<CaptchaImageData> listener) {
        new UserImpl(new ZZNetCallBack<CaptchaImageData>(this, CaptchaImageData.class) {
            @Override
            public void onSuccess(CaptchaImageData response) {
                if (response != null) {
                    if (CommonUtils.StringNotNull(response.getVerificationCode())) {//????????????,????????????
                        startRegister(response.getVerificationCode());
                    } else {
                        response.setPhone(mPhone);
                        if (TWO_STRING.equals(response.getType())) {
                            MyDialogManager.getManager().disMissSliderValidationDialog();
                            showDXDialog(mPhone, response.getAppId(), response.getApiServer());
                        } else {//???????????????
                            if (CommonUtils.StringNotNull(response.getBg(), response.getSlide())) {
                                if (listener != null) {
                                    listener.succeedCallBack(response);
                                } else {
                                    setCaptcha(response);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String msg, String code) {
                if (listener != null) {
                    listener.succeedCallBack(new CaptchaImageData().setErrorMsg(msg));
                } else {
                    loginToast(msg);
                }
            }
        }.setNeedDialog(needDialog)).captcha(mPhone);
    }

    /**
     * ?????????????????????
     */
    public void showDXDialog(String phone, String id, String server) {
        if (null != mCaptDialog && mCaptDialog.isShowing()) {
            mCaptDialog.dismiss();
        }
        mCaptDialog = new CaptchaDialog(this);
        mCaptDialog.setCanceledOnTouchOutside(false);
        mCaptDialog.addOnCallBackCaptchaListener(new CaptchaDialog.OnCallBackCaptchaListener() {
            @Override
            public void callBack(String validCode) {
                MyApp.postDelayed(() -> {
                    startRegister(validCode);
                }, 80);
            }

            @Override
            public void onError() {
                getCaptcha(true, null);
            }
        });

        mCaptDialog.init(this, phone, id, getConfig(id, server), getTokenConfig(), DxServerConfig.PERCENT_WIDTH);

        if (!mCaptDialog.isShowing()) {
            mCaptDialog.show();
        }
    }

    /**
     * ??????????????????
     * <p>
     * ??????
     *
     * @param response
     */
    private void setCaptcha(CaptchaImageData response) {
        if (null != mCaptDialog) {
            mCaptDialog.dismiss();
        }

        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.ONE.value,
                MyDialogManager.SliderValidationDialog, new SliderValidationDialogData(response,
                        new SliderValidationDialogData.MineCallBack() {
                            @Override
                            public void verifyFinish(String validCode) {
                                startRegister(validCode);
                            }

                            @Override
                            public void refreshCaptcha(boolean needLoading, SucceedCallBackListener<CaptchaImageData> listener) {
                                getCaptcha(needLoading, listener);
                            }
                        }));
    }

    private void startRegister(String validCode) {
        new UserImpl(new ZZNetCallBack<UserData>(this, UserData.class) {
            @Override
            public void onSuccess(UserData response) {
                if (response != null) {
                    loginSucceed(mPhone, mRegisterPassword, response);
                } else {
                    onError("", "");
                }
            }

            @Override
            public void onError(String msg, String code) {
                loginToast(msg);
            }

        }.setNeedDialog(true).setNeedToast(true)).register(mPhone, mRegisterPassword, mPasswordConfirm, validCode, paypwd, mCode);
    }

    /**
     * ??????
     *
     * @param account
     * @param psd
     * @param response
     */
    private void loginSucceed(String account, String psd, UserData response) {
        if (regSlLogin != null) {
            regSlLogin.setClickable(false);
        }
        toast(MyApp.getLanguageString(getContext(), R.string.reguster_success));
        SPUtil.setUserPhone(account);
        MobclickAgent.onProfileSignIn(account);
        CommonUtils.setLoginSuccessData(response);
        EventBusUtil.post(new LoginSuccessEvent(response));
        closeDialog();
        startClassWithFlag(MyApp.getLanguageString(this, R.string.MainActivity), null, Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        super.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        Editable textPass = regEtInputPass.getText();
        if (null != textPass) {
            mRegisterPassword = textPass.toString().trim();
        } else {
            mRegisterPassword = "";
        }
        Editable textPasstwo = regEtInputPasstwo.getText();
        if (null != textPasstwo) {
            mPasswordConfirm = textPasstwo.toString().trim();
        } else {
            mPasswordConfirm = "";
        }
        checkInput();

    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
    }

    private void checkInput() {
        checkPasswordIv();
//        regSlLogin.setClickable(CommonUtils.StringNotNull(mPhone, mRegisterPassword, mPasswordConfirm, mCode)
//                && mPhone.length() >= NINE
//                && (mRegisterPassword.length() >= SIX && mRegisterPassword.matches(StringConstants.matches))
//                && ((mPasswordConfirm.length() >= SIX && mPasswordConfirm.matches(StringConstants.matches)))
//                && mRegisterPassword.equals(mPasswordConfirm));

        boolean paypwdEnable = false;
        paypwd = regLlPasspayLayout.getAllText();
        if (CommonUtils.StringNotNull(paypwd) && paypwd.length() == SIX) {
            paypwdEnable = true;
        }
        regSlLogin.setClickable(CommonUtils.StringNotNull(mPhone, mRegisterPassword, mPasswordConfirm, mCode)
                && mPhone.length() >= 11
                && mRegisterPassword.length() >= SIX
                && mPasswordConfirm.length() >= SIX
                && paypwdEnable);
    }

    /**
     * ????????????????????????????????????????????????icon
     */
    private void checkPasswordIv() {
        passwordIv[ZERO].setVisibility(CommonUtils.StringNotNull(mRegisterPassword) ? View.VISIBLE : View.GONE);
        passwordIv[ONE].setVisibility(CommonUtils.StringNotNull(mPasswordConfirm) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
        MyApp.postDelayed(() -> {
            if (ActivityPresent.needClickView != null) {
                if (isLogin()) {
                    ActivityPresent.needClickView.performClick();
                }
                ActivityPresent.needClickView = null;
            }
        }, 500);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d("LoginActivity_onNewIntent");
        setIntent(intent);//????????????intent
        initParams();
    }

    /**
     * ?????????????????????,?????????????????????
     * ????????????{@link #initEdit(CleanEditTextView...)}
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!CommonUtils.MapNotNull(inputMap) || !(v instanceof CleanEditTextView)) {
            return;
        }
        CleanEditTextView cleanEditTextView = (CleanEditTextView) v;
        int integer = inputMap.get(cleanEditTextView);
        if (hasFocus) {
            checkPasswordIv();
            return;
        }
        //?????????????????????????????????????????????icon
//        switch (integer) {
//            case ZERO://????????????
//                if (CommonUtils.StringNotNull(mRegisterPassword)) {
//                    if (!mRegisterPassword.matches(StringConstants.matches) || mRegisterPassword.length() < SIX) {
//                        loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
//                        return;
//                    }
//                }
//                break;
//            case ONE://??????????????????
//                if (CommonUtils.StringNotNull(mRegisterPassword, mPasswordConfirm) && !mRegisterPassword.equals(mPasswordConfirm)) {
//                    loginToast(MyApp.getLanguageString(getContext(), R.string.regsiter_DoublePassWord_Error));
//                    return;
//                }
//                break;
//            case TWO://?????????
//                if (CommonUtils.StringNotNull(mPhone)) {
//                    if (mPhone.length() != ELEVEN) {
//                        toast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
//                        return;
//                    }
//                }
//                break;
//            default:
//                break;
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.reg_iv_pass_right, R.id.reg_iv_passtwo_right, R.id.reg_sl_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_iv_pass_right:
                changePwVisible(ZERO);
                break;
            case R.id.reg_iv_passtwo_right:
                changePwVisible(ONE);
                break;
            case R.id.reg_sl_login:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                hideInput();
                //
                if (!mRegisterPassword.matches(StringConstants.matches) || mRegisterPassword.length() < SIX) {
                    loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
                    return;
                }
                if (CommonUtils.StringNotNull(mRegisterPassword, mPasswordConfirm) && !mRegisterPassword.equals(mPasswordConfirm)) {
                    loginToast(MyApp.getLanguageString(getContext(), R.string.regsiter_DoublePassWord_Error));
                    return;
                }
                if (TextUtils.isEmpty(mPhone) || mPhone.length() != ELEVEN) {
                    toast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
                    return;
                }
                //
                paypwd = regLlPasspayLayout.getAllText();
                if (CommonUtils.StringNotNull(paypwd) && paypwd.length() == SIX) {
                    if (!checkPhone()) {
                        register();
                    }
                } else {
                    toast(MyApp.getLanguageString(this, R.string.please_input_password_tip));
                }
                break;
        }
    }

    /**
     * ????????????poc???????????????????????????????????????
     *
     * @return
     */
    public HashMap<String, String> getTokenConfig() {
        HashMap<String, String> config = new HashMap<>();
//        config.put("PRIVATE_CLEAR_TOKEN", "true");
        return config;
    }

    /**
     * ???????????????
     *
     * @return
     */
    private HashMap<String, Object> getConfig(String appid, String server) {
        Map<String, Object> customStyle = new HashMap<String, Object>();
        Map<String, Object> customLanguage = new HashMap<String, Object>();
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("customLanguage", customLanguage); // ???????????????
        config.put("customStyle", customStyle); // ???????????????
        config.put("language", "cn"); // ????????????

//        DxServerConfig dxServerConfig = new DxServerConfig(appid, server);
//        String apiServer = dxServerConfig.getApi_server();
//        String uaJs = dxServerConfig.getUa_js();
//        String capJs = dxServerConfig.getCap_js();
//        String constIdServer = dxServerConfig.getConst_id_server();
//        String constIdJs = dxServerConfig.getConst_id_js();
//        // ????????????????????????????????????????????????
//        if (!TextUtils.isEmpty(apiServer)) {
//            config.put("apiServer", apiServer);
//        }
//        if (!TextUtils.isEmpty(uaJs)) {
//            config.put("ua_js", uaJs);
//        }
//        if (!TextUtils.isEmpty(capJs)) {
//            config.put("captchaJS", capJs);
//        }
//        if (!TextUtils.isEmpty(constIdServer)) {
//            config.put("constIDServer", constIdServer);
//        }
//        if (!TextUtils.isEmpty(constIdJs)) {
//            config.put("constID_js", constIdJs);
//        }
//        config.put("isSaaS", false);

        return config;
    }
}
