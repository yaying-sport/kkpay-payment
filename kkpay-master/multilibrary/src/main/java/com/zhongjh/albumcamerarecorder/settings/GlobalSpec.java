package com.zhongjh.albumcamerarecorder.settings;

import android.content.pm.ActivityInfo;

import androidx.annotation.StyleRes;

import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.engine.ImageEngine;
import com.zhongjh.albumcamerarecorder.album.engine.impl.GlideEngine;
import com.zhongjh.albumcamerarecorder.constants.ModuleTypes;
import com.zhongjh.albumcamerarecorder.listener.ImageCompressionInterface;
import com.zhongjh.albumcamerarecorder.listener.OnMainListener;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.common.coordinator.VideoCompressCoordinator;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;

import java.util.Set;

/**
 * 设置的一些属性,别的界面也根据这个来进行动态改变
 *
 * @author zhongjh
 * @date 2018/8/23
 */
public class GlobalSpec {

    /**
     * 相册的设置
     */
    public AlbumSetting albumSetting;
    /**
     * 拍摄的设置
     */
    public CameraSetting cameraSetting;
    /**
     * 录音的设置
     */
    public RecorderSetting recorderSetting;
    /**
     * 选择 mime 的类型，MimeType.allOf()
     */
    private Set<MimeType> mimeTypeSet;
    /**
     * 是否通过正规方式进来
     */
    public boolean hasInited;
    /**
     * 默认从第几个开始
     */
    public int defaultPosition;
    /**
     * 样式
     */
    @StyleRes
    public int themeId;
    /**
     * 最大选择数量，如果设置为null，那么能选择的总数量就是 maxImageSelectable+maxVideoSelectable+maxAudioSelectable 的总数
     */
    public Integer maxSelectable = null;
    /**
     * 最大图片选择数量
     */
    public Integer maxImageSelectable = null;
    /**
     * 最大视频选择数量
     */
    public Integer maxVideoSelectable = null;
    /**
     * 最大音频选择数量
     */
    public Integer maxAudioSelectable = null;
    /**
     * 拍照\录像\存储的保存路径 参数1 true表示在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
     */
    public SaveStrategy saveStrategy;
    /**
     * 图片保存路径 参数1 true表示在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
     */
    public SaveStrategy pictureStrategy;
    /**
     * 视频保存路径 参数1 true表示在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
     */
    public SaveStrategy videoStrategy;
    /**
     * 音频保存路径 参数1 true表示在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
     */
    public SaveStrategy audioStrategy;
    public ImageEngine imageEngine;
    /**
     * 是否启动过场动画，只包括开始打开界面和关闭界面的过场动画
     */
    public boolean isCutscenes;

    /**
     * 横竖屏设置,默认强制竖屏
     */
    public int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    /**
     * 图片是否开启编辑功能，涉及功能：预览、拍照
     */
    public boolean isImageEdit;
    /**
     * 压缩图片接口
     */
    public ImageCompressionInterface imageCompressionInterface;
    /**
     * 视频压缩功能
     */
    public VideoCompressCoordinator videoCompressCoordinator;
    /**
     * 主界面的有关事件
     */
    public OnMainListener onMainListener;
    /**
     * 请求界面的Code
     */
    public int requestCode;
    /**
     * 回调监听
     */
    public OnResultCallbackListener onResultCallbackListener;

    private GlobalSpec() {
    }

    public static GlobalSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static GlobalSpec getCleanInstance() {
        GlobalSpec globalSpec = getInstance();
        globalSpec.reset();
        return globalSpec;
    }

    /**
     * 重置
     */
    private void reset() {
        albumSetting = null;
        cameraSetting = null;
        recorderSetting = null;
        mimeTypeSet = null;
        defaultPosition = 0;
        themeId = R.style.AppTheme_Blue;
        maxSelectable = null;
        maxImageSelectable = null;
        maxVideoSelectable = null;
        maxAudioSelectable = null;
        saveStrategy = null;
        pictureStrategy = null;
        videoStrategy = null;
        audioStrategy = null;
        hasInited = true;
        imageEngine = new GlideEngine();
        isCutscenes = true;
        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        isImageEdit = true;
        imageCompressionInterface = null;
        requestCode = 0;
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    private static final class InstanceHolder {
        private static final GlobalSpec INSTANCE = new GlobalSpec();
    }

    /**
     * @return 返回 mime 的类型，MimeType.allOf()
     */
    public Set<MimeType> getMimeTypeSet(@ModuleTypes int moduleTypes) {
        // 优先取各自的类型，如果没设置则取公共的
        switch (moduleTypes) {
            case ModuleTypes.ALBUM:
                if (AlbumSpec.getInstance().mimeTypeSet != null) {
                    return AlbumSpec.getInstance().mimeTypeSet;
                } else {
                    return GlobalSpec.getInstance().mimeTypeSet;
                }
            case ModuleTypes.CAMERA:
                if (CameraSpec.getInstance().mimeTypeSet != null) {
                    return CameraSpec.getInstance().mimeTypeSet;
                } else {
                    return GlobalSpec.getInstance().mimeTypeSet;
                }
            case ModuleTypes.RECORDER:
            default:
                break;
        }
        return mimeTypeSet;
    }

    public void setMimeTypeSet(Set<MimeType> mimeTypeSet) {
        this.mimeTypeSet = mimeTypeSet;
    }

    /**
     * @return 是否开启了视频压缩功能
     */
    public boolean isCompressEnable() {
        return videoCompressCoordinator != null;
    }

}
