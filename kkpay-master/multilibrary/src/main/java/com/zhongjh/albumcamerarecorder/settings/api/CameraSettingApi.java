package com.zhongjh.albumcamerarecorder.settings.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhongjh.albumcamerarecorder.camera.constants.FlashModels;
import com.zhongjh.albumcamerarecorder.camera.listener.OnCameraViewListener;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.MultiMediaSetting;

import java.util.Set;

import com.zhongjh.common.coordinator.VideoMergeCoordinator;
import com.zhongjh.common.enums.MimeType;

/**
 * 有关拍摄界面的动态设置
 *
 * @author zhongjh
 * @date 2019/3/20
 */
public interface CameraSettingApi {

    /**
     * 销毁事件
     */
    void onDestroy();

    /**
     * 提示文本
     *
     * @param string
     * @return
     */
    CameraSetting setDisplay(String string);

    /**
     * 支持的类型：图片，视频
     * 这个优先于 {@link MultiMediaSetting#choose}
     *
     * @param mimeTypes 类型
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting mimeTypeSet(@NonNull Set<MimeType> mimeTypes);

    /**
     * 是否开启图片高清拍摄
     * 注意开启该模式后，录制界面不能同时存在拍摄图片功能和录制视频功能
     *
     * @param enable whether to enable
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting enableImageHighDefinition(boolean enable);

    /**
     * 是否开启视频高清录制
     * 注意开启该模式后，录制界面不能同时存在拍摄图片功能和录制视频功能
     *
     * @param enable whether to enable
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting enableVideoHighDefinition(boolean enable);

    /**
     * 最长录制时间,默认10秒
     *
     * @param duration 最长录制时间,单位为秒
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting duration(int duration);

    /**
     * 最短录制时间限制，单位为毫秒，即是如果长按在1500毫秒内，都暂时不开启录制
     *
     * @param minDuration 最短录制时间限制，单位为毫秒
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting minDuration(int minDuration);

    /**
     * 点击即录制（点击拍摄图片功能则失效）
     *
     * @param isClickReocrd 是：开启该功能，否：关闭该功能
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting isClickRecord(boolean isClickReocrd);

    /**
     * 启动视频编辑功能，目前只有视频分段录制，后续会增加
     *
     * @param videoEditManager 视频编辑协调者
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting videoMerge(VideoMergeCoordinator videoEditManager);

    /**
     * 水印资源,可通过layout赋值水印，所处于的位置等等都可通过layout本身来处理
     *
     * @param watermarkResource 水印资源的layout id
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting watermarkResource(int watermarkResource);

    /**
     * 更换 切换前置/后置摄像头图标资源
     *
     * @param imageSwitch 切换前置/后置摄像头图标资源
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting imageSwitch(int imageSwitch);

    /**
     * 更换 闪光灯开启状态图标
     *
     * @param imageFlashOn 闪光灯开启状态图标
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting imageFlashOn(int imageFlashOn);

    /**
     * 更换 闪光灯关闭状态图标
     *
     * @param imageFlashOff 闪光灯关闭状态图标
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting imageFlashOff(int imageFlashOff);

    /**
     * 更换 闪光灯自动状态图标
     *
     * @param imageFlashAuto 闪光灯自动状态图标
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting imageFlashAuto(int imageFlashAuto);

    /**
     * 更换 闪光灯默认模式，默认是闪光灯关闭模式
     *
     * @param flashModel 闪光灯默认模式
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting flashModel(@FlashModels int flashModel);

    /**
     * 是否开启闪光灯记忆模式，默认关闭
     * 在开启闪光某个模式（例如闪光灯开启模式）后，在界面结束时，会自动记录当前模式（例如闪光灯开启模式）
     * 下次再打开时，依然是这个模式（例如闪光灯开启模式）
     *
     * @param enableFlashMemoryModel 是否开启
     * @return {@link CameraSetting} for fluent API.
     */
    CameraSetting enableFlashMemoryModel(boolean enableFlashMemoryModel);

    /**
     * 有关CameraView事件
     * <p>
     *
     * @param listener {@link OnCameraViewListener}
     * @return {@link CameraSetting} this
     */
    CameraSetting setOnCameraViewListener(@Nullable OnCameraViewListener listener);

}
