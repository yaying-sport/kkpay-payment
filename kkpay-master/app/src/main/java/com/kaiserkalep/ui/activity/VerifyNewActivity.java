package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityPresent;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.CaptchaImageData;
import com.kaiserkalep.bean.SliderValidationDialogData;
import com.kaiserkalep.bean.UpdateDate;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.constants.DxServerConfig;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.eventbus.LoginSuccessEvent;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.mydialog.CaptchaDialog;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.EventBusUtil;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.KeyBoardUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.NoZeroStartFilterText;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.AndroidBug5497Workaround;
import com.kaiserkalep.widgets.BoldTextView;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.FillBlankView;
import com.kaiserkalep.widgets.UpdateDialog;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ELEVEN;
import static com.kaiserkalep.constants.Config.SEVEN;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;


/**
 * 功能描述:  效验手机号登录
 *
 * @auther: Jack
 * @date: 2020/8/12 14:52
 */
public class VerifyNewActivity extends PhoneCodeActivity {


    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.tv_phone)
    BoldTextView tvPhone;

    @BindView(R.id.btn_send_auth_code)
    Button btnSendAuthCode;
    @BindView(R.id.verify_sl_btn)
    ShadowLayout verifySlBtn;
    private String title;
    private String phone = "";
    private String mPassword = "";


    @Override
    public int getViewId() {
        return R.layout.activity_verifynew;
    }

    @Override
    public void afterViews() {
        super.afterViews();
        title = MyApp.getLanguageString(getContext(), R.string.verifynew_title);
        commTitle.init(title);
        phone = getStringParam(StringConstants.PHONE);
        mPassword = getStringParam(StringConstants.PASSWORD);
        if (CommonUtils.StringNotNull(phone) && phone.length() == ELEVEN) {
            setPhone(phone);
            tvPhone.setText(phone);
            checkPhoneInput();
        }
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
                    MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value,
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

    @Override
    protected void setTextChangedLinstener() {
        super.setTextChangedLinstener();
        verifySlBtn.setClickable(CommonUtils.StringNotNull(mCode));
    }


    @OnClick({R.id.verify_sl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verify_sl_btn:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                hideInput();
                if (!checkPhone()) {
                    checkPhoneCode();
                }
                break;
        }
    }

    private void checkPhoneCode() {
        new UserImpl(new ZZNetCallBack<UserData>(this, UserData.class) {
            @Override
            public void onSuccess(UserData response) {
                if (response != null) {
                    loginSucceed(mPhone, mPassword, response);
                } else {
                    onError("", "");
                }
            }
        }.setNeedDialog(true).setNeedToast(true)).sendCheckPhone(mPhone, mCode);
    }

    /**
     * 登录
     *
     * @param account
     * @param psd
     * @param response
     */
    private void loginSucceed(String account, String psd, UserData response) {
        if (verifySlBtn != null) {
            verifySlBtn.setClickable(false);
        }
        toast(MyApp.getLanguageString(getContext(), R.string.login_success));
        SPUtil.setUserPhone(account);
        MobclickAgent.onProfileSignIn(account);
        CommonUtils.setLoginSuccessData(response);
        EventBusUtil.post(new LoginSuccessEvent(response));
        closeDialog();
        startClassWithFlag(MyApp.getLanguageString(this, R.string.MainActivity), null, Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
