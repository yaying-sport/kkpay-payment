package com.zhongjh.albumcamerarecorder.settings.api;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.zhongjh.albumcamerarecorder.album.engine.ImageEngine;
import com.zhongjh.albumcamerarecorder.album.engine.impl.GlideEngine;
import com.zhongjh.albumcamerarecorder.album.engine.impl.PicassoEngine;
import com.zhongjh.albumcamerarecorder.listener.ImageCompressionInterface;
import com.zhongjh.albumcamerarecorder.listener.OnMainListener;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.settings.AlbumSetting;
import com.zhongjh.albumcamerarecorder.settings.AlbumSpec;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.GlobalSetting;
import com.zhongjh.albumcamerarecorder.settings.RecorderSetting;
import com.zhongjh.common.coordinator.VideoCompressCoordinator;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;

import java.util.ArrayList;

/**
 * 用于构建媒体具体公共设置 API。
 *
 * @author zhongjh
 * @date 2019/3/21
 */
public interface GlobalSettingApi {

    /**
     * 销毁事件，防止内存泄漏
     */
    void onDestroy();

    /**
     * 设置相册配置，如果不设置则不启用相册
     *
     * @param albumSetting 相册
     * @return this
     */
    GlobalSetting albumSetting(AlbumSetting albumSetting);

    /**
     * 设置录制配置，如果不设置则不启用录制
     *
     * @param cameraSetting 录制
     * @return this
     */
    GlobalSetting cameraSetting(CameraSetting cameraSetting);

    /**
     * 设置录音配置，如果不设置则不启用录音
     *
     * @param recorderSetting 录音
     * @return this
     */
    GlobalSetting recorderSetting(RecorderSetting recorderSetting);

    /**
     * 主题
     * <p>
     * 有两个内置主题：
     * 1. R.style.AppTheme_Blue
     * 2. R.style.AppTheme.Dracula
     * 你可以定义从上述主题或其他主题派生的自定义主题。
     *
     * @param themeId 样式id. 默认为R.style.AppTheme_Blue
     * @return {@link GlobalSetting} this
     */
    GlobalSetting theme(@StyleRes int themeId);

    /**
     * 设置界面默认显示 相册、录制、录音 三个其中之一
     *
     * @param position 0：相册，录制：1，录音：2
     * @return {@link GlobalSetting} this
     */
    GlobalSetting defaultPosition(int position);

    /**
     * 仅当 {@link AlbumSpec#mediaTypeExclusive} 设置为true并且您希望为图像和视频媒体类型设置不同的最大可选文件时才有用。
     * <p>
     * <p>
     * 根据当前设置值来呈现相应的功能：
     * 1： maxSelectable有值maxImageSelectable无值，可选择的图片上限和所有数据的上限总和以maxSelectable为标准
     * 2： maxSelectable无值maxImageSelectable有值，可选择的图片上限以maxImageSelectable为准，其他例如视频音频也是以各自的上限为准
     * 3： maxSelectable有值maxImageSelectable有值，可选择的图片上限以maxImageSelectable为准，但是最终总和数据以maxSelectable为标准
     *
     * @param maxSelectable      最大选择数量
     * @param maxImageSelectable imga的最大可选计数.
     * @param maxVideoSelectable video的最大可选计数.
     * @param maxAudioSelectable audio的最大可选计数.
     * @param alreadyImageCount  已选择的图片数量
     * @param alreadyVideoCount  已选择的视频数量
     * @param alreadyAudioCount  已选择的音频数量
     * @return {@link GlobalSetting} this
     */
    GlobalSetting maxSelectablePerMediaType(Integer maxSelectable,
                                            Integer maxImageSelectable, Integer maxVideoSelectable, Integer maxAudioSelectable,
                                            int alreadyImageCount, int alreadyVideoCount, int alreadyAudioCount);

    /**
     * @param alreadyImageCount 已选择的图片数量
     * @param alreadyVideoCount 已选择的视频数量
     * @param alreadyAudioCount 已选择的音频数量
     * @return {@link GlobalSetting} this
     */
    GlobalSetting alreadyCount(int alreadyImageCount, int alreadyVideoCount, int alreadyAudioCount);

    /**
     * 保存文件的位置{@link androidx.core.content.FileProvider}.
     *
     * @param saveStrategy {@link SaveStrategy}, 仅在启用捕获时需要
     * @return {@link GlobalSetting} this
     */
    GlobalSetting allStrategy(SaveStrategy saveStrategy);

    /**
     * 保存图片文件的位置{@link androidx.core.content.FileProvider}.
     * 如果设置这个，有关图片的优先权比allStrategy高     *
     *
     * @param saveStrategy {@link SaveStrategy}, 仅在启用捕获时需要
     * @return {@link GlobalSetting} this
     */
    GlobalSetting pictureStrategy(SaveStrategy saveStrategy);

    /**
     * 如果设置这个，有关视频的优先权比allStrategy高
     * 为保存内部和外部视频文件的位置提供的捕获策略{@link androidx.core.content.FileProvider}.
     *
     * @param saveStrategy {@link SaveStrategy}, 仅在启用捕获时需要
     * @return {@link GlobalSetting} this
     */
    GlobalSetting videoStrategy(SaveStrategy saveStrategy);

    /**
     * 如果设置这个，有关音频的优先权比allStrategy高
     * 为保存内部和外部音频文件的位置提供的捕获策略{@link androidx.core.content.FileProvider}.
     *
     * @param saveStrategy {@link SaveStrategy}, 仅在启用捕获时需要
     * @return {@link GlobalSetting} this
     */
    GlobalSetting audioStrategy(SaveStrategy saveStrategy);

    /**
     * 提供图像引擎。
     * <p>
     * 有两个内置图像引擎：
     * 1. {@link GlideEngine}
     * 2. {@link PicassoEngine}
     * 你可以实现你自己的图像引擎。
     *
     * @param imageEngine {@link ImageEngine}
     * @return {@link GlobalSetting} this
     */
    GlobalSetting imageEngine(ImageEngine imageEngine);

    /**
     * 设置是否启动过场动画 ，只包括开始打开界面和关闭界面的过场动画
     *
     * @param isCutscenes 是否启动
     * @return {@link GlobalSetting} this
     */
    GlobalSetting isCutscenes(boolean isCutscenes);

    /**
     * 横竖屏设置,默认强制竖屏
     *
     * @param requestedOrientation {@link GlobalSetting.ScreenOrientation}
     * @return {@link GlobalSetting} this
     */
    GlobalSetting setRequestedOrientation(@GlobalSetting.ScreenOrientation int requestedOrientation);

    /**
     * 设置图片是否开启编辑功能，涉及功能：预览、拍照
     *
     * @param isImageEdit 图片是否编辑
     * @return {@link GlobalSetting} this
     */
    GlobalSetting isImageEdit(boolean isImageEdit);

    /**
     * 有关压缩操作的接口
     *
     * @param listener 接口 {@link ImageCompressionInterface}
     * @return {@link GlobalSetting} for fluent API.
     */
    GlobalSetting setOnImageCompressionInterface(@Nullable ImageCompressionInterface listener);

    /**
     * 有关首页的一些事件
     * <p>
     * 这是一个冗余的api ,我们只建议您在需要立即执行某些操作时使用此API。
     *
     * @param listener {@link OnMainListener}
     * @return {@link GlobalSetting} for fluent API.
     */
    GlobalSetting setOnMainListener(@Nullable OnMainListener listener);

    /**
     *
     * @param videoCompressManager 视频压缩处理管理
     * @return {@link GlobalSetting} for fluent API.
     */
    GlobalSetting videoCompress(VideoCompressCoordinator videoCompressManager);

    /**
     * 开始进行多媒体操作并等待结果.
     *
     * @param requestCode 请求活动或片段的标识.
     */
    void forResult(int requestCode);

    /**
     * 开始进行多媒体操作并等待结果.
     *
     * @param listener 回调事件
     */
    void forResult(OnResultCallbackListener listener);

    /**
     * 调用打开图片、视频预览 - 主要用于配合九宫图
     *
     * @param activity    窗体
     * @param requestCode 请求码
     * @param list        数据源
     * @param position    当前数据的索引
     */
    void openPreviewData(Activity activity, int requestCode,
                         ArrayList<? extends MultiMedia> list, int position);

    /**
     * 调用打开图片预览 - 纯浏览不可操作
     *
     * @param activity 窗体
     * @param list     资源id数据源
     * @param position 当前数据的索引
     */
    void openPreviewResourceId(Activity activity, ArrayList<Integer> list, int position);

    /**
     * 调用打开图片预览 - 纯浏览不可操作
     *
     * @param activity 窗体
     * @param list     文件地址的数据源
     * @param position 当前数据的索引
     */
    void openPreviewPath(Activity activity, ArrayList<String> list, int position);
}
