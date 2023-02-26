package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityPresent;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CaptchaImageData;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.ServiceUrlData;
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
import com.kaiserkalep.net.impl.WebsiteImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.KeyBoardUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyActivityManager;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.NetWorkCaCheUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.PasswordEditTextView;
import com.kaiserkalep.widgets.UpdateDialog;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.EIGHT;
import static com.kaiserkalep.constants.Config.ELEVEN;
import static com.kaiserkalep.constants.Config.FOUR;
import static com.kaiserkalep.constants.Config.NINE;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.SEVEN;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;


/**
 * 功能描述:  登录页
 *
 * @auther: Jack
 * @date: 2020/8/12 14:52
 */
public class LoginActivity extends ZZActivity implements TextWatcher, CleanEditTextView.MyFocusChangeListener {

    @BindView(R.id.login_et_input_phone)
    CleanEditTextView loginEtInputPhone;
    @BindView(R.id.login_et_input_pass)
    PasswordEditTextView loginEtInputPass;
    @BindView(R.id.login_iv_pass_right)
    ImageView loginIvPassRight;
    @BindView(R.id.login_sl_login)
    ShadowLayout loginSlLogin;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private ServiceUrlData serviceurldata;
    private String mAccount = "";
    private String mPassword = "";
    private boolean[] isChangeStatus = {true};//密码可见标识,登录密码
    private EditText[] passwordEt;
    private ImageView[] passwordIv;
    /**
     * 输入校验,焦点失去是校验
     */
    private HashMap<EditText, Integer> inputMap = new HashMap<>();
    private CaptchaDialog mCaptDialog;
    private String title;

    @Override
    public int getViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void afterViews() {
        Log.e("answer", "login_afterViews");
        title = MyApp.getLanguageString(getContext(), R.string.login_acitivity_title);
        tvVersion.setText(MyApp.getLanguageString(getContext(), R.string.app_version) + "：\tv" + ClientInfo.VER);
        isStatusBar = true;
        initParams();

        initEdit(loginEtInputPhone, loginEtInputPass);
        MyApp.postDelayed(this::checkInput, 300);
    }

    @Override
    public void onResume() {
        super.onResume();
        pagePv(true);
        setUserName();
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

    private void setUserName() {
        mAccount = SPUtil.getUserPhone();
        if (null != loginEtInputPhone && CommonUtils.StringNotNull(mAccount)) {
            if (mAccount.length() == ELEVEN) {
                loginEtInputPhone.setEditText(mAccount);
                loginEtInputPhone.clearFocus();
            } else {
                SPUtil.setUserPhone("");
            }
        }
        if (null != loginEtInputPass) {
            loginEtInputPass.setEditText("");
            loginEtInputPass.clearFocus();
        }
    }

    /**
     * 处理更新弹框
     */
    private void handlerUpDateDialog() {
        MyApp.postDelayed(() -> {
            UpdateDialog dialog = MyDialogManager.getManager().getUpdateDialog();
            if (dialog != null) {
                Activity activity = dialog.getActivity();
                UpdateDate updateDate = dialog.getUpdateDate();
                if (!(activity instanceof LoginActivity) && updateDate != null) {
                    LogUtils.d("更新弹框处理弹出到登录页");
                    dialog.dismiss();
                    MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.ONE.value,
                            MyDialogManager.UpdateDialog, updateDate);
                }
            }
        }, 150);
    }

    /**
     * 友盟pv统计
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
     * 初始输入框
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
     * 初始页面传参
     */
    private void initParams() {
        passwordEt = new EditText[]{loginEtInputPass};
        passwordIv = new ImageView[]{loginIvPassRight};
    }

    /**
     * 设置密码输入可见
     *
     * @param index EditText数组中下表
     */
    private void changePwVisible(int index) {
        isChangeStatus[index] = !isChangeStatus[index];
        if (isChangeStatus[index]) {
            passwordIv[index].setImageResource(R.drawable.icon_pass_hide);
            //从密码可见模式变为密码不可见模式
            passwordEt[index].setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            passwordIv[index].setImageResource(R.drawable.icon_pass_show);
            //从密码不可见模式变为密码可见模式
            passwordEt[index].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        try {
            passwordEt[index].requestFocus();
            KeyBoardUtils.setCursorRight(passwordEt[index]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弱提示
     *
     * @param msg 文字显示
     */
    public void loginToast(String msg) {
        toast(msg);
    }

    private void login() {
        getCaptcha(true, null);
    }


    /**
     * 获取验证码及设置图片
     * <p>
     * 登录
     *
     * @param listener
     */
    private void getCaptcha(boolean needDialog, SucceedCallBackListener<CaptchaImageData> listener) {
        new UserImpl(new ZZNetCallBack<CaptchaImageData>(this, CaptchaImageData.class) {
            @Override
            public void onSuccess(CaptchaImageData response) {
                if (response != null) {
                    if (CommonUtils.StringNotNull(response.getVerificationCode())) {//无须验证,直接登录
                        startLogin(response.getVerificationCode(), false);
                    } else {
                        response.setPhone(mAccount);
                        if (TWO_STRING.equals(response.getType())) {
                            MyDialogManager.getManager().disMissSliderValidationDialog();
                            showDXDialog(mAccount, response.getAppId(), response.getApiServer());
                        } else {//旧验证逻辑
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
                LoginActivity.this.closeDialog();
                if (listener != null) {
                    listener.succeedCallBack(new CaptchaImageData().setErrorMsg(msg));
                } else {
                    loginToast(msg);
                }
            }
        }.setNeedDialog(needDialog)).captcha(mAccount);
    }

    /**
     * 顶象验证码弹窗
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
                    startLogin(validCode, true);
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
     * 显示滑块弹框
     * <p>
     * 登录
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
                                startLogin(validCode, true);
                            }

                            @Override
                            public void refreshCaptcha(boolean needLoading, SucceedCallBackListener<CaptchaImageData> listener) {
                                getCaptcha(needLoading, listener);
                            }
                        }));
    }


    private void startLogin(String validCode, boolean needDialog) {
        new UserImpl(new ZZNetCallBack<UserData>(this, UserData.class) {

            @Override
            public void onSuccessToVerifyNew(UserData response, boolean isVerifynew) {
                super.onSuccessToVerifyNew(response, isVerifynew);
                if (isVerifynew) {
                    startClass(MyApp.getLanguageString(LoginActivity.this, R.string.VerifyNewActivity), IntentUtils.getHashObj(new String[]{
                            StringConstants.PHONE, mAccount,
                            StringConstants.PASSWORD, mPassword}));
                }
            }

            @Override
            public void onSuccess(UserData response) {
                if (response != null) {
                    loginSucceed(mAccount, mPassword, response);
                } else {
                    onError("", "");
                }
            }

            @Override
            public void onError(String msg, String code) {
                loginToast(msg);
            }
        }.setNeedDialog(needDialog)).login(mAccount, mPassword, validCode);
    }

    /**
     * 登录
     *
     * @param account
     * @param psd
     * @param response
     */
    private void loginSucceed(String account, String psd, UserData response) {
        if (loginSlLogin != null) {
            loginSlLogin.setClickable(false);
        }
        toast(MyApp.getLanguageString(getContext(), R.string.login_success));
        SPUtil.setUserPhone(account);
        MobclickAgent.onProfileSignIn(account);
        CommonUtils.setLoginSuccessData(response);
        EventBusUtil.post(new LoginSuccessEvent(response));
        closeDialog();
        startClass(MyApp.getLanguageString(this, R.string.MainActivity));
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable phoneText = loginEtInputPhone.getText();
        if (null != phoneText) {
            mAccount = phoneText.toString().trim();
        } else {
            mAccount = "";
        }
        Editable passText = loginEtInputPass.getText();
        if (null != passText) {
            mPassword = passText.toString().trim();
        } else {
            mPassword = "";
        }
        checkInput();
    }

    private void checkInput() {
        checkPasswordIv();
        loginSlLogin.setClickable(
                CommonUtils.StringNotNull(mAccount, mPassword)
                        && mAccount.length() >= 11
                        && mPassword.length() >= 6);
//                        && mPassword.matches(StringConstants.matches));
    }

    /**
     * 校验密码输入框焦点及内容显示明文icon
     */
    private void checkPasswordIv() {
        passwordIv[0].setVisibility(CommonUtils.StringNotNull(mPassword) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {

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
        setIntent(intent);//设置新的intent
        initParams();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitBy2Click(getApplicationContext())) {// 调用双击退出函数
                MyActivityManager.getActivityManager().cleanAll();
            }
        }
        return false;
    }

    /**
     * 输入框焦点变化,失去焦点的校验
     * 顺序对应{@link #initEdit(CleanEditTextView...)}
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
        //处理失去焦点校验及隐藏密码明文icon
        switch (integer) {
            case ZERO://登录账号
                if (CommonUtils.StringNotNull(mAccount)) {
                    if (mAccount.length() != ELEVEN) {
//                        loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
                        return;
                    }
                }
                break;
            case ONE://登录密码
                if (CommonUtils.StringNotNull(mPassword)) {
//                    if (mPassword.length() < SIX || mPassword.length() > 16) {
//                        loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
//                        return;
//                    }
//                    if (!mPassword.matches(StringConstants.matches)) {
//                        loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
//                        return;
//                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FindPasswordActivity.CHANGE_SUCCEED) {
            loginEtInputPass.setEditText("");
            loginEtInputPass.clearFocus();
        }
    }

    @OnClick({R.id.login_sl_login, R.id.login_iv_pass_right, R.id.login_tv_online_service, R.id.login_tv_forgot_password, R.id.login_tv_reg_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_sl_login:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (TextUtils.isEmpty(mAccount) || mAccount.length() != ELEVEN) {
                        loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
                    return;
                }
                if (TextUtils.isEmpty(mPassword) || mPassword.length() < SIX || mPassword.length() > 16) {
                    loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
                    return;
                }
                if (!mPassword.matches(StringConstants.matches)) {
                    loginToast(MyApp.getLanguageString(this, R.string.login_edit_input_pass_error_tip));
                    return;
                }
                hideInput();
                login();
                break;
            case R.id.login_iv_pass_right:
                changePwVisible(ZERO);
                break;
            case R.id.login_tv_online_service:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (null != serviceurldata) {
                    getWebView(serviceurldata.getMain());
                } else {
                    getService();
                }
                break;
            case R.id.login_tv_forgot_password:
                hideInput();
                startClassForResult(MyApp.getLanguageString(getContext(), R.string.FindPasswordActivity), null, FindPasswordActivity.CHANGE_SUCCEED);
                break;
            case R.id.login_tv_reg_btn:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                hideInput();
                startClass(MyApp.getLanguageString(getContext(), R.string.RegisterActivity), null);
                break;
        }
    }

    private void getWebView(String service) {
        String title = MyApp.getLanguageString(getContext(), R.string.main_tab_service);
        HashMap<String, String> param = new HashMap();
        param.put(StringConstants.URL, service);
        param.put(StringConstants.TITLE, title);
        LoginActivity.this.startClass(MyApp.getLanguageString(LoginActivity.this, R.string.WebViewActivity), param, false, null);
    }

    private void getService() {
        new WebsiteImpl(new ZZNetCallBack<ServiceUrlData>(this, ServiceUrlData.class) {
            @Override
            public void onSuccess(ServiceUrlData response) {
                if (null != response) {
                    LoginActivity.this.serviceurldata = response;
                    getWebView(response.getMain());
                    NetWorkCaCheUtils.setServiceUrl(response);
                } else {
                    toast(MyApp.getLanguageString(LoginActivity.this, R.string.Share_Data_Error));
                }
            }
        }.setNeedDialog(true).setNeedToast(true)).getServiceUrl();
    }

    /**
     * 主要用于poc测试，每次请求不走本地缓存
     *
     * @return
     */
    public HashMap<String, String> getTokenConfig() {
        HashMap<String, String> config = new HashMap<>();
//        config.put("PRIVATE_CLEAR_TOKEN", "true");
        return config;
    }

    /**
     * 初始化参数
     *
     * @return
     */
    private HashMap<String, Object> getConfig(String appid, String server) {
        Map<String, Object> customStyle = new HashMap<String, Object>();
        Map<String, Object> customLanguage = new HashMap<String, Object>();
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("customLanguage", customLanguage); // 自定义语言
        config.put("customStyle", customStyle); // 自定义风格
        config.put("language", "cn"); // 默认中文

//        DxServerConfig dxServerConfig = new DxServerConfig(appid, server);
//        String apiServer = dxServerConfig.getApi_server();
//        String uaJs = dxServerConfig.getUa_js();
//        String capJs = dxServerConfig.getCap_js();
//        String constIdServer = dxServerConfig.getConst_id_server();
//        String constIdJs = dxServerConfig.getConst_id_js();
//        // 私有化选填，设备指纹数据线上备份
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
