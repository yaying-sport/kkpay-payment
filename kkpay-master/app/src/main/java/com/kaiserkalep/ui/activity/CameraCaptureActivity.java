/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kaiserkalep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.MobclickAgentUtils;
import com.kaiserkalep.utils.PickImageUtils;
import com.kaiserkalep.utils.StatusBarUtil;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.king.zxing.CaptureHelper;
import com.king.zxing.Intents;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.king.zxing.util.QRCodeHelpers;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kaiserkalep.base.BaseApp.getContext;
import static com.kaiserkalep.base.BaseApp.toast;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 自定义扫码：当直接使用CaptureActivity
 * 自定义扫码，切记自定义扫码需在{@link Activity}或者{@link Fragment}相对应的生命周期里面调用{@link #mCaptureHelper}对应的生命周期
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CameraCaptureActivity extends AppCompatActivity implements OnCaptureCallback {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinderView)
    ViewfinderView viewfinderView;
    @BindView(R.id.ivFlash)
    ImageView ivTorch;

    private CaptureHelper mCaptureHelper;
    private String title = "";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.camera_capture_activity);
        ButterKnife.bind(this);

        title = MyApp.getLanguageString(this, R.string.camera_view_title);
        setWhiteStatus();

        surfaceView = findViewById(R.id.surfaceView);
        viewfinderView = findViewById(R.id.viewfinderView);
        ivTorch = findViewById(R.id.ivFlash);
        ivTorch.setVisibility(View.INVISIBLE);

        mCaptureHelper = new CaptureHelper(this, surfaceView, viewfinderView, ivTorch);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
        mCaptureHelper.vibrate(true)
                .fullScreenScan(true)//全屏扫码
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .supportLuminanceInvert(true)//是否支持识别反色码（黑白反色的码），增加识别率
                .continuousScan(false);
    }

    /**
     * 设置白色标题,黑色文字
     */
    protected void setWhiteStatus() {
        StatusBarUtil.setColorForSwipeBack(this, MyApp.getMyColor(R.color.white), 1);
        StatusBarUtil.setOPPOStatusTextColor(true, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mCaptureHelper) {
            mCaptureHelper.onResume();
        }
        MobclickAgentUtils.onPageStart(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mCaptureHelper) {
            mCaptureHelper.onPause();
        }
        MobclickAgentUtils.onPageEnd(title);
    }

    /**
     * 扫码结果回调
     *
     * @param result 扫码结果
     * @return
     */
    @Override
    public boolean onResultCallback(String result) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mCaptureHelper) {
            mCaptureHelper.onDestroy();
        }
    }

    @OnClick({R.id.iv_top_left, R.id.iv_top_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_left:
                finish();
                break;
            case R.id.iv_top_right:
                PickImageUtils.openGallery(CameraCaptureActivity.this, false, ONE, ONE, result -> {
                    if (null != result && result.size() >= ONE) {
                        LocalMedia localMedia = result.get(ZERO);
                        if (localMedia != null && CommonUtils.StringNotNull(localMedia.getCutPath())) {
                            String cutPath = localMedia.getCutPath();
                            //启动线程完成图片扫码
                            new QRCodeHelpers.QrCodeAsyncTask(cutPath, str -> {
                                LogUtils.e("扫描二维码结果str = " + str);
                                if (CommonUtils.StringNotNull(str)) {
                                    CameraCaptureActivity.this.setResult(RESULT_OK, new Intent().putExtra(Intents.Scan.RESULT, str));
                                } else {
                                    toast(MyApp.getLanguageString(getContext(), R.string.QrPhoto_Error_Tip));
                                }
                                CameraCaptureActivity.this.finish();
                            }).execute(cutPath);
                        } else {
                            toast(MyApp.getLanguageString(getContext(), R.string.Share_Error));
                            CameraCaptureActivity.this.finish();
                        }
                    } else {
                        toast(MyApp.getLanguageString(getContext(), R.string.Share_Error) + ".");
                        CameraCaptureActivity.this.finish();
                    }
                });
                break;
        }
    }
}
