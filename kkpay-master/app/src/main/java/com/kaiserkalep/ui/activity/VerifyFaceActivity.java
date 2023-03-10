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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PhotoViewPopuWindow;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.bean.VerifyFaceParam;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.FilePathBean;
import com.kaiserkalep.bean.FindReadNumBean;
import com.kaiserkalep.bean.IdcardInfoData;
import com.kaiserkalep.bean.SimpleDialogData;
import com.kaiserkalep.bean.UserInfoBean;
import com.kaiserkalep.bean.VerifySignParam;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.IdentifyCardValidate;
import com.kaiserkalep.utils.JudgeDoubleUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MyDialogManager;
import com.kaiserkalep.utils.PickImageUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.CleanEditTextView;
import com.kaiserkalep.widgets.CommTitle;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;
import com.kaiserkalep.widgets.shadowLayout.TouchShadowLayout;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.cloud.huiyansdkface.facelight.api.WbCloudFaceContant;
import com.tencent.cloud.huiyansdkface.facelight.api.WbCloudFaceVerifySdk;
import com.tencent.cloud.huiyansdkface.facelight.api.listeners.WbCloudFaceVerifyLoginListener;
import com.tencent.cloud.huiyansdkface.facelight.api.listeners.WbCloudFaceVerifyResultListener;
import com.tencent.cloud.huiyansdkface.facelight.api.result.WbFaceError;
import com.tencent.cloud.huiyansdkface.facelight.api.result.WbFaceVerifyResult;
import com.tencent.cloud.huiyansdkface.facelight.process.FaceVerifyStatus;
import com.tencent.cloud.huiyansdkface.wehttp2.WeLog;
import com.tencent.cloud.huiyansdkface.wehttp2.WeOkHttp;
import com.zhongjh.albumcamerarecorder.AlbumCameraRecorderApi;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.GlobalSetting;
import com.zhongjh.albumcamerarecorder.settings.MultiMediaSetting;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kaiserkalep.base.BaseNetUtil.singleTime;
import static com.kaiserkalep.constants.Config.FOUR;
import static com.kaiserkalep.constants.Config.FOUR_STRING;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.SIX_STRING;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.THREE_STRING;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.TWO_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

public class VerifyFaceActivity extends ZZActivity implements View.OnClickListener, TextWatcher {

    @BindView(R.id.comm_title)
    CommTitle commTitle;
    @BindView(R.id.loading)
    LoadingLayout mLoading;
    @BindView(R.id.ll_veriyf_ing_parent)
    LinearLayout llVeriyfIngParent;
    @BindView(R.id.tv_round_one)
    TextView tvRoundOne;
    @BindView(R.id.tv_text_one)
    TextView tvTextOne;
    @BindView(R.id.tv_point_one)
    TextView tvPointOne;
    @BindView(R.id.tv_round_two)
    TextView tvRoundTwo;
    @BindView(R.id.tv_text_two)
    TextView tvTextTwo;
    @BindView(R.id.tv_point_two)
    TextView tvPointTwo;
    @BindView(R.id.tv_round_thr)
    TextView tvRoundThr;
    @BindView(R.id.tv_text_thr)
    TextView tvTextThr;
    //?????????UI
    @BindView(R.id.ll_sfz_view_one)
    LinearLayout llSfzViewOne;
    @BindView(R.id.ll_sfz_view_two)
    LinearLayout llSfzViewTwo;
    @BindView(R.id.iv_sfzfront_tip)
    ImageView ivSfzfrontTip;
    @BindView(R.id.iv_sfzfront_add_tip)
    ImageView ivSfzfrontAddTip;
    @BindView(R.id.iv_upload_front)
    ImageView ivUploadFront;
    @BindView(R.id.iv_sfzrear_tip)
    ImageView ivSfzrearTip;
    @BindView(R.id.iv_sfzrear_add_tip)
    ImageView ivSfzrearAddTip;
    @BindView(R.id.iv_upload_rear)
    ImageView ivUploadRear;
    @BindView(R.id.et_name)
    CleanEditTextView etName;
    @BindView(R.id.et_number)
    CleanEditTextView etNumber;

    //?????????UI
    @BindView(R.id.rl_identify_view)
    RelativeLayout rlIdentifyView;
    //?????????UI
    @BindView(R.id.iv_artificial_one)
    ImageView ivArtificialOne;
    @BindView(R.id.tv_artificial_two)
    TextView tvArtificialTwo;
    //??????
    @BindView(R.id.sl_btn)
    ShadowLayout slBtn;
    @BindView(R.id.tv_text)
    TextView tvText;

    //????????????????????????
    @BindView(R.id.tsl_verify_success_parent)
    TouchShadowLayout tslVerifySuccessParent;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_no)
    TextView tvNo;


    @BindView(R.id.inputErrorTip_TV)
    TextView inputErrorTip_TV;

    private String title = "";

    private int viewState = 0;//ZERO???????????????/ONE????????????/TWO?????????/THREE???????????????/FOUR????????????
    private String frontPath = "";//??????
    private String rearPath = "";//??????
    private String realname = "";//??????
    private String sfzNumberNo = "";//??????
    private String filePathGroup = "";
    private FindReadNumBean findreadnumbean;

    private PhotoViewPopuWindow photoviewpopuwindow;
    private GlobalSetting mGlobalSetting;


    //


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
    public int getViewId() {
        return R.layout.verifyfaceactivity;
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.Share_VerifyNew);
        commTitle.init(title);
        mLoading.setRetryListener(this);
        mLoading.setErrorImage(R.drawable.no_data);
        etName.addTextChangedListener(this);
        etNumber.addTextChangedListener(this);
        getUserInfo();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 8);
    }


    /**
     * ??????????????????
     */
    private void getUserInfo() {
        new UserImpl(new ZZNetCallBack<UserInfoBean>(this, UserInfoBean.class) {
            @Override
            public void onSuccess(UserInfoBean response) {
                if (null != response) {
                    if (null != mLoading) {
                        mLoading.showContent();
                    }
                    String identityStatus = response.getIdentityStatus();
                    switch (identityStatus) {
                        case ONE_STRING:
                            ivArtificialOne.setBackgroundResource(R.drawable.icon_verify_auditing);
                            tvArtificialTwo.setText(MyApp.getLanguageString(VerifyFaceActivity.this, R.string.face_artificial_ing_tip));
                            setViewVisibility(TWO);
                            break;
                        case TWO_STRING:
                            ivArtificialOne.setBackgroundResource(R.drawable.icon_verify_fail);
                            tvArtificialTwo.setText(MyApp.getLanguageString(VerifyFaceActivity.this, R.string.face_artificial_fail_tip));
                            setViewVisibility(THREE);
                            break;
                        case THREE_STRING:
                            tvName.setText(response.getRealName());
                            tvNo.setText(response.getIdentityId());
                            setViewVisibility(FOUR);
                            break;
                        default:
                            setViewVisibility(ZERO);
                            break;
                    }
                    SPUtil.setUserInfo(response);
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
        }.setNeedDialog(true)).getUserInfo();
    }

    /**
     * ?????????????????????
     *
     * @param type 1??????/2??????
     */
    private void openImageDialog(int type) {
        int color = getResources().getColor(R.color.face_text_bold_color);
        MyDialogManager.getManager().putDialog(MyDialogManager.LEVEL_TYPE.THREE.value, MyDialogManager.SimpleDialog,
                new SimpleDialogData(MyApp.getLanguageString(getContext(), R.string.open_camera), color,
                        MyApp.getLanguageString(getContext(), R.string.open_photo), color,
                        o -> {
                            if (ONE_STRING.equals(o)) {
                                PickImageUtils.openCamera(VerifyFaceActivity.this, 1, new SucceedCallBackListener<List<LocalMedia>>() {
                                    @Override
                                    public void succeedCallBack(@Nullable List<LocalMedia> o) {
                                        showHideView(type, o);
                                    }
                                });
                            } else if (TWO_STRING.equals(o)) {
                                PickImageUtils.openGallery(VerifyFaceActivity.this, 1, null, new SucceedCallBackListener<List<LocalMedia>>() {
                                    @Override
                                    public void succeedCallBack(@Nullable List<LocalMedia> o) {
                                        showHideView(type, o);
                                    }
                                });
                            }
                        }));
    }

    /**
     * @param type   1??????/2??????
     * @param result
     */
    private void showHideView(int type, List<LocalMedia> result) {
        if (null != result && result.size() >= ONE) {
            LocalMedia localMedia = result.get(ZERO);
            if (null != localMedia && CommonUtils.StringNotNull(localMedia.getPath())) {
                String path = localMedia.getCutPath();
                switch (type) {
                    case ONE:
                        getUploadToken(FOUR_STRING, path, ZERO);
                        break;
                    case TWO:
                        getUploadToken(FOUR_STRING, path, ONE);
                        break;
                }
            }
        }
    }

    /**
     * @param Path    ????????????
     * @param isFront ZERO??????/ONE??????/TWO??????
     */
    private void getUploadToken(String type, String Path, int isFront) {
        new UserImpl(new ZZNetCallBack<FilePathBean>(this, FilePathBean.class) {
            @Override
            public void onSuccess(FilePathBean response) {
                if (null != response) {
                    String path = response.getPath();
                    switch (isFront) {
                        case ZERO:
                            frontPath = path;
                            ivSfzfrontTip.setVisibility(View.GONE);
                            ivSfzfrontAddTip.setVisibility(View.GONE);
                            ivUploadFront.setVisibility(View.VISIBLE);
                            GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(frontPath), ivUploadFront);
                            break;
                        case ONE:
                            rearPath = path;
                            ivSfzrearTip.setVisibility(View.GONE);
                            ivSfzrearAddTip.setVisibility(View.GONE);
                            ivUploadRear.setVisibility(View.VISIBLE);
                            GlideUtil.loadLocalImage(BaseNetUtil.jointUrl(rearPath), ivUploadRear);
                            break;
                        case TWO:
                            updateVideo(path);
                            break;
                    }
                    checkBtn();
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
//                slBtn.setClickable(true);
            }
        }.setNeedDialog(true).setTimeOut(singleTime)).uploadQrCode(type, Path);
    }

    @OnClick({R.id.iv_sfzfront_tip, R.id.iv_sfzfront_add_tip, R.id.iv_upload_front, R.id.iv_sfzrear_tip, R.id.iv_sfzrear_add_tip, R.id.iv_upload_rear, R.id.sl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retry_button:
                getUserInfo();
                break;
            case R.id.iv_sfzfront_tip:
            case R.id.iv_sfzfront_add_tip:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getPermission(ZERO_STRING);
                break;
            case R.id.iv_upload_front:
                if (CommonUtils.StringNotNull(frontPath)) {
//                    showPhotoViewDialog(BaseNetUtil.jointUrl(frontPath));
                    HashMap<String, String> params = new HashMap<>();
                    params.put(StringConstants.TITLE, MyApp.getLanguageString(this, R.string.identity_card_front));
                    params.put(StringConstants.DATA, frontPath);
                    params.put(StringConstants.DELETE, "1");
                    startClassForResult(UIUtils.getString(R.string.PhotoDetailActivity), params, 100);
                }
                break;
            case R.id.iv_sfzrear_tip:
            case R.id.iv_sfzrear_add_tip:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                getPermission(ONE_STRING);
                break;
            case R.id.iv_upload_rear:
                if (CommonUtils.StringNotNull(rearPath)) {
//                    showPhotoViewDialog(BaseNetUtil.jointUrl(rearPath));
                    HashMap<String, String> params = new HashMap<>();
                    params.put(StringConstants.TITLE, MyApp.getLanguageString(this, R.string.identity_card_back));
                    params.put(StringConstants.DATA, rearPath);
                    params.put(StringConstants.DELETE, "1");
                    startClassForResult(UIUtils.getString(R.string.PhotoDetailActivity), params, 200);
                }
                break;
            case R.id.sl_btn:
                if (JudgeDoubleUtils.isDoubleClick()) {
                    return;
                }
                //ZERO???????????????/ONE????????????/TWO?????????/THREE???????????????/FOUR????????????
                if (ZERO == viewState) {
//                    uploadidentifyid();
                    clickNext1();
                } else if (ONE == viewState) {
                    getPermission(TWO_STRING);
                } else if (THREE == viewState) {
                    clearData();
                    setViewVisibility(ZERO);
                }
                break;
        }
    }


    /**
     * * ???????????? ?????????
     */
    private void uploadidentifyid() {
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                setViewVisibility(ONE);
            }

        }.setNeedDialog(true).setNeedToast(true)).uploadidentifyid(realname, sfzNumberNo, frontPath, rearPath);
    }

    /**
     * * ??????????????????????????????
     */
    private void getFindReadNum() {
        new UserImpl(new ZZNetCallBack<FindReadNumBean>(this, FindReadNumBean.class) {

            @Override
            public void onSuccess(FindReadNumBean response) {
                if (null != response) {
                    VerifyFaceActivity.this.findreadnumbean = response;
                    openMain(response.getNum());
                }
            }

        }.setNeedDialog(true).setNeedToast(true)).getFindReadNum();
    }

    /**
     * * ?????????????????????????????????????????????
     */
    private void updateVideo(String videoPath) {
        if (null != findreadnumbean && CommonUtils.StringNotNull(videoPath)) {
            String numId = findreadnumbean.getNumId();
            new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {

                @Override
                public void onSuccess(Object response) {
                    Context context = VerifyFaceActivity.this.getContext();
                    if (null != context) {
                        ivArtificialOne.setBackgroundResource(R.drawable.icon_verify_auditing);
                        tvArtificialTwo.setText(MyApp.getLanguageString(VerifyFaceActivity.this, R.string.face_artificial_ing_tip));
                        setViewVisibility(TWO);
                    }
                }

            }.setNeedDialog(true).setNeedToast(true)).setUpdateVideo(numId, videoPath);
        }
    }

    /**
     * ????????????????????????
     */
    private void clearData() {
        if (null != frontPath)
            frontPath = "";//??????
        if (null != rearPath)
            rearPath = "";//??????
        if (null != realname)
            realname = "";//??????
        if (null != sfzNumberNo)
            sfzNumberNo = "";//??????
        if (null != filePathGroup)
            filePathGroup = "";
        if (null != findreadnumbean)
            findreadnumbean = null;
        if (null != mGlobalSetting)
            mGlobalSetting = null;
    }

    /**
     * ??????????????????
     */
    private void checkBtn() {
        firstSteepSuccess = false;
        if (sfzNumberNo != null && sfzNumberNo.length() == 18) {
            if (sfzNumberNo.contains("x")) {
                sfzNumberNo = sfzNumberNo.replace('x', 'X');
            }
            IdentifyCardValidate vali = new IdentifyCardValidate();
            String msg = vali.validate_effective(sfzNumberNo);
            if (msg.equals(sfzNumberNo)) {
                inputErrorTip_TV.setVisibility(View.GONE);
            } else {
                inputErrorTip_TV.setVisibility(View.VISIBLE);
            }
        } else {
            inputErrorTip_TV.setVisibility(View.GONE);
        }
        MyApp.getMainHandler().postDelayed(() -> {
            if (null != slBtn) {
                switch (viewState) {
                    case ZERO://???????????????
                        slBtn.setClickable(
                                CommonUtils.StringNotNull(frontPath, rearPath, realname, sfzNumberNo)
                                        && inputErrorTip_TV.getVisibility() != View.VISIBLE
                                        && sfzNumberNo.length() == 18
                        );
                        break;
                    case ONE://????????????
                    case THREE://????????????
                        slBtn.setClickable(true);
                        break;
                    default:
                        slBtn.setClickable(false);
                        break;
                }
            }
        }, 100);
    }

    /**
     * ZERO     ???????????????
     * ONE     ????????????
     * TWO     ?????????
     * THREE     ???????????????
     * Four     ????????????
     *
     * @param state
     */
    private void setViewVisibility(int state) {
        int bule = getResources().getColor(R.color.colorPrimary);
        int grey = getResources().getColor(R.color.shape_round_grey);
        switch (state) {
            case ZERO://???????????????
                viewState = ZERO;
                setViewVisibility(true, llVeriyfIngParent, llSfzViewOne, llSfzViewTwo, slBtn);
                setViewVisibility(false, tslVerifySuccessParent, rlIdentifyView, ivArtificialOne, tvArtificialTwo);
                tvRoundOne.setBackgroundResource(R.drawable.shape_round_bule);
                tvTextOne.setTextColor(bule);
                setTextColor(grey, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_grey, tvRoundTwo, tvRoundThr);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_next_step));
                break;
            case ONE://????????????
                viewState = ONE;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, ivArtificialOne, tvArtificialTwo);
                setViewVisibility(true, llVeriyfIngParent, rlIdentifyView, slBtn);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo);
                setTextColor(grey, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo);
                tvRoundThr.setBackgroundResource(R.drawable.shape_round_grey);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_open_face));
                break;
            case TWO://?????????
                viewState = TWO;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, rlIdentifyView, slBtn);
                setViewVisibility(true, llVeriyfIngParent, ivArtificialOne, tvArtificialTwo);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo, tvRoundThr);
                tvText.setText("");
                break;
            case THREE://????????????
                viewState = THREE;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, rlIdentifyView);
                setViewVisibility(true, llVeriyfIngParent, ivArtificialOne, tvArtificialTwo, slBtn);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo, tvRoundThr);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_again_face));
                break;
            case FOUR://????????????
                viewState = FOUR;
                llVeriyfIngParent.setVisibility(View.GONE);
                tslVerifySuccessParent.setVisibility(View.VISIBLE);
                break;
        }
        checkBtn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                clearPhoto(0);
            } else if (requestCode == 200) {
                clearPhoto(1);
            }
        }
    }

    void clearPhoto(int i) {
        if (i == 0) {
            frontPath = "";
            ivSfzfrontTip.setVisibility(View.VISIBLE);
            ivSfzfrontAddTip.setVisibility(View.VISIBLE);
            ivUploadFront.setVisibility(View.GONE);
        } else if (i == 1) {
            rearPath = "";
            ivSfzrearTip.setVisibility(View.VISIBLE);
            ivSfzrearAddTip.setVisibility(View.VISIBLE);
            ivUploadRear.setVisibility(View.GONE);
        }
        checkBtn();
    }

    /**
     * ????????????????????????
     */
    private void showPhotoViewDialog(String imgUrl) {
        if (photoviewpopuwindow == null) {
            photoviewpopuwindow = new PhotoViewPopuWindow(getActivity());
        }
        if (photoviewpopuwindow.isShowing()) {
            photoviewpopuwindow.dismiss();
        } else {
            View parent = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
            photoviewpopuwindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        }
        photoviewpopuwindow.showImage(false, imgUrl);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable nameText = etName.getText();
        if (null != nameText) {
            realname = nameText.toString().trim();
        } else {
            realname = "";
        }
        Editable numberText = etNumber.getText();
        if (null != numberText) {
            sfzNumberNo = numberText.toString().trim();
        } else {
            sfzNumberNo = "";
        }

        checkBtn();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    /**
     * ????????????
     */
    public void release() {
        if (null != photoviewpopuwindow) {
            photoviewpopuwindow.release();
        }
        if (mGlobalSetting != null) {
            mGlobalSetting.onDestroy();
        }

        AlbumCameraRecorderApi.deleteCacheDirFile(getApplication());
    }

    /**
     * ????????????
     *
     * @param displayText
     */
    protected void openMain(String displayText) {
        // ??????????????????
        CameraSetting cameraSetting = initCameraSetting(displayText);
        // ??????
        mGlobalSetting = MultiMediaSetting.from(this).choose(MimeType.ofVideo());
        mGlobalSetting.cameraSetting(cameraSetting);
        mGlobalSetting.setOnMainListener(errorMessage -> {
            Toast.makeText(VerifyFaceActivity.this.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        });
        String filepath = getPackageName() + ".fileProvider";
        filePathGroup = filepath + Config.SCHEME;
        mGlobalSetting.allStrategy(new SaveStrategy(true, filepath, Config.SCHEME));// ???????????????7.0??????????????????
        mGlobalSetting.videoStrategy(new SaveStrategy(true, filepath, Config.SCHEME));
        mGlobalSetting.audioStrategy(new SaveStrategy(true, filepath, Config.SCHEME));
        mGlobalSetting.maxSelectablePerMediaType(ONE, ONE, ONE, ONE, ZERO, ZERO, ZERO);// ??????5??????????????????3??????????????????1?????????

        mGlobalSetting.forResult(new OnResultCallbackListener() {
            @Override
            public void onResult(List<LocalFile> result) {
                if (null != result && result.size() >= ONE) {
                    String path = result.get(ZERO).getPath();
                    slBtn.setClickable(false);
                    getUploadToken(SIX_STRING, path, TWO);
                }
            }

            @Override
            public void onResultFromPreview(List<MultiMedia> result, boolean apply) {
//                Log.e("answer", "onResultFromPreview" + result);
            }
        });
    }

    /**
     * @return ????????????
     */
    private CameraSetting initCameraSetting(String display) {
        CameraSetting cameraSetting = new CameraSetting();
        cameraSetting.setDisplay(display);
        // ?????????????????????????????????
        cameraSetting.mimeTypeSet(MimeType.ofVideo());
        // ??????????????????
        cameraSetting.duration(11);
        // ??????????????????????????????????????????????????????????????????1500????????????????????????????????????
        cameraSetting.minDuration(3);
        // ??????????????????(??????????????????)
        cameraSetting.enableImageHighDefinition(false);
        // ??????????????????(??????????????????)
        cameraSetting.enableVideoHighDefinition(false);
        // ???????????????????????????(????????????????????????)
        cameraSetting.isClickRecord(true);
        return cameraSetting;
    }

    private void setTextColor(int textcolor, TextView... textViews) {
        if (null != textViews && textViews.length > ZERO) {
            for (int i = 0; i < textViews.length; i++) {
                TextView view = textViews[i];
                if (null != view) {
                    view.setTextColor(textcolor);
                }
            }
        }
    }

    private void setTextBackground(int background, TextView... textViews) {
        if (null != textViews && textViews.length > ZERO) {
            for (int i = 0; i < textViews.length; i++) {
                TextView view = textViews[i];
                if (null != view) {
                    view.setBackgroundResource(background);
                }
            }
        }
    }

    private void setViewVisibility(boolean visibility, View... views) {
        if (null != views && views.length > ZERO) {
            for (int i = 0; i < views.length; i++) {
                View view = views[i];
                if (null != view) {
                    view.setVisibility(visibility ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void getPermission(String type) {
        String tipAudioCaneraMsg = MyApp.getLanguageString(getContext(), R.string.Rationale_Audio_Canera_Msg);
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.RECORD_AUDIO,//??????
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//??????
                        Manifest.permission.CAMERA)//??????
                .subscribe(permission -> {
                    if (permission.granted) {
                        // All permissions are granted !
                        switch (type) {
                            case ZERO_STRING://????????????????????? ??????
                                openImageDialog(ONE);
                                break;
                            case ONE_STRING://????????????????????? ??????
                                openImageDialog(TWO);
                                break;
                            case TWO_STRING://????????????
                                getFindReadNum();
                                break;
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        toast(tipAudioCaneraMsg);
                    } else {
                        showRejectDialog(tipAudioCaneraMsg);
                    }
                });

    }

    private void showRejectDialog(String msg) {
        Context context = getContext();
        if (null != context) {
            String tipAudioCaneraTitle = MyApp.getLanguageString(context, R.string.Rationale_Audio_Canera_title);
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


    boolean firstSteepSuccess = false;
    int errorCount = 0;

    //???????????????????????????
    void clickNext1() {
        if (isFaceVerifyInService) {
            return;
        }
        errorCount = 0;
        showDialog();
//        if (!firstSteepSuccess) {
//            getIdCardInfo(frontPath, rearPath);
//        } else {
//            getVerifyFaceParam();
//        }
        getVerifyFaceParam();
    }

    public void getIdCardInfo(String frontUrl, String revUrl) {
//        url = "https://kkpay11.com/s3/ID/2022/04/30/a4cf43af-06d2-4c17-b8a6-f6d830811be6.jpeg";
        new UserImpl(new ZZNetCallBack<IdcardInfoData>(this, IdcardInfoData.class) {
            @Override
            public void onSuccess(IdcardInfoData response) {
                errorCount = 0;
                boolean success = false;
                if (null != response) {
                    if (response.getSim() >= 70) {
                        if (!response.getName().equals(realname) &&
                                !response.getIdNum().equals(sfzNumberNo)) {
                            toast(UIUtils.getString(R.string.identity_info_and_input_mismatch));
                        } else if (!response.getName().equals(realname)) {
                            toast(UIUtils.getString(R.string.please_fill_your_real_name));
                        } else if (!response.getIdNum().equals(sfzNumberNo)) {
                            toast(UIUtils.getString(R.string.please_fill_your_real_id_card_no));
                        } else {
                            success = true;
                            firstSteepSuccess = true;
                            getVerifyFaceParam();
                        }
                    } else {
                        if (!TextUtils.isEmpty(response.getDescription())) {
                            toast(response.getDescription());
                        } else {
                            toast(UIUtils.getString(R.string.identity_info_and_portrait_mismatch));
                        }
                    }
                }
                if (!success) {
                    VerifyFaceActivity.this.closeDialog();
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                VerifyFaceActivity.this.closeDialog();
            }
        }.setNeedDialog(false)).getIdCardInfo(frontUrl, revUrl);
    }

    //??????????????????????????????1
    private void getVerifyFaceParam() {
        new UserImpl(new ZZNetCallBack<VerifyFaceParam>(this, VerifyFaceParam.class) {
            @Override
            public void onSuccess(VerifyFaceParam response) {
                if (null != response) {
                    getVerifySignParam(response);
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                VerifyFaceActivity.this.closeDialog();
            }
        }.setNeedDialog(false)).getFaceParam(realname, sfzNumberNo, frontPath, rearPath);
    }

    private void getVerifySignParam(VerifyFaceParam faceParam) {
        new UserImpl(new ZZNetCallBack<VerifySignParam>(this, VerifySignParam.class) {
            @Override
            public void onSuccess(VerifySignParam response) {
                if (null != response) {
                    openCloudFaceService(faceParam, response);
                }
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                VerifyFaceActivity.this.closeDialog();
            }
        }.setNeedDialog(false)).getSignParam();
    }

    boolean isFaceVerifyInService = false;

    //????????????sdk
    public void openCloudFaceService(VerifyFaceParam faceParam, VerifySignParam signParam) {
        Log.e(TAG, "openCloudFaceService");
//        if (MyApp.isDebug) {
//            keyLicence = UIUtils.getString(R.string.verify_face_license_debug);
//        } else {
//            keyLicence = UIUtils.getString(R.string.verify_face_license_release);
//        }

        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                faceParam.getFaceId(),
                faceParam.getOrderNo(),
                signParam.getAppid(),
                "1.0.0",//???????????????1.0.0  ??????????????? ???????????????????????????
                signParam.getNonce(),
                SPUtil.getUserId(),
                signParam.getSign(),
                FaceVerifyStatus.Mode.GRADE,
                signParam.getLicence()
        );


        LogUtils.e("VerifySdkParam", "param.getFaceId()  " + faceParam.getFaceId());
        LogUtils.e("VerifySdkParam", "param.getOrderNo()  " + faceParam.getOrderNo());
        LogUtils.e("VerifySdkParam", "param.getNonce()  " + signParam.getNonce());
        LogUtils.e("VerifySdkParam", "param.getSign()  " + signParam.getSign());
        LogUtils.e("VerifySdkParam", "param.getAppid()  " + signParam.getAppid());
        LogUtils.e("VerifySdkParam", "ClientInfo.getVersionCode" + ClientInfo.getVersionCode(getContext()));
        LogUtils.e("VerifySdkParam", "SPUtil.getUserId()  " + SPUtil.getUserId());
        LogUtils.e("VerifySdkParam", "FaceVerifyStatus.Mode.GRADE  " + FaceVerifyStatus.Mode.GRADE);
        LogUtils.e("VerifySdkParam", "param.getLicence()  " + signParam.getLicence());

        data.putSerializable(WbCloudFaceContant.INPUT_DATA, inputData);
        data.putString(WbCloudFaceContant.LANGUAGE, WbCloudFaceContant.LANGUAGE_ZH_CN);
        //????????????????????????????????????????????????
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, false);
        //????????????????????????????????????????????????
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, false);
        //????????????,sdk????????????????????????????????????????????????
        //???????????????????????????????????????????????????WbCloudFaceContant.CUSTOM??????,??????????????????ui????????????????????????
        //??????????????????app/res/colors.xml?????????????????????
        data.putString(WbCloudFaceContant.COLOR_MODE, WbCloudFaceContant.WHITE);
        //?????????????????????????????? ???????????????
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, false);
        //???????????????????????????????????????
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, false);
        //????????????????????????????????????,?????????????????????demo??????
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_LIVE, "");//?????????????????? ??????????????????!
        //????????????????????????????????????,?????????????????????demo??????
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_UPLOAD, "");//?????????????????? ??????????????????!
        //???????????????????????????????????????????????????demo??????
        //???????????????????????????????????????????????????
        data.putString(WbCloudFaceContant.CUSTOMER_LONG_TIP, "");//???demo?????????appId???????????????????????????????????????????????????????????????appId???
        //???????????????????????????  ?????????????????????????????????
        //???????????????????????? WbCloudFaceContant.ID_CRAD
        //???????????????  WbCloudFaceContant.NONE
        //??????????????????????????????
        data.putString(WbCloudFaceContant.COMPARE_TYPE, WbCloudFaceContant.ID_CARD);
        //sdk log????????????????????????debug??????sdk???????????????????????????
        //??????????????????????????????????????????sdk log???????????????
//        data.putBoolean(WbCloudFaceContant.IS_ENABLE_LOG, true);

        Log.e(TAG, "WbCloudFaceVerifySdk initSdk");
        isFaceVerifyInService = true;
        //???????????????????????????activity context??????sdk
        //???????????????????????????????????????sdk???
        WbCloudFaceVerifySdk.getInstance().initSdk(VerifyFaceActivity.this, data, new WbCloudFaceVerifyLoginListener() {
            @Override
            public void onLoginSuccess() {
                //??????sdk??????
                Log.i(TAG, "onLoginSuccess");
                closeDialog();
                //??????????????????
                //???????????????????????????activity context??????sdk
                //???????????????????????????????????????sdk???
                WbCloudFaceVerifySdk.getInstance().startWbFaceVerifySdk(VerifyFaceActivity.this, new WbCloudFaceVerifyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        isFaceVerifyInService = false;
                        closeDialog();
                        //??????????????????
                        if (result != null) {
                            if (result.isSuccess()) {
                                //?????????????????????????????????????????????
                                Log.e(TAG, "????????????! Sign=" + result.getSign() + "; liveRate=" + result.getLiveRate() +
                                        "; similarity=" + result.getSimilarity() + "userImageString=" + result.getUserImageString());
//                                toast("????????????");
                                //??????????????????????????????
                                queryfaceRecord(faceParam.getOrderNo());
                            } else {
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    Log.e(TAG, "???????????????domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        //??????????????????????????????domain????????????????????????????????????
                                        Log.e(TAG, "???????????????liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity());
                                    }
                                    //41000 ????????????	????????????/?????? home/?????????/????????????????????????
                                    if (!"41000".equals(error.getCode())) {
                                        if (!TextUtils.isEmpty(error.getDesc())) {
                                            toast("????????????!" + error.getDesc());
                                        } else {
                                            toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "sdk??????error?????????");
                                    toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                                }
                            }
                        } else {
                            Log.e(TAG, "sdk?????????????????????");
                            toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                        }
                        //???????????????
                        //??????????????????????????????????????????????????????userId
                        Log.e(TAG, "??????userId");
                        //??????????????????????????????
                        WbCloudFaceVerifySdk.getInstance().release();
                    }
                });
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                //????????????
                Log.i(TAG, "onLoginFailed!");
                isFaceVerifyInService = false;
                closeDialog();
                if (error != null) {
                    Log.e(TAG, "???????????????domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        toast("?????????????????????" + error.getDesc());
                    } else {
                        toast("????????????sdk?????????" + error.getDesc());
                    }
                } else {
                    Log.e(TAG, "sdk??????error?????????");
                }
            }
        });
    }

    ///app/identify/queryfacerecord
    private void queryfaceRecord(String orderNo) {
        new UserImpl(new ZZNetCallBack<Object>(this, Object.class) {
            @Override
            public void onSuccess(Object response) {
                toast(UIUtils.getString(R.string.identity_verify_success));
                finish();
            }

            @Override
            public void onError(String msg, String code) {
                super.onError(msg, code);
                VerifyFaceActivity.this.closeDialog();
//                3001 - ???????????????70 %
//                3002 - ???????????????????????????
//                3003 - ???????????????????????????
                if ("3002".equals(code)
                        || "3003".equals(code)) {
                    if (errorCount < 5) {
                        errorCount++;
                        MyApp.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showDialog();
                                queryfaceRecord(orderNo);
                            }
                        }, 1000);
                    } else {
                        toast(msg);
                    }
                } else if ("3001".equals(code)) {
                    toast(UIUtils.getString(R.string.identity_info_and_portrait_mismatch));
                } else {
                    toast(msg);
                }
            }
        }.setNeedDialog(true).setNeedToast(false)).queryFaceRecord(orderNo);
    }
}
