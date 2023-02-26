package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.FilePathBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.PickImageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.widgets.LimitInputTextWatcher;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.base.BaseNetUtil.singleTime;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.SEVEN;
import static com.kaiserkalep.constants.Config.TEN;
import static com.kaiserkalep.constants.Config.TWELVE;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 编辑用户资料
 *
 * @Auther: Jack
 * @Date: 2020/12/18 20:50
 * @Description:
 */
public class EditorUserDataActivity extends ZZActivity {

    @BindView(R.id.iv_user_portrait)
    ImageView ivUserPortrait;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.sl_login)
    ShadowLayout slLogin;

    private String title = "";
    private String photoUrl = "";
    private String newNickName = "";

    @Override
    public int getViewId() {
        return R.layout.activity_editoruserdata;
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
        title = MyApp.getLanguageString(getContext(), R.string.editor_user_title);
        commTitle.init(title);
        GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(SPUtil.getUserAvatar()), ivUserPortrait, R.drawable.icon_default_photo);
        String nickName = SPUtil.getNickName();
        if (CommonUtils.StringNotNull(nickName)) {
            etNickname.setText(nickName);
            etNickname.setSelection(nickName.length());//将光标移至文字末尾
        }
        etNickname.addTextChangedListener(new LimitInputTextWatcher(etNickname) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = etNickname.getText();
                if (null != text) {
                    newNickName = text.toString().trim();
                }
                checkData();
            }
        });
    }

    private void checkData() {
        slLogin.setClickable(CommonUtils.StringNotNull(photoUrl) || CommonUtils.StringNotNull(newNickName));
    }

    @OnClick({R.id.iv_user_portrait, R.id.sl_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user_portrait:
                PickImageUtils.selectCirclePic(this);
                break;
            case R.id.sl_login:
                if (CommonUtils.StringNotNull(newNickName)) {
                    if (newNickName.length() <= TEN) {
                        editAfterSave();
                    } else {
                        toast(MyApp.getLanguageString(EditorUserDataActivity.this, R.string.editor_user_nickname_error_tip));
                    }
                } else {
                    editAfterSave();
                }
                break;
        }
    }

    /**
     * 编辑后保存
     */
    private void editAfterSave() {
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                if (CommonUtils.StringNotNull(photoUrl)) {
                    SPUtil.setUserAvatar(photoUrl);
                }
                if (CommonUtils.StringNotNull(newNickName)) {
                    SPUtil.setNickName(newNickName);
                }
                toast(MyApp.getLanguageString(EditorUserDataActivity.this, R.string.System_Revise_Success));
                EditorUserDataActivity.this.finish();
            }

        }.setNeedDialog(true).setNeedToast(true)).setAvatarNickName(photoUrl, newNickName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (CommonUtils.ListNotNull(selectList)) {
                        LocalMedia localMedia = selectList.get(ZERO);
                        if (localMedia != null && CommonUtils.StringNotNull(localMedia.getCutPath())) {
                            long size = localMedia.getSize();
                            if (size <= 1024 * 1024) {
                                getUploadToken(localMedia.getCutPath());
                            } else {
                                toast(MyApp.getLanguageString(getContext(), R.string.addbankactivity_ReceivingQRCode_tip));
                            }
                        } else {
                            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + ".");
                        }
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + "..");
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUploadToken(String Path) {
        new UserImpl(new ZZNetCallBack<FilePathBean>(this, FilePathBean.class) {
            @Override
            public void onSuccess(FilePathBean response) {
                if (null != response) {
                    photoUrl = response.getPath();
                    GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(photoUrl), ivUserPortrait, R.drawable.icon_default_photo);
                    checkData();
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
            }
        }.setNeedDialog(true).setTimeOut(singleTime)).uploadQrCode(ONE_STRING, Path);
    }
}
