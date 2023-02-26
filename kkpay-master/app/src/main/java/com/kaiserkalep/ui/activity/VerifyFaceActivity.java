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
    //第一步UI
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

    //第二步UI
    @BindView(R.id.rl_identify_view)
    RelativeLayout rlIdentifyView;
    //第三步UI
    @BindView(R.id.iv_artificial_one)
    ImageView ivArtificialOne;
    @BindView(R.id.tv_artificial_two)
    TextView tvArtificialTwo;
    //提交
    @BindView(R.id.sl_btn)
    ShadowLayout slBtn;
    @BindView(R.id.tv_text)
    TextView tvText;

    //已经实名认证成功
    @BindView(R.id.tsl_verify_success_parent)
    TouchShadowLayout tslVerifySuccessParent;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_no)
    TextView tvNo;


    @BindView(R.id.inputErrorTip_TV)
    TextView inputErrorTip_TV;

    private String title = "";

    private int viewState = 0;//ZERO上传证件照/ONE录制视频/TWO审核中/THREE审核不通过/FOUR审核通过
    private String frontPath = "";//正面
    private String rearPath = "";//背面
    private String realname = "";//姓名
    private String sfzNumberNo = "";//号码
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
     * 获取个人信息
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
     * 上传身份证照片
     *
     * @param type 1正面/2背面
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
     * @param type   1正面/2背面
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
     * @param Path    图片地址
     * @param isFront ZERO正面/ONE背面/TWO视频
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
                //ZERO上传证件照/ONE录制视频/TWO审核中/THREE审核不通过/FOUR审核通过
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
     * * 实名认证 第一步
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
     * * 点击时获取实名随机数
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
     * * 视频上传成功后，提交路径到后台
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
     * 重新审核清空数据
     */
    private void clearData() {
        if (null != frontPath)
            frontPath = "";//正面
        if (null != rearPath)
            rearPath = "";//背面
        if (null != realname)
            realname = "";//姓名
        if (null != sfzNumberNo)
            sfzNumberNo = "";//号码
        if (null != filePathGroup)
            filePathGroup = "";
        if (null != findreadnumbean)
            findreadnumbean = null;
        if (null != mGlobalSetting)
            mGlobalSetting = null;
    }

    /**
     * 效验提交按钮
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
                    case ZERO://上传证件照
                        slBtn.setClickable(
                                CommonUtils.StringNotNull(frontPath, rearPath, realname, sfzNumberNo)
                                        && inputErrorTip_TV.getVisibility() != View.VISIBLE
                                        && sfzNumberNo.length() == 18
                        );
                        break;
                    case ONE://录制视频
                    case THREE://审核失败
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
     * ZERO     上传证件照
     * ONE     录制视频
     * TWO     审核中
     * THREE     审核未通过
     * Four     审核通过
     *
     * @param state
     */
    private void setViewVisibility(int state) {
        int bule = getResources().getColor(R.color.colorPrimary);
        int grey = getResources().getColor(R.color.shape_round_grey);
        switch (state) {
            case ZERO://上传证件照
                viewState = ZERO;
                setViewVisibility(true, llVeriyfIngParent, llSfzViewOne, llSfzViewTwo, slBtn);
                setViewVisibility(false, tslVerifySuccessParent, rlIdentifyView, ivArtificialOne, tvArtificialTwo);
                tvRoundOne.setBackgroundResource(R.drawable.shape_round_bule);
                tvTextOne.setTextColor(bule);
                setTextColor(grey, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_grey, tvRoundTwo, tvRoundThr);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_next_step));
                break;
            case ONE://录制视频
                viewState = ONE;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, ivArtificialOne, tvArtificialTwo);
                setViewVisibility(true, llVeriyfIngParent, rlIdentifyView, slBtn);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo);
                setTextColor(grey, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo);
                tvRoundThr.setBackgroundResource(R.drawable.shape_round_grey);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_open_face));
                break;
            case TWO://审核中
                viewState = TWO;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, rlIdentifyView, slBtn);
                setViewVisibility(true, llVeriyfIngParent, ivArtificialOne, tvArtificialTwo);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo, tvRoundThr);
                tvText.setText("");
                break;
            case THREE://审核失败
                viewState = THREE;
                setViewVisibility(false, tslVerifySuccessParent, llSfzViewOne, llSfzViewTwo, rlIdentifyView);
                setViewVisibility(true, llVeriyfIngParent, ivArtificialOne, tvArtificialTwo, slBtn);
                setTextColor(bule, tvTextOne, tvPointOne, tvTextTwo, tvPointTwo, tvTextThr);
                setTextBackground(R.drawable.shape_round_bule, tvRoundOne, tvRoundTwo, tvRoundThr);
                tvText.setText(MyApp.getLanguageString(this, R.string.Share_again_face));
                break;
            case FOUR://审核通过
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
     * 点击查看图片详情
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
     * 释放资源
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
     * 开始录制
     *
     * @param displayText
     */
    protected void openMain(String displayText) {
        // 拍摄有关设置
        CameraSetting cameraSetting = initCameraSetting(displayText);
        // 全局
        mGlobalSetting = MultiMediaSetting.from(this).choose(MimeType.ofVideo());
        mGlobalSetting.cameraSetting(cameraSetting);
        mGlobalSetting.setOnMainListener(errorMessage -> {
            Toast.makeText(VerifyFaceActivity.this.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        });
        String filepath = getPackageName() + ".fileProvider";
        filePathGroup = filepath + Config.SCHEME;
        mGlobalSetting.allStrategy(new SaveStrategy(true, filepath, Config.SCHEME));// 设置路径和7.0保护路径等等
        mGlobalSetting.videoStrategy(new SaveStrategy(true, filepath, Config.SCHEME));
        mGlobalSetting.audioStrategy(new SaveStrategy(true, filepath, Config.SCHEME));
        mGlobalSetting.maxSelectablePerMediaType(ONE, ONE, ONE, ONE, ZERO, ZERO, ZERO);// 最大5张图片、最大3个视频、最大1个音频

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
     * @return 拍摄设置
     */
    private CameraSetting initCameraSetting(String display) {
        CameraSetting cameraSetting = new CameraSetting();
        cameraSetting.setDisplay(display);
        // 支持的类型：图片，视频
        cameraSetting.mimeTypeSet(MimeType.ofVideo());
        // 最长录制时间
        cameraSetting.duration(11);
        // 最短录制时间限制，单位为毫秒，即是如果长按在1500毫秒内，都暂时不开启录制
        cameraSetting.minDuration(3);
        // 开启高清拍照(失去录像功能)
        cameraSetting.enableImageHighDefinition(false);
        // 开启高清录像(失去拍照功能)
        cameraSetting.enableVideoHighDefinition(false);
        // 开启点击即开启录制(失去点击拍照功能)
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
                .requestEachCombined(Manifest.permission.RECORD_AUDIO,//录音
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储
                        Manifest.permission.CAMERA)//相机
                .subscribe(permission -> {
                    if (permission.granted) {
                        // All permissions are granted !
                        switch (type) {
                            case ZERO_STRING://左边上传身份证 正面
                                openImageDialog(ONE);
                                break;
                            case ONE_STRING://右边上传身份证 背面
                                openImageDialog(TWO);
                                break;
                            case TWO_STRING://录制视频
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

    //点击第一步的下一步
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

    //获取检测所需数据参数1
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

    //拉起刷脸sdk
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
                "1.0.0",//版本号写死1.0.0  否则会报错 “签名校验不通过”
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
        //是否展示刷脸成功页面，默认不展示
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, false);
        //是否展示刷脸失败页面，默认不展示
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, false);
        //颜色设置,sdk内置黑色和白色两种模式，默认白色
        //如果客户想定制自己的皮肤，可以传入WbCloudFaceContant.CUSTOM模式,此时可以配置ui里各种元素的色值
        //定制详情参考app/res/colors.xml文件里各个参数
        data.putString(WbCloudFaceContant.COLOR_MODE, WbCloudFaceContant.WHITE);
        //是否需要录制上传视频 默认不需要
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, false);
        //是否播放提示音，默认不播放
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, false);
        //识别阶段合作方定制提示语,可不传，此处为demo演示
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_LIVE, "");//仅供体验使用 请勿用于投产!
        //上传阶段合作方定制提示语,可不传，此处为demo演示
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_UPLOAD, "");//仅供体验使用 请勿用于投产!
        //合作方长定制提示语，可不传，此处为demo演示
        //如果需要展示长提示语，需要邮件申请
        data.putString(WbCloudFaceContant.CUSTOMER_LONG_TIP, "");//本demo提供的appId仅用于体验，实际生产请使用控制台给您分配的appId！
        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceContant.ID_CRAD
        //仅活体检测  WbCloudFaceContant.NONE
        //默认公安网纹图片比对
        data.putString(WbCloudFaceContant.COMPARE_TYPE, WbCloudFaceContant.ID_CARD);
        //sdk log开关，默认关闭，debug调试sdk问题的时候可以打开
        //【特别注意】上线前请务必关闭sdk log开关！！！
//        data.putBoolean(WbCloudFaceContant.IS_ENABLE_LOG, true);

        Log.e(TAG, "WbCloudFaceVerifySdk initSdk");
        isFaceVerifyInService = true;
        //【特别注意】请使用activity context拉起sdk
        //【特别注意】请在主线程拉起sdk！
        WbCloudFaceVerifySdk.getInstance().initSdk(VerifyFaceActivity.this, data, new WbCloudFaceVerifyLoginListener() {
            @Override
            public void onLoginSuccess() {
                //登录sdk成功
                Log.i(TAG, "onLoginSuccess");
                closeDialog();
                //拉起刷脸页面
                //【特别注意】请使用activity context拉起sdk
                //【特别注意】请在主线程拉起sdk！
                WbCloudFaceVerifySdk.getInstance().startWbFaceVerifySdk(VerifyFaceActivity.this, new WbCloudFaceVerifyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        isFaceVerifyInService = false;
                        closeDialog();
                        //得到刷脸结果
                        if (result != null) {
                            if (result.isSuccess()) {
                                //刷脸成功可以去后台查询刷脸结果
                                Log.e(TAG, "刷脸成功! Sign=" + result.getSign() + "; liveRate=" + result.getLiveRate() +
                                        "; similarity=" + result.getSimilarity() + "userImageString=" + result.getUserImageString());
//                                toast("刷脸成功");
                                //调用后台通知刷脸成功
                                queryfaceRecord(faceParam.getOrderNo());
                            } else {
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    Log.e(TAG, "刷脸失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        //虽然对比失败，但这个domain也可以去后台查询刷脸结果
                                        Log.e(TAG, "对比失败，liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity());
                                    }
                                    //41000 用户取消	回到后台/单击 home/左上角/上传时左上角取消
                                    if (!"41000".equals(error.getCode())) {
                                        if (!TextUtils.isEmpty(error.getDesc())) {
                                            toast("刷脸失败!" + error.getDesc());
                                        } else {
                                            toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "sdk返回error为空！");
                                    toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                                }
                            }
                        } else {
                            Log.e(TAG, "sdk返回结果为空！");
                            toast(UIUtils.getString(R.string.verify_fail_please_try_later));
                        }
                        //测试用代码
                        //不管刷脸成功失败，只要结束了，都更新userId
                        Log.e(TAG, "更新userId");
                        //刷脸结束后，释放资源
                        WbCloudFaceVerifySdk.getInstance().release();
                    }
                });
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                //登录失败
                Log.i(TAG, "onLoginFailed!");
                isFaceVerifyInService = false;
                closeDialog();
                if (error != null) {
                    Log.e(TAG, "登录失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        toast("传入参数有误！" + error.getDesc());
                    } else {
                        toast("登录刷脸sdk失败！" + error.getDesc());
                    }
                } else {
                    Log.e(TAG, "sdk返回error为空！");
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
//                3001 - 匹配度小于70 %
//                3002 - 腾讯云接口返回报错
//                3003 - 请求腾讯云接口失败
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
