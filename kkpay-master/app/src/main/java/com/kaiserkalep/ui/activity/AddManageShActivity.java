package com.kaiserkalep.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.AgentBindInfoBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.net.impl.FundImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.king.zxing.Intents;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

/**
 * 添加商户
 */
public class AddManageShActivity extends ZZActivity {

    public static final int REQUEST_CODE_ADD_MANAGE_SCAN = 0X02;

    @BindView(R.id.et_sh_account)
    CleanEditTextView etShAccount;
    @BindView(R.id.sl_btn)
    ShadowLayout slBtn;
    private String title = "";
    private String account = "";

    @Override
    public int getViewId() {
        return R.layout.activity_addmanagesh;
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
        title = MyApp.getLanguageString(getContext(), R.string.addManagesh_view_title);
        commTitle.init(title);
        etShAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = etShAccount.getText();
                if (null != text) {
                    account = text.toString().trim();
                } else {
                    account = "";
                }
                checkParam();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkParam() {
        if (null != slBtn) {
            slBtn.setClickable(CommonUtils.StringNotNull(account));
        }
    }

    @OnClick({R.id.sl_click_scan, R.id.sl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sl_click_scan:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                cameraCallBack();
                break;
            case R.id.sl_btn:
                agentMemberBindInfo(account);
                break;
        }
    }

    /**
     * 根据钱包地址查找代理商信息
     *
     * @param acc
     */
    private void agentMemberBindInfo(String acc) {
        new FundImpl(new ZZNetCallBack<AgentBindInfoBean>(this, AgentBindInfoBean.class) {
            @Override
            public void onSuccess(AgentBindInfoBean response) {
                if (null != response) {
                    if (ONE_STRING.equals(response.getIsBand())) {
                        toast(MyApp.getLanguageString(AddManageShActivity.this, R.string.AddSh_boundAddress_tip));
                    } else {
                        toDetail(response.getId(), response.getWalletAddress());
                    }
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).agentMemberBindInfo(acc);
    }

    /**
     * 跳转商户详情
     */
    private void toDetail(String shId, String address) {
        Intent intent = new Intent(this, AddShAcountDetailActivity.class);
        intent.putExtra(StringConstants.ID, shId);
        intent.putExtra(StringConstants.ADDRESS, address);
        intent.putExtra(StringConstants.TYPE, ZERO_STRING);
        AddManageShActivity.this.startActivity(intent);
        AddManageShActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_MANAGE_SCAN://扫描回调结果
                    String dataStringExtra = data.getStringExtra(Intents.Scan.RESULT);
                    if (null != dataStringExtra && dataStringExtra.contains(Config.SCHEME) && dataStringExtra.contains(Config.SCHEME_SH_HOST) && dataStringExtra.contains(Config.SCHEME_SH_PARAM)) {
                        String result = Config.getUrlValue(dataStringExtra);
                        if (CommonUtils.StringNotNull(result)) {
                            account = result;
                            etShAccount.setEditText(account);
                            etShAccount.setSelection(account.length());//将光标移至文字末尾
                            etShAccount.clearFocus();
                        } else {
                            toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                        }
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                    }
                    checkParam();
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
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(AddManageShActivity.this, R.anim.in, R.anim.out);
        Intent intent = new Intent(AddManageShActivity.this, CameraCaptureActivity.class);
        AddManageShActivity.this.startActivityForResult(intent, REQUEST_CODE_ADD_MANAGE_SCAN, optionsCompat.toBundle());
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
}
