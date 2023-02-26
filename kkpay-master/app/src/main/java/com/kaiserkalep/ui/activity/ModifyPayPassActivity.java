package com.kaiserkalep.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.KeyBoardUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.PasswordEditTextView;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class ModifyPayPassActivity extends ZZActivity implements TextWatcher, CleanEditTextView.MyFocusChangeListener {

    @BindView(R.id.et_input_pass_one)
    CleanEditTextView etInputPassOne;
    @BindView(R.id.et_input_pass_two)
    CleanEditTextView etInputPassTwo;
    @BindView(R.id.et_input_pass_thr)
    CleanEditTextView etInputPassThr;
    @BindView(R.id.iv_pass_one_right)
    ImageView ivPassOneRight;
    @BindView(R.id.iv_pass_two_right)
    ImageView ivPassTwoRight;
    @BindView(R.id.iv_pass_thr_right)
    ImageView ivPassThrRight;
    @BindView(R.id.sl_update)
    ShadowLayout slUpdate;

    private String title = "";
    private boolean[] isChangeStatus = {true, true, true};//密码可见标识,登录密码
    /**
     * 输入校验,焦点失去是校验
     */
    private HashMap<EditText, Integer> inputMap = new HashMap<>();
    private ImageView[] passwordIv;
    private EditText[] passwordEt;
    private String strPassOne;
    private String strPassTwo;
    private String strPassThr;

    @Override
    public int getViewId() {
        return R.layout.modifypaypassactivity;
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.mine_update_pay_pass);
        commTitle.init(title);
        initEdit(etInputPassOne, etInputPassTwo, etInputPassThr);
        passwordIv = new ImageView[]{ivPassOneRight, ivPassTwoRight, ivPassThrRight};
        passwordEt = new EditText[]{etInputPassOne, etInputPassTwo, etInputPassThr};

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
     * 校验密码输入框焦点及内容显示明文icon
     */
    private void checkPasswordIv() {
        passwordIv[ZERO].setVisibility(CommonUtils.StringNotNull(strPassOne) ? View.VISIBLE : View.GONE);
        passwordIv[ONE].setVisibility(CommonUtils.StringNotNull(strPassTwo) ? View.VISIBLE : View.GONE);
        passwordIv[TWO].setVisibility(CommonUtils.StringNotNull(strPassThr) ? View.VISIBLE : View.GONE);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable passOneText = etInputPassOne.getText();
        if (null != passOneText) {
            strPassOne = passOneText.toString().trim();
        } else {
            strPassOne = "";
        }
        Editable passTwoText = etInputPassTwo.getText();
        if (null != passTwoText) {
            strPassTwo = passTwoText.toString().trim();
        } else {
            strPassTwo = "";
        }
        Editable passThrText = etInputPassThr.getText();
        if (null != passThrText) {
            strPassThr = passThrText.toString().trim();
        } else {
            strPassThr = "";
        }
        checkInput();
    }

    @Override
    public void afterTextChanged(Editable s) {

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
            case ZERO://旧
                if (CommonUtils.StringNotNull(strPassOne) && strPassOne.length() != Config.SIX) {
                    toast(MyApp.getLanguageString(this, R.string.modify_edit_paypass_error_tip));
                    return;
                }
                break;
            case ONE://新一
                if (CommonUtils.StringNotNull(strPassTwo) && strPassTwo.length() != Config.SIX) {
                    toast(MyApp.getLanguageString(this, R.string.modify_edit_paynewpass_error_tip));
                    return;
                }
            case TWO://新二
                if (CommonUtils.StringNotNull(strPassThr) && strPassThr.length() != Config.SIX) {
                    toast(MyApp.getLanguageString(this, R.string.modify_edit_paynewpass_error_tip));
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void checkInput() {
        checkPasswordIv();
        slUpdate.setClickable(CommonUtils.StringNotNull(strPassOne, strPassTwo, strPassThr) && strPassOne.length() == Config.SIX &&
                strPassTwo.length() == Config.SIX);
    }


    @OnClick({R.id.iv_pass_one_right, R.id.iv_pass_two_right, R.id.iv_pass_thr_right, R.id.sl_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_pass_one_right:
            case R.id.login_iv_pass_right:
                changePwVisible(ZERO);
                break;
            case R.id.iv_pass_two_right:
                changePwVisible(ONE);
                break;
            case R.id.iv_pass_thr_right:
                changePwVisible(TWO);
                break;
            case R.id.sl_update:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                if (!strPassThr.equals(strPassTwo)) {
                    toast(MyApp.getLanguageString(this, R.string.regsiter_DoublePassWord_Error));
                    return;
                }
                hideInput();
                updataPwd();
                break;
        }
    }

    private void updataPwd() {
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(MyApp.getLanguageString(ModifyPayPassActivity.this, R.string.Revise_paypass_success));
                finish();
            }

            @Override
            public void onError(String msg, String code) {
                if (StringConstants.REQUEST_OTHER_ERROR.equals(code)) {//网络异常
                    super.onError(msg, code);
                } else {
                    toast(msg);
                }
            }
        }).updateMemberUpdatePayPwd(strPassOne, strPassTwo, strPassThr);
    }
}
