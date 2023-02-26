package com.kaiserkalep.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.ui.activity.VerifyFaceActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.kaiserkalep.R;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tencent.tinker.loader.TinkerRuntimeException;

import java.util.List;

import static com.kaiserkalep.constants.Config.NINE;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.SIXTEEN;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;


/**
 * @Auther: Administrator
 * @Date: 2019/3/23 0023 23:22
 * @Description:
 */
public class PickImageUtils {


    public static final int maxSelectNumber = 9;
    private static int maxSelectNum = maxSelectNumber;
    private static int themeId = R.style.picture_default_style;

    /**
     * 默认单选,长方形375*203pt\dp
     *
     * @param activity
     */
    public static void selectPic(Activity activity) {
        setConfig(activity, null, PictureConfig.SINGLE, false, true);
    }

    public static void selectCirclePic(Activity activity) {
        setConfig(activity, null, PictureConfig.SINGLE, true, true);
    }

    /**
     * 默认单选,长方形375*203pt\dp
     *
     * @param activity
     */
    public static void selectqRCodePic(Activity activity, boolean isSrcop) {
        setConfig(activity, null, PictureConfig.SINGLE, false, isSrcop);
    }

    /**
     * @param activity
     * @param selectList    多选时复显选中
     * @param selectionMode 单选or多选
     * @param isCircle      是否圆形
     */
    public static void setConfig(Activity activity, List<LocalMedia> selectList, int selectionMode, boolean isCircle, boolean isCrop) {
        MyDataManager.setPermission(activity, o -> {
            if (o == null || !o) {
                return;
            }
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(activity)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .closeAndroidQChangeWH(true)
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(selectionMode)// 多选 or 单选
                    .isPreviewImage(true)// 是否可预览图片
                    .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
//                    .previewVideo(cb_preview_video.isChecked())// 是否可预览视频
//                    .enablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .isEnableCrop(isCrop)// 是否裁剪
                    .isCompress(true)// 是否压缩
                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)// 列表动画效果
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                .glideOverride(240, 240)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .withAspectRatio(isCircle ? 1 : 1, isCircle ? 1 : 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
//                .isGif(MyApp.isDebug)// 是否显示gif图片
                    .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                    .circleDimmedLayer(isCircle)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                    .openClickSound(cb_voice.isChecked())// 是否开启点击声音
                    .selectionData(selectList)// 是否传入已选图片
                    .isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                    .isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
//                    .cutOutQuality(90)// 裁剪压缩质量 默认100
                    .minimumCompressSize(200)// 小于200kb的图片不压缩
//                    .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    .rotateEnabled(false) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//竖屏
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        });
    }

    /**
     * 打开相机
     */
    public static void openCamera(Activity activity, boolean isCircle, int ratioX, int ratioY, SucceedCallBackListener<List<LocalMedia>> succeedCallBackListener) {
        if (null != activity) {
            MyDataManager.setPermission(activity, o -> {
                if (o == null || !o) {
                    return;
                }
                PictureSelector.create(activity)
                        .openCamera(PictureMimeType.ofImage())
                        .theme(R.style.picture_default_style)
                        .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .isPreviewImage(true)// 是否可预览图片
                        .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .closeAndroidQChangeWH(true)
                        .imageSpanCount(THREE)// 每行显示个数
                        .isCompress(true)// 是否压缩
                        .isEnableCrop(true)// 是否裁剪
                        .withAspectRatio(isCircle ? ratioX : ratioX, isCircle ? ratioY : ratioY)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                        .circleDimmedLayer(isCircle)// 是否圆形裁剪
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .rotateEnabled(false) // 裁剪是否可旋转图片
                        .showCropFrame(!isCircle)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(200)// 小于200kb的图片不压缩
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(ONE)// 最小选择数量// 最大图片选择数量
                        .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)// 列表动画效果
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//竖屏
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                if (null != succeedCallBackListener) {
                                    succeedCallBackListener.succeedCallBack(result);
                                }
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            });
        }

    }

    // type 1 身份证
    public static void openCamera(Activity activity, int type, SucceedCallBackListener<List<LocalMedia>> succeedCallBackListener) {
        if (null != activity) {
            MyDataManager.setPermission(activity, o -> {
                if (o == null || !o) {
                    return;
                }
                boolean isCircle = false, rotateEnabled = false;
                int ratioX = 0, ratioY = 0;
                if (type == 1) {
                    ratioX = THREE;
                    ratioY = TWO;
                    rotateEnabled = true;
                }
                PictureSelector.create(activity)
                        .openCamera(PictureMimeType.ofImage())
                        .theme(R.style.picture_default_style)
                        .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                        .isPreviewImage(true)// 是否可预览图片
                        .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .closeAndroidQChangeWH(true)
                        .imageSpanCount(THREE)// 每行显示个数
                        .isCompress(true)// 是否压缩
                        .isEnableCrop(true)// 是否裁剪
                        .withAspectRatio(isCircle ? ratioX : ratioX, isCircle ? ratioY : ratioY)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                        .circleDimmedLayer(isCircle)// 是否圆形裁剪
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .rotateEnabled(rotateEnabled) // 裁剪是否可旋转图片
                        .showCropFrame(!isCircle)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(200)// 小于200kb的图片不压缩
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(ONE)// 最小选择数量// 最大图片选择数量
                        .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)// 列表动画效果
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//竖屏
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                if (null != succeedCallBackListener) {
                                    succeedCallBackListener.succeedCallBack(result);
                                }
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            });
        }

    }


    public static void openGallery(Activity activity, boolean isCamera, int ratioX, int ratioY, SucceedCallBackListener<List<LocalMedia>> succeedCallBackListener) {
        openGallery(activity, isCamera, false, ratioX, ratioY, 1, true, null, succeedCallBackListener);
    }

    /**
     * 打开图库
     */
    public static void openGallery(Activity activity, boolean isCamera, boolean isCircle, int ratioX, int ratioY, int maxSelectNum, boolean isEnableCrop, List<LocalMedia> selectionData, SucceedCallBackListener<List<LocalMedia>> succeedCallBackListener) {
        if (null != activity) {
            MyDataManager.setPermission(activity, o -> {
                if (o == null || !o) {
                    return;
                }
                PictureSelector.create(activity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_default_style)
                        .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                        .selectionMode(maxSelectNum > 1 ? PictureConfig.MULTIPLE : PictureConfig.SINGLE)// 多选 or 单选
                        .isPreviewImage(true)// 是否可预览图片
                        .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
                        .isCamera(isCamera)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .closeAndroidQChangeWH(true)
                        .imageSpanCount(THREE)// 每行显示个数
                        .isCompress(true)// 是否压缩
                        .isEnableCrop(isEnableCrop)// 是否裁剪
                        .withAspectRatio(isCircle ? ratioX : ratioX, isCircle ? ratioY : ratioY)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                        .circleDimmedLayer(isCircle)// 是否圆形裁剪
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .rotateEnabled(false) // 裁剪是否可旋转图片
                        .showCropFrame((isEnableCrop && !isCircle))// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(200)// 小于200kb的图片不压缩
//                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
//                        .minSelectNum(ONE)// 最小选择数量// 最大图片选择数量
                        .selectionData(selectionData)
                        .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)// 列表动画效果
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//竖屏
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                if (null != succeedCallBackListener) {
                                    succeedCallBackListener.succeedCallBack(result);
                                }
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            });
        }
    }


    //type 1 身份证   2 付款证明
    public static void openGallery(Activity activity, int type, List<LocalMedia> selectionData, SucceedCallBackListener<List<LocalMedia>> succeedCallBackListener) {
        if (null != activity) {
            MyDataManager.setPermission(activity, o -> {
                if (o == null || !o) {
                    return;
                }
                boolean isCamera = true;
                boolean isEnableCrop = true;
                boolean isCircle = false;
                boolean rotateEnabled = false;
                int ratioX = 0;
                int ratioY = 0;
                int maxSelectNum = 1;
                if (type == 1) {//身份证
                    ratioX = THREE;
                    ratioY = TWO;
                    rotateEnabled = true;
                } else if (type == 2) {//付款证明
                    ratioX = NINE;
                    ratioY = SIXTEEN;
                    isEnableCrop = false;
                    maxSelectNum = 3;
                }
                PictureSelector.create(activity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_default_style)
                        .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                        .selectionMode(maxSelectNum > 1 ? PictureConfig.MULTIPLE : PictureConfig.SINGLE)// 多选 or 单选
                        .isPreviewImage(true)// 是否可预览图片
                        .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
                        .isCamera(isCamera)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .closeAndroidQChangeWH(true)
                        .imageSpanCount(THREE)// 每行显示个数
                        .isCompress(true)// 是否压缩
                        .isEnableCrop(isEnableCrop)// 是否裁剪
                        .withAspectRatio(isCircle ? ratioX : ratioX, isCircle ? ratioY : ratioY)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                        .circleDimmedLayer(isCircle)// 是否圆形裁剪
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .rotateEnabled(rotateEnabled) // 裁剪是否可旋转图片
                        .showCropFrame((isEnableCrop && !isCircle))// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .minimumCompressSize(200)// 小于200kb的图片不压缩
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(ONE)// 最小选择数量// 最大图片选择数量
                        .selectionData(selectionData)
                        .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)// 列表动画效果
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//竖屏
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                if (null != succeedCallBackListener) {
                                    succeedCallBackListener.succeedCallBack(result);
                                }
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            });
        }
    }
}
