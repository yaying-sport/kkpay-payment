package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.CountDownButtonHelper;
import com.kaiserkalep.utils.PhoneFilterNumber;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.CleanEditTextView;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ELEVEN;
import static com.kaiserkalep.constants.Config.SIX;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 手机号码验证
 *
 * @Auther: Jack
 * @Date: 2021/2/18 13:32
 * @Description:
 */
public abstract class PhoneCodeActivity extends ZZActivity implements TextWatcher, CleanEditTextView.MyFocusChangeListener {

    @BindView(R.id.et_phone)
    protected CleanEditTextView etPhone;
    @BindView(R.id.et_code)
    protected CleanEditTextView etCode;
    @BindView(R.id.btn_send_auth_code)
    Button btnSendAuthCode;

    public String mPhone = "";
    public String mCode = "";
    public CountDownButtonHelper countDownButtonHelper;
    private boolean isStart;//计时中


    @Override
    public void afterViews() {
        btnSendAuthCode.setOnClickListener(sendCode);
        String ShareResend = MyApp.getLanguageString(this, R.string.Share_Resend);
        countDownButtonHelper = new CountDownButtonHelper(btnSendAuthCode, ShareResend, "", "S", 60);
        etPhone.setFilters(new InputFilter[]{new PhoneFilterNumber(), new InputFilter.LengthFilter(11)});
        etPhone.addTextChangedListener(this);
        etCode.addTextChangedListener(this);
        initInput(this, R.drawable.icon_clear_wallet_input, etPhone, etCode);
        etCode.setPadding(ZERO);
        setButtonEnabled(false);
        countDownButtonHelper.setOnFinishListener(() -> {
            setBtnStart(false);
            checkPhoneInput();
            if (CommonUtils.StringNotNull(mPhone) && mPhone.length() == ELEVEN) {
                setButtonEnabled(true);
            } else {
                setButtonEnabled(false);
            }
        });
    }

    public void checkPhoneInput() {
        setButtonEnabled(CommonUtils.StringNotNull(mPhone) && mPhone.length() == ELEVEN);
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
        etPhone.setEditText(mPhone);
    }

    public void initInput(TextWatcher watcher, int res, CleanEditTextView... et) {
        if (et == null || et.length <= ZERO) {
            return;
        }
        for (CleanEditTextView cleanEditTextView : et) {
            cleanEditTextView.setCleanDrawable(MyApp.getMyDrawable(res));
            cleanEditTextView.addTextChangedListener(watcher);
        }
    }

    public boolean checkPhone() {
        if (!CommonUtils.simpleMobile(mPhone)) {
            toast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
            return true;
        }
        if (null == mCode || TextUtils.isEmpty(mCode)) {
            toast(MyApp.getLanguageString(this, R.string.verification_code_tip));
            return true;
        }
        return false;
    }

    /**
     * 语音验证码
     */
    public void sendAuthCode() {
        if (!CommonUtils.simpleMobile(getPhone())) {
            toast(MyApp.getLanguageString(this, R.string.login_edit_input_phone_error_tip));
            return;
        }
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                countDownButtonHelper.start();
                setButtonEnabled(false);
                MyApp.postDelayed(() -> setBtnStart(true), 100);
            }
        }).sendAuthCode(getPhone());
    }

    protected abstract String getPhone();

    public void setBtnStart(boolean isStart) {
        this.isStart = isStart;
        ViewGroup.LayoutParams layoutParams = btnSendAuthCode.getLayoutParams();
        layoutParams.width = UIUtils.dip2px(isStart ? 75 : 75);
        btnSendAuthCode.setLayoutParams(layoutParams);
    }

    /**
     * 设置是否可以点击发送
     *
     * @param enable
     */
    public void setButtonEnabled(boolean enable) {
        if (isStart) {
            return;
        }
        btnSendAuthCode.setEnabled(enable);
        btnSendAuthCode.setTextColor(MyApp.getMyColor(enable ? R.color.send_code_text_enabled : R.color.send_code_text_unenabled));
    }

    private View.OnClickListener sendCode = view -> {
        if (view.getId() == R.id.btn_send_auth_code) {
            sendAuthCode();
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable textPhone = etPhone.getText();
        if (null != textPhone) {
            mPhone = textPhone.toString().trim();
        } else {
            mPhone = "";
        }
        Editable textCode = etCode.getText();
        if (null != textCode) {
            mCode = textCode.toString().trim();
        } else {
            mCode = "";
        }
        checkPhoneInput();
        setTextChangedLinstener();
    }

    protected void setTextChangedLinstener() {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
