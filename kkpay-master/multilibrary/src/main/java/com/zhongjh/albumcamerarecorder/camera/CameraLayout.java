package com.zhongjh.albumcamerarecorder.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.zhongjh.albumcamerarecorder.MainActivity;
import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.camera.adapter.PhotoAdapter;
import com.zhongjh.albumcamerarecorder.camera.adapter.PhotoAdapterListener;
import com.zhongjh.albumcamerarecorder.camera.camerastate.CameraStateManagement;
import com.zhongjh.albumcamerarecorder.camera.camerastate.StateInterface;
import com.zhongjh.albumcamerarecorder.camera.constants.FlashCacheUtils;
import com.zhongjh.albumcamerarecorder.camera.entity.BitmapData;
import com.zhongjh.albumcamerarecorder.camera.listener.CaptureListener;
import com.zhongjh.albumcamerarecorder.camera.listener.ClickOrLongListener;
import com.zhongjh.albumcamerarecorder.camera.listener.CloseListener;
import com.zhongjh.albumcamerarecorder.camera.listener.EditListener;
import com.zhongjh.albumcamerarecorder.camera.listener.ErrorListener;
import com.zhongjh.albumcamerarecorder.camera.listener.OperateCameraListener;
import com.zhongjh.albumcamerarecorder.camera.util.FileUtil;
import com.zhongjh.albumcamerarecorder.camera.util.LogUtil;
import com.zhongjh.albumcamerarecorder.camera.widget.PhotoVideoLayoutBase;
import com.zhongjh.albumcamerarecorder.settings.CameraSpec;
import com.zhongjh.albumcamerarecorder.settings.GlobalSpec;
import com.zhongjh.albumcamerarecorder.utils.MediaStoreUtils;
import com.zhongjh.albumcamerarecorder.utils.PackageManagerUtils;
import com.zhongjh.albumcamerarecorder.utils.SelectableUtils;
import com.zhongjh.albumcamerarecorder.utils.ViewBusinessUtils;
import com.zhongjh.albumcamerarecorder.widget.BaseOperationLayout;
import com.zhongjh.albumcamerarecorder.widget.ChildClickableFrameLayout;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.enums.MimeType;
import com.zhongjh.common.listener.VideoEditListener;
import com.zhongjh.common.utils.MediaStoreCompat;
import com.zhongjh.common.utils.StatusBarUtils;
import com.zhongjh.common.utils.ThreadUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import static com.otaliastudios.cameraview.controls.Mode.PICTURE;
import static com.otaliastudios.cameraview.controls.Mode.VIDEO;
import static com.zhongjh.albumcamerarecorder.camera.constants.FlashModels.TYPE_FLASH_AUTO;
import static com.zhongjh.albumcamerarecorder.camera.constants.FlashModels.TYPE_FLASH_OFF;
import static com.zhongjh.albumcamerarecorder.camera.constants.FlashModels.TYPE_FLASH_ON;
import static com.zhongjh.albumcamerarecorder.utils.MediaStoreUtils.MediaTypes.TYPE_PICTURE;
import static com.zhongjh.albumcamerarecorder.widget.clickorlongbutton.ClickOrLongButton.BUTTON_STATE_BOTH;
import static com.zhongjh.albumcamerarecorder.widget.clickorlongbutton.ClickOrLongButton.BUTTON_STATE_CLICK_AND_HOLD;
import static com.zhongjh.albumcamerarecorder.widget.clickorlongbutton.ClickOrLongButton.BUTTON_STATE_ONLY_CLICK;
import static com.zhongjh.albumcamerarecorder.widget.clickorlongbutton.ClickOrLongButton.BUTTON_STATE_ONLY_LONG_CLICK;

/**
 * @author zhongjh
 * @date 2018/7/23.
 * 一个全局界面，包含了 右上角的闪光灯、前/后置摄像头的切换、底部按钮功能、对焦框等、显示当前拍照和摄像的界面
 * 该类类似MVP的View，主要包含有关 除了Camera的其他所有ui操作
 * <p>
 * 录制逻辑：
 * 拍摄/录制 文件后，会先缓存到Cache文件夹，当点击完成后，才将相关确认的文件复制到配置的路径下，加入相册库，并且清空Cache文件夹
 */
public class CameraLayout extends RelativeLayout implements PhotoAdapterListener {

    private final String TAG = CameraLayout.class.getSimpleName();
    private final static int PROGRESS_MAX = 100;
    /**
     * 图片
     */
    private MediaStoreCompat mPictureMediaStoreCompat;
    /**
     * 录像文件配置路径
     */
    private MediaStoreCompat mVideoMediaStoreCompat;
    /**
     * 公共配置
     */
    private GlobalSpec mGlobalSpec;
    /**
     * 拍摄配置
     */
    private CameraSpec mCameraSpec;

    /**
     * 状态管理
     */
    public final CameraStateManagement mCameraStateManagement;

    public CameraStateManagement getCameraStateManagement() {
        return mCameraStateManagement;
    }

    /**
     * 一个迁移图片的异步线程
     */
    public ThreadUtils.SimpleTask<ArrayList<LocalFile>> mMovePictureFileTask;

    /**
     * 闪关灯状态 默认关闭
     */
    private int mFlashModel = TYPE_FLASH_OFF;

    /**
     * 当前界面的所有控件
     */
    public ViewHolder mViewHolder;

    /**
     * 默认图片
     */
    private Drawable mPlaceholder;
    /**
     * 拍照的图片集合适配器
     */
    private PhotoAdapter mPhotoAdapter;
    /**
     * 图片,单图或者多图都会加入该列表
     */
    List<BitmapData> mBitmapData = new ArrayList<>();
    /**
     * 视频File,用于后面能随时删除
     */
    public File mVideoFile;
    /**
     * 照片File,用于后面能随时删除
     */
    public File mPhotoFile;
    /**
     * 编辑后的照片
     */
    private File mPhotoEditFile;
    /**
     * 是否短时间录像
     */
    public boolean mIsShort;

    /**
     * 是否分段录制
     */
    public boolean mIsSectionRecord;

    public boolean isBreakOff() {
        Log.d(TAG, "isBreakOff: " + mIsBreakOff);
        return mIsBreakOff;
    }

    public void setBreakOff(boolean breakOff) {
        Log.d(TAG, "setBreakOff: " + breakOff);
        this.mIsBreakOff = breakOff;
    }

    /**
     * 是否中断录像
     */
    private boolean mIsBreakOff;
    /**
     * 上一个分段录制的时间
     */
    private long mSectionRecordTime;
    /**
     * 处于分段录制模式下的视频的文件列表
     */
    public final ArrayList<String> mVideoPaths = new ArrayList<>();
    /**
     * 处于分段录制模式下的视频的时间列表
     */
    private final ArrayList<Long> mVideoTimes = new ArrayList<>();
    /**
     * 处于分段录制模式下合成的新的视频
     */
    private String mNewSectionVideoPath;
    /**
     * 用于延迟隐藏的事件，如果不用延迟，会有短暂闪屏现象
     */
    private final Handler mCameraViewGoneHandler = new Handler(Looper.getMainLooper());
    /**
     * 用于延迟显示的事件，如果不用延迟，会有短暂闪屏现象
     */
    private final Handler mCameraViewVisibleHandler = new Handler(Looper.getMainLooper());
    /**
     * 延迟拍摄，用于打开闪光灯再拍摄
     */
    private final Handler mCameraTakePictureHandler = new Handler(Looper.getMainLooper());
    private final Runnable mCameraViewGoneRunnable = new Runnable() {
        @Override
        public void run() {
            // 隐藏cameraView
            mViewHolder.cameraView.close();
        }
    };
    private final Runnable mCameraViewVisibleRunnable = new Runnable() {
        @Override
        public void run() {
            mViewHolder.cameraView.open();
        }
    };
    private final Runnable mCameraTakePictureRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCameraSpec.enableImageHighDefinition) {
                mViewHolder.cameraView.takePicture();
            } else {
                mViewHolder.cameraView.takePictureSnapshot();
            }
        }
    };

    // region 回调监听事件

    private ErrorListener mErrorListener;
    /**
     * 退出当前Activity的按钮监听
     */
    private CloseListener mCloseListener;
    /**
     * 编辑当前图片的监听
     */
    private EditListener mEditListener;

    /**
     * 确认跟返回的监听
     */
    private OperateCameraListener mOperateCameraListener;
    /**
     * 拍摄后操作图片的事件
     */
    private CaptureListener mCaptureListener;
    public MainActivity mMainActivity;
    private Fragment mFragment;


    // 赋值Camera错误回调

    public void setErrorListener(ErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    /**
     * 退出当前Activity的按钮监听
     *
     * @param closeListener 事件
     */
    public void setCloseListener(CloseListener closeListener) {
        this.mCloseListener = closeListener;
    }

    /**
     * 确认跟返回的监听
     *
     * @param operateCameraListener 事件
     */
    public void setOperateCameraListener(OperateCameraListener operateCameraListener) {
        this.mOperateCameraListener = operateCameraListener;
    }

    public OperateCameraListener getOperateCameraListener() {
        return mOperateCameraListener;
    }

    /**
     * 拍摄后操作图片的事件
     *
     * @param captureListener 事件
     */
    public void setCaptureListener(CaptureListener captureListener) {
        this.mCaptureListener = captureListener;
    }

    /**
     * 编辑图片的回调
     *
     * @param editListener 事件
     */
    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    // endregion

    public CameraLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCameraStateManagement = new CameraStateManagement(this);
        initData();
        initView();
        initListener();
    }

    /**
     * 初始化
     *
     * @param mainActivity {@link com.zhongjh.albumcamerarecorder.MainActivity }
     * @param fragment     设置fragment
     */
    public void init(MainActivity mainActivity, CameraFragment fragment) {
        this.mMainActivity = mainActivity;
        this.mFragment = fragment;

        // 初始化多图适配器，先判断是不是多图配置
        mPhotoAdapter = new PhotoAdapter(getContext(), fragment, mGlobalSpec, mBitmapData, this);
        if (SelectableUtils.getImageMaxCount() > 1) {
            mViewHolder.rlPhoto.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            mViewHolder.rlPhoto.setAdapter(mPhotoAdapter);
            mViewHolder.rlPhoto.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.rlPhoto.setVisibility(View.GONE);
        }
        mViewHolder.cameraView.setFacing(Facing.FRONT);//默认设置前置摄像头

        if (null == mCameraSpec.displayText ||TextUtils.isEmpty(mCameraSpec.displayText)){
            mViewHolder.tvDidplay.setText("--------");
            Context context = getContext();
            if(null!= context){
                Toast.makeText(context,context.getString(R.string.z_please_logback_reenter),Toast.LENGTH_LONG).show();
            }
        }else{
            mViewHolder.tvDidplay.setText(mCameraSpec.displayText);
        }

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化设置
        mCameraSpec = CameraSpec.getInstance();
        mGlobalSpec = GlobalSpec.getInstance();
        // 设置图片路径
        if (mGlobalSpec.pictureStrategy != null) {
            // 如果设置了视频的文件夹路径，就使用它的
            mPictureMediaStoreCompat = new MediaStoreCompat(getContext(), mGlobalSpec.pictureStrategy);
        } else {
            // 否则使用全局的
            if (mGlobalSpec.saveStrategy == null) {
                throw new RuntimeException("Don't forget to set SaveStrategy.");
            } else {
                mPictureMediaStoreCompat = new MediaStoreCompat(getContext(), mGlobalSpec.saveStrategy);
            }
        }
        mVideoMediaStoreCompat = new MediaStoreCompat(getContext(),
                mGlobalSpec.videoStrategy == null ? mGlobalSpec.saveStrategy : mGlobalSpec.videoStrategy);

        // 默认图片
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);

        // 闪光灯修改默认模式
        mFlashModel = mCameraSpec.flashModel;
        // 记忆模式
        flashGetCache();
    }

    /**
     * 初始化view
     */
    private void initView() {
        // 自定义View中如果重写了onDraw()即自定义了绘制，那么就应该在构造函数中调用view的setWillNotDraw(false).
        setWillNotDraw(false);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_camera_main_view_zjh, this);
        mViewHolder = new ViewHolder(view);

        // 如果有设置高清模式，则根据相应高清模式更改模式
        if (mCameraSpec.enableImageHighDefinition) {
            mViewHolder.cameraView.setMode(PICTURE);
        } else if (mCameraSpec.enableVideoHighDefinition) {
            mViewHolder.cameraView.setMode(VIDEO);
        } else {
            mViewHolder.cameraView.setMode(VIDEO);
        }

        if (mCameraSpec.watermarkResource != -1) {
            LayoutInflater.from(getContext()).inflate(mCameraSpec.watermarkResource, mViewHolder.cameraView, true);
        }

        // 回调cameraView可以自定义相关参数
        if (mCameraSpec.onCameraViewListener != null) {
            mCameraSpec.onCameraViewListener.onInitListener(mViewHolder.cameraView);
        }

        // 兼容沉倾状态栏
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getContext());
        mViewHolder.clMenu.setPadding(0, statusBarHeight, 0, 0);
        ViewGroup.LayoutParams layoutParams = mViewHolder.clMenu.getLayoutParams();
        layoutParams.height = layoutParams.height + statusBarHeight;

        // 如果启动视频编辑并且可录制数量>=0，便显示分段录制功能
        if (SelectableUtils.getVideoMaxCount() <= 0 || !mCameraSpec.isMergeEnable()) {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.GONE);
        } else {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.VISIBLE);
        }

        // 处理图片、视频等需要进度显示
        mViewHolder.pvLayout.getViewHolder().btnConfirm.setProgressMode(true);

        // 初始化cameraView,判断是否开启录制视频，如果开启就开启声音
        if (!SelectableUtils.videoValid()) {
            mViewHolder.cameraView.setAudio(Audio.OFF);
        }

        setFlashLamp(); // 设置闪光灯模式
        mViewHolder.imgSwitch.setImageResource(mCameraSpec.imageSwitch);
        // 设置录制时间
        mViewHolder.pvLayout.setDuration(mCameraSpec.duration * 1000);
        // 最短录制时间
        mViewHolder.pvLayout.setMinDuration(mCameraSpec.minDuration);
    }

    /**
     * 初始化中心按钮状态
     */
    private void initPvLayoutButtonFeatures() {
        // 判断点击和长按的权限
        if (mCameraSpec.isClickRecord) {
            // 禁用长按功能
            mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_CLICK_AND_HOLD);
            mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_light_touch_camera));
        } else {
            if (mCameraSpec.onlySupportImages()) {
                // 禁用长按功能
                mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
                mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_light_touch_take));
            } else if (mCameraSpec.onlySupportVideos()) {
                // 禁用点击功能
                mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_LONG_CLICK);
                mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_long_press_camera));
            } else {
                // 支持所有，不过要判断数量
                if (SelectableUtils.getImageMaxCount() == 0) {
                    // 禁用点击功能
                    mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_LONG_CLICK);
                    mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_long_press_camera));
                } else if (SelectableUtils.getVideoMaxCount() == 0) {
                    // 禁用长按功能
                    mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
                    mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_light_touch_take));
                } else {
                    mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_BOTH);
                    mViewHolder.pvLayout.setTip(getResources().getString(R.string.z_multi_library_light_touch_take_long_press_camera));
                }
            }
        }
    }

    /**
     * 初始化有关事件
     */
    private void initListener() {
        // 切换闪光灯模式
        initImgFlashListener();

        // 切换摄像头前置/后置
        initImgSwitchListener();

        // 主按钮监听
        initPvLayoutPhotoVideoListener();

        // 左右确认和取消
        initPvLayoutOperateListener();

        // 录制界面按钮事件监听，目前只有一个，点击分段录制
        initPvLayoutRecordListener();

        // 视频编辑后的事件，目前只有分段录制后合并
        initVideoEditListener();

        // 拍照监听
        initCameraViewListener();

        // 关闭事件
        initImgCloseListener();

        // 编辑图片事件
        initPhotoEditListener();
    }

    /**
     * 返回true的时候即是纸条跳过了后面的ActivityResult事件
     *
     * @param resultCode Activity的返回码
     * @return 返回true是跳过，返回false则是继续
     */
    public boolean onActivityResult(int resultCode) {
        return mCameraStateManagement.onActivityResult(resultCode);
    }

    /**
     * 生命周期onResume
     */
    public void onResume() {
        LogUtil.i("CameraLayout onResume");
        // 清空进度，防止正在进度中突然按home键
        mViewHolder.pvLayout.viewHolder.btnClickOrLong.reset();
        // 重置当前按钮的功能
        initPvLayoutButtonFeatures();
        mViewHolder.cameraView.open();
    }

    /**
     * 生命周期onPause
     */
    public void onPause() {
        LogUtil.i("CameraLayout onPause");
        mViewHolder.cameraView.close();
    }

    /**
     * 生命周期onDestroy
     *
     * @param isCommit 是否提交了数据,如果不是提交则要删除冗余文件
     */
    protected void onDestroy(boolean isCommit) {
        LogUtil.i("CameraLayout destroy");
        if (!isCommit) {
            if (mPhotoFile != null) {
                // 删除图片
                FileUtil.deleteFile(mPhotoFile);
            }
            if (mVideoFile != null) {
                // 删除视频
                FileUtil.deleteFile(mVideoFile);
            }
            // 删除多个视频
            for (String item : mVideoPaths) {
                FileUtil.deleteFile(item);
            }
            // 删除多个图片
            if (mPhotoAdapter.getListData() != null) {
                for (BitmapData bitmapData : mPhotoAdapter.getListData()) {
                    FileUtil.deleteFile(bitmapData.getPath());
                }
            }
            // 新合成视频删除
            if (mNewSectionVideoPath != null) {
                FileUtil.deleteFile(mNewSectionVideoPath);
            }
        } else {
            // 如果是提交的，删除合成前的视频
            for (String item : mVideoPaths) {
                FileUtil.deleteFile(item);
            }
        }
        mViewHolder.pvLayout.getViewHolder().btnConfirm.reset();
        if (mCameraSpec.isMergeEnable()) {
            mCameraSpec.videoMergeCoordinator.onMergeDestroy(CameraLayout.this.getClass());
            mCameraSpec.videoMergeCoordinator = null;
        }
        mCameraViewGoneHandler.removeCallbacks(mCameraViewGoneRunnable);
        mCameraViewVisibleHandler.removeCallbacks(mCameraViewVisibleRunnable);
        mCameraTakePictureHandler.removeCallbacks(mCameraTakePictureRunnable);
        if (mMovePictureFileTask != null) {
            mMovePictureFileTask.cancel();
        }
        mViewHolder.cameraView.destroy();
        // 记忆模式
        flashSaveCache();
    }

    /**
     * 切换闪光灯模式
     */
    private void initImgFlashListener() {
        mViewHolder.imgFlash.setOnClickListener(v -> {
            mFlashModel++;
            if (mFlashModel > TYPE_FLASH_OFF) {
                mFlashModel = TYPE_FLASH_AUTO;
            }
            // 重新设置当前闪光灯模式
            setFlashLamp();
        });
    }

    /**
     * 切换摄像头前置/后置
     */
    private void initImgSwitchListener() {
        mViewHolder.imgSwitch.setOnClickListener(v -> mViewHolder.cameraView.toggleFacing());
        mViewHolder.imgSwitch.setOnClickListener(v -> mViewHolder.cameraView.toggleFacing());
    }

    /**
     * 主按钮监听
     */
    private void initPvLayoutPhotoVideoListener() {
        mViewHolder.pvLayout.setPhotoVideoListener(new ClickOrLongListener() {
            @Override
            public void actionDown() {
                Log.d(TAG, "pvLayout actionDown");
                // 母窗体隐藏底部滑动
                ViewBusinessUtils.setTabLayoutScroll(false, mMainActivity, mViewHolder.pvLayout);
            }

            @Override
            public void onClick() {
                Log.d(TAG, "pvLayout onClick");
                takePhoto();
            }

            @Override
            public void onLongClickShort(final long time) {
                Log.d(TAG, "pvLayout onLongClickShort");
                longClickShort(time);
            }

            @Override
            public void onLongClick() {
                Log.d(TAG, "pvLayout onLongClick ");
                recordVideo();
            }

            @Override
            public void onLongClickEnd(long time) {
                Log.d(TAG, "pvLayout onLongClickEnd " + time);
                mSectionRecordTime = time;
                // 录像结束
                stopRecord(false);
            }

            @Override
            public void onLongClickError() {
                Log.d(TAG, "pvLayout onLongClickError ");
                if (mErrorListener != null) {
                    mErrorListener.onAudioPermissionError();
                }
            }

            @Override
            public void onBanClickTips() {
                // 判断如果是分段录制模式就提示
                if (mIsSectionRecord) {
                    Toast.makeText(getContext(), R.string.z_multi_library_working_video_click_later,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onClickStopTips() {
                if (mIsSectionRecord) {
                    mViewHolder.pvLayout.setTipAlphaAnimation(getResources().getString(R.string.z_multi_library_touch_your_suspension));
                } else {
                    mViewHolder.pvLayout.setTipAlphaAnimation(getResources().getString(R.string.z_multi_library_touch_your_end));
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        // 开启才能执行别的事件, 如果已经有分段视频，则不允许拍照了
        if (mViewHolder.cameraView.isOpened() && mVideoTimes.size() <= 0) {
            // 判断数量
            if (mPhotoAdapter.getItemCount() < currentMaxSelectable()) {
                // 设置不能点击，防止多次点击报错
                mViewHolder.rlMain.setChildClickable(false);
                // 判断如果是自动闪光灯模式便开启闪光灯
                if (mFlashModel == TYPE_FLASH_AUTO) {
                    mViewHolder.cameraView.setFlash(Flash.TORCH);
                    // 延迟1秒拍照
                    mCameraTakePictureHandler.postDelayed(mCameraTakePictureRunnable, 1000);
                } else {
                    mCameraTakePictureRunnable.run();
                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.z_multi_library_the_camera_limit_has_been_reached), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 录制视频
     */
    private void recordVideo() {
        // 开启录制功能才能执行别的事件
        if (mViewHolder.cameraView.isOpened()) {
            // 用于播放的视频file
            if (mVideoFile == null) {
                mVideoFile = mVideoMediaStoreCompat.createFile(1, true, "mp4");
            }
            if (mCameraSpec.enableVideoHighDefinition) {
                mViewHolder.cameraView.takeVideo(mVideoFile);
            } else {
                mViewHolder.cameraView.takeVideoSnapshot(mVideoFile);
            }
            // 设置录制状态
            if (mIsSectionRecord) {
                mCameraStateManagement.setState(mCameraStateManagement.getVideoMultipleIn());
            } else {
                mCameraStateManagement.setState(mCameraStateManagement.getVideoIn());
            }
            // 开始录像
            setMenuVisibility(INVISIBLE);
        }
    }

    /**
     * 录制时间过短
     */
    private void longClickShort(final long time) {
        Log.d(TAG, "longClickShort " + time);
        mCameraStateManagement.longClickShort(time);
        // 提示过短
        mViewHolder.pvLayout.setTipAlphaAnimation(getResources().getString(R.string.z_multi_library_the_recording_time_is_too_short));
        // 显示右上角菜单
        setMenuVisibility(VISIBLE);
        // 停止录像
        postDelayed(() -> stopRecord(true), mCameraSpec.minDuration - time);
    }

    /**
     * 左右两个按钮：确认和取消
     */
    private void initPvLayoutOperateListener() {
        mViewHolder.pvLayout.setOperateListener(new BaseOperationLayout.OperateListener() {
            @Override
            public void cancel() {
                Log.d(TAG, "cancel " + getState().toString());
                pvLayoutCancel();
            }

            @Override
            public void confirm() {
                Log.d(TAG, "confirm " + getState().toString());
                mCameraStateManagement.pvLayoutCommit();
                mViewHolder.pvLayout.getProgressMode();
            }

            @Override
            public void startProgress() {
                Log.d(TAG, "startProgress " + getState().toString());
                mCameraStateManagement.pvLayoutCommit();
            }

            @Override
            public void stopProgress() {
                // 不同模式下处理
                Log.d(TAG, "stopProgress " + getState().toString());
                mCameraStateManagement.stopProgress();
                // 重置按钮
                mViewHolder.pvLayout.resetConfirm();
            }

            @Override
            public void doneProgress() {
                Log.d(TAG, "doneProgress " + getState().toString());
                mViewHolder.pvLayout.resetConfirm();
            }
        });
    }

    /**
     * 录制界面按钮事件监听，目前只有一个，点击分段录制
     */
    private void initPvLayoutRecordListener() {
        mViewHolder.pvLayout.setRecordListener(tag -> {
            mIsSectionRecord = "1".equals(tag);
            mViewHolder.pvLayout.setProgressMode(true);
        });
    }

    /**
     * 视频编辑后的事件，目前 有分段录制后合并、压缩视频
     */
    private void initVideoEditListener() {
        if (mCameraSpec.isMergeEnable()) {
            mCameraSpec.videoMergeCoordinator.setVideoMergeListener(CameraLayout.this.getClass(), new VideoEditListener() {
                @Override
                public void onFinish() {
                    Log.d(TAG, "videoMergeCoordinator onFinish");
                    mViewHolder.pvLayout.getViewHolder().btnConfirm.setProgress(100);
                    PreviewVideoActivity.startActivity(mFragment, mNewSectionVideoPath);
                }

                @Override
                public void onProgress(int progress, long progressTime) {
                    Log.d(TAG, "videoMergeCoordinator onProgress progress: " + progress + " progressTime: " + progressTime);
                    if (progress >= PROGRESS_MAX) {
                        mViewHolder.pvLayout.getViewHolder().btnConfirm.setProgress(99);
                    } else {
                        mViewHolder.pvLayout.getViewHolder().btnConfirm.setProgress(progress);
                    }
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "videoMergeCoordinator onCancel");
                    // 重置按钮
                    mViewHolder.pvLayout.getViewHolder().btnConfirm.reset();
                }

                @Override
                public void onError(@NotNull String message) {
                    Log.d(TAG, "videoMergeCoordinator onError" + message);
                    // 重置按钮
                    mViewHolder.pvLayout.getViewHolder().btnConfirm.reset();
                }
            });
        }
    }

    /**
     * 拍照、录制监听
     */
    private void initCameraViewListener() {
        mViewHolder.cameraView.addCameraListener(new CameraListener() {

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                // 如果是自动闪光灯模式便关闭闪光灯
                if (mFlashModel == TYPE_FLASH_AUTO) {
                    mViewHolder.cameraView.setFlash(Flash.OFF);
                }
                result.toBitmap(bitmap -> {
                    // 显示图片
                    addCaptureData(bitmap);
                    // 恢复点击
                    mViewHolder.rlMain.setChildClickable(true);
                });
                super.onPictureTaken(result);
            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                Log.d(TAG, "onVideoTaken");
                super.onVideoTaken(result);
                // 判断是否短时间结束
                if (!mIsShort && !isBreakOff()) {
                    if (!mIsSectionRecord) {
                        //  如果录制结束，打开该视频。打开底部菜单
                        PreviewVideoActivity.startActivity(mFragment, result.getFile().getPath());
                        Log.d(TAG, "onVideoTaken " + result.getFile().getPath());
                    } else {
                        Log.d(TAG, "onVideoTaken 分段录制 " + result.getFile().getPath());
                        mVideoTimes.add(mSectionRecordTime);
                        // 如果已经有录像缓存，那么就不执行这个动作了
                        if (mVideoPaths.size() <= 0) {
                            mViewHolder.pvLayout.startShowLeftRightButtonsAnimator();
                            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.GONE);
                        }
                        // 加入视频列表
                        mVideoPaths.add(result.getFile().getPath());
                        // 显示当前进度
                        mViewHolder.pvLayout.setData(mVideoTimes);
                        // 创建新的file
                        mVideoFile = mVideoMediaStoreCompat.createFile(1, true, "mp4");
                        // 如果是在已经合成的情况下继续拍摄，那就重置状态
                        if (!mViewHolder.pvLayout.getProgressMode()) {
                            mViewHolder.pvLayout.resetConfirm();
                        }
                    }
                } else {
                    Log.d(TAG, "onVideoTaken delete " + mVideoFile.getPath());
                    FileUtil.deleteFile(mVideoFile);
                }
                mIsShort = false;
                setBreakOff(false);
                mViewHolder.pvLayout.setEnabled(true);
            }

            @Override
            public void onVideoRecordingStart() {
                Log.d(TAG, "onVideoRecordingStart");
                super.onVideoRecordingStart();
                // 录制开始后，在没有结果之前，禁止第二次点击
                mViewHolder.pvLayout.setEnabled(false);
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                Log.d(TAG, "onCameraError");
                super.onCameraError(exception);
                if (mIsSectionRecord) {
                    Toast.makeText(getContext(), R.string.z_multi_library_recording_error_roll_back_previous_paragraph, Toast.LENGTH_SHORT).show();
                    mViewHolder.pvLayout.getViewHolder().btnClickOrLong.selectionRecordRollBack();
                }
                if (!TextUtils.isEmpty(exception.getMessage())) {
                    Log.d(TAG, "onCameraError:" + exception.getMessage() + " " + exception.getReason());
                    mErrorListener.onError();
                }
                mViewHolder.pvLayout.setEnabled(true);
            }

        });
    }

    /**
     * 关闭事件
     */
    private void initImgCloseListener() {
        mViewHolder.imgClose.setOnClickListener(v -> {
            if (mCloseListener != null) {
                setBreakOff(true);
                mCloseListener.onClose();
            }
        });
    }

    /**
     * 编辑图片事件
     */
    private void initPhotoEditListener() {
        mViewHolder.rlEdit.setOnClickListener(view -> {
            Uri uri = (Uri) view.getTag();
            mPhotoEditFile = mPictureMediaStoreCompat.createFile(0, true, "jpg");
            if (mEditListener != null) {
                mEditListener.onImageEdit(uri, mPhotoEditFile.getAbsolutePath());
            }
        });
    }

    /**
     * 刷新多个图片
     */
    public void refreshMultiPhoto(ArrayList<BitmapData> bitmapDatas) {
        mBitmapData = bitmapDatas;
        mPhotoAdapter.setListData(mBitmapData);
    }

    /**
     * 刷新编辑后的单图
     *
     * @param width  图片宽
     * @param height 图片高
     */
    public void refreshEditPhoto(int width, int height) {
        // 删除旧图
        if (mPhotoFile.exists()) {
            boolean wasSuccessful = mPhotoFile.delete();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
        }
        // 用编辑后的图作为新的图片
        mPhotoFile = mPhotoEditFile;
        Uri uri = mPictureMediaStoreCompat.getUri(mPhotoFile.getPath());

        // 重置mCaptureBitmaps
        mBitmapData.clear();
        BitmapData bitmapData = new BitmapData(mPhotoFile.getPath(), uri, width, height);
        mBitmapData.add(bitmapData);

        // 重置位置
        mViewHolder.imgPhoto.resetMatrix();
        mGlobalSpec.imageEngine.loadUriImage(getContext(), mViewHolder.imgPhoto, uri);
        mViewHolder.rlEdit.setTag(uri);
    }

    /**
     * 取消核心事件
     */
    private void pvLayoutCancel() {
        mCameraStateManagement.pvLayoutCancel();
    }

    /**
     * 删除视频 - 多个模式
     */
    public void removeVideoMultiple() {
        // 每次删除，后面都要重新合成,新合成的也删除
        mViewHolder.pvLayout.resetConfirm();
        if (mNewSectionVideoPath != null) {
            FileUtil.deleteFile(mNewSectionVideoPath);
        }
        // 删除最后一个视频和视频文件
        FileUtil.deleteFile(mVideoPaths.get(mVideoPaths.size() - 1));
        mVideoPaths.remove(mVideoPaths.size() - 1);
        mVideoTimes.remove(mVideoTimes.size() - 1);

        // 显示当前进度
        mViewHolder.pvLayout.setData(mVideoTimes);
        mViewHolder.pvLayout.invalidateClickOrLongButton();
        if (mVideoPaths.size() == 0) {
            mCameraStateManagement.resetState();
        }
    }

    /**
     * 取消单图后的重置
     */
    public void cancelOnResetBySinglePicture() {
        mBitmapData.clear();

        // 根据不同状态处理相应的事件
        resetStateAll();
    }

    /**
     * 结束所有当前活动，重置状态
     * 一般指完成了当前活动，或者清除所有活动的时候调用
     */
    public void resetStateAll() {
        // 重置右上角菜单
        setMenuVisibility(VISIBLE);

        // 重置分段录制按钮 如果启动视频编辑并且可录制数量>=0，便显示分段录制功能
        if (SelectableUtils.getVideoMaxCount() <= 0 || !mCameraSpec.isMergeEnable()) {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.GONE);
        } else {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.VISIBLE);
        }

        // 恢复底部
        showBottomMenu();

        // 隐藏大图
        mViewHolder.flShow.setVisibility(View.GONE);

        // 隐藏编辑按钮
        mViewHolder.rlEdit.setVisibility(View.GONE);

        // 恢复底部按钮
        mViewHolder.pvLayout.reset();
    }

    /**
     * 恢复底部菜单
     */
    public void showBottomMenu() {
        if (mOperateCameraListener != null) {
            mOperateCameraListener.cancel();
        }
    }

    /**
     * 打开预览视频界面
     */
    public void openPreviewVideoActivity() {
        if (mIsSectionRecord) {
            // 合并视频
            mNewSectionVideoPath = mVideoMediaStoreCompat.createFile(1, true, "mp4").getPath();
            Log.d(TAG, "新的合并视频：" + mNewSectionVideoPath);
            for (String item : mVideoPaths) {
                Log.d(TAG, "新的合并视频素材：" + item);
            }
            mCameraSpec.videoMergeCoordinator.merge(CameraLayout.this.getClass(), mNewSectionVideoPath, mVideoPaths,
                    getContext().getCacheDir().getPath() + File.separator + "cam.txt");
        }
    }

    /**
     * 迁移图片文件，缓存文件迁移到配置目录
     * 在 doInBackground 线程里面也执行了 runOnUiThread 跳转UI的最终事件
     */
    public void movePictureFile() {
        // 执行等待动画
        mViewHolder.pvLayout.getViewHolder().btnConfirm.setProgress(1);
        // 开始迁移文件
        ThreadUtils.executeByIo(getMovePictureFileTask());
    }

    /**
     * 迁移图片的线程
     */
    private ThreadUtils.SimpleTask<ArrayList<LocalFile>> getMovePictureFileTask() {
        mMovePictureFileTask = new ThreadUtils.SimpleTask<ArrayList<LocalFile>>() {
            @Override
            public ArrayList<LocalFile> doInBackground() {
                // 每次拷贝文件后记录，最后用于全部添加到相册，回调等操作
                ArrayList<LocalFile> newFiles = new ArrayList<>();
                // 将 缓存文件 拷贝到 配置目录
                for (BitmapData item : mBitmapData) {
                    File oldFile = new File(item.getPath());
                    // 压缩图片
                    File compressionFile = null;
                    if (mGlobalSpec.imageCompressionInterface != null) {
                        try {
                            compressionFile = mGlobalSpec.imageCompressionInterface.compressionFile(getContext(), oldFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        compressionFile = oldFile;
                    }
                    // 移动文件,获取文件名称
                    String newFileName = item.getPath().substring(item.getPath().lastIndexOf(File.separator));
                    File newFile = mPictureMediaStoreCompat.createFile(newFileName, 0, false);
                    // new localFile
                    LocalFile localFile = new LocalFile();
                    localFile.setPath(newFile.getAbsolutePath());
                    localFile.setWidth(item.getWidth());
                    localFile.setHeight(item.getHeight());
                    localFile.setSize(compressionFile != null ? compressionFile.length() : 0);
                    newFiles.add(localFile);
                    FileUtil.copy(compressionFile, newFile, null, (ioProgress, file) -> {
                        if (ioProgress >= 1) {
                            Log.d(TAG, file.getAbsolutePath());
                            // 每次迁移完一个文件的进度
                            int progress = 100 / mBitmapData.size();
                            ThreadUtils.runOnUiThread(() ->
                                    mViewHolder.pvLayout.getViewHolder().btnConfirm.addProgress(progress)
                            );
                        }
                    });
                }
                for (LocalFile item : newFiles) {
                    // 加入图片到android系统库里面
                    Uri uri = MediaStoreUtils.displayToGallery(getContext(), new File(item.getPath()), TYPE_PICTURE, -1, item.getWidth(), item.getHeight(),
                            mPictureMediaStoreCompat.getSaveStrategy().getDirectory(), mPictureMediaStoreCompat);
                    // 加入相册后的最后是id，直接使用该id
                    item.setId(MediaStoreUtils.getId(uri));
                    item.setMimeType(MimeType.JPEG.getMimeTypeName());
                    item.setUri(mPictureMediaStoreCompat.getUri(item.getPath()));
                }
                // 执行完成
                Log.d(TAG, "captureSuccess");
                return newFiles;
            }

            @Override
            public void onSuccess(ArrayList<LocalFile> newFiles) {
                Log.d(TAG, "mMovePictureFileTask onSuccess");
                mOperateCameraListener.captureSuccess(newFiles);
                setUiEnableTrue();
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                Log.e(TAG, t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                setUiEnableTrue();
            }
        };
        return mMovePictureFileTask;
    }

    /**
     * 添加入数据源
     *
     * @param bitmap bitmap
     */
    private void addCaptureData(Bitmap bitmap) {
        // 初始化数据并且存储进file
        File file = mPictureMediaStoreCompat.saveFileByBitmap(bitmap, true);
        Uri uri = mPictureMediaStoreCompat.getUri(file.getPath());
        Log.d(TAG, "file:" + file.getAbsolutePath());
        BitmapData bitmapData = new BitmapData(file.getPath(), uri, bitmap.getWidth(), bitmap.getHeight());
        // 回收bitmap
        if (bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
        }
        // 加速回收机制
        System.gc();
        // 判断是否多个图片
        if (SelectableUtils.getImageMaxCount() > 1) {
            // 添加入数据源
            mBitmapData.add(bitmapData);
            showMultiplePicture();
        } else {
            mBitmapData.add(bitmapData);
            showSinglePicture(bitmapData, file, uri);
        }
        // 回调接口：添加图片后剩下的相关数据
        mCaptureListener.add(mBitmapData);
    }

    /**
     * 显示单图
     *
     * @param bitmapData 显示单图数据源
     * @param file       显示单图的文件
     * @param uri        显示单图的uri
     */
    private void showSinglePicture(BitmapData bitmapData, File file, Uri uri) {
        // 拍照  隐藏 闪光灯、右上角的切换摄像头
        setMenuVisibility(View.INVISIBLE);
        // 重置位置
        mViewHolder.imgPhoto.resetMatrix();
        mViewHolder.imgPhoto.setVisibility(VISIBLE);
        mGlobalSpec.imageEngine.loadUriImage(getContext(), mViewHolder.imgPhoto, bitmapData.getUri());
        mViewHolder.cameraView.close();
        mViewHolder.flShow.setVisibility(VISIBLE);
        mViewHolder.pvLayout.startTipAlphaAnimation();
        mViewHolder.pvLayout.startShowLeftRightButtonsAnimator();
        mPhotoFile = file;

        // 设置当前模式是图片模式
        mCameraStateManagement.setState(mCameraStateManagement.getPictureComplete());

        // 判断是否要编辑
        if (mGlobalSpec.isImageEdit) {
            mViewHolder.rlEdit.setVisibility(View.VISIBLE);
            mViewHolder.rlEdit.setTag(uri);
        } else {
            mViewHolder.rlEdit.setVisibility(View.INVISIBLE);
        }

        // 隐藏拍照按钮
        mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示多图
     */
    private void showMultiplePicture() {
        // 显示横版列表
        mViewHolder.rlPhoto.setVisibility(View.VISIBLE);

        // 显示横版列表的线条空间
        mViewHolder.vLine1.setVisibility(View.VISIBLE);
        mViewHolder.vLine2.setVisibility(View.VISIBLE);

        // 更新最后一个添加
        mPhotoAdapter.notifyItemInserted(mPhotoAdapter.getItemCount() - 1);
        mPhotoAdapter.notifyItemRangeChanged(mPhotoAdapter.getItemCount() - 1, mPhotoAdapter.getItemCount());

        mViewHolder.pvLayout.startTipAlphaAnimation();
        mViewHolder.pvLayout.startOperaeBtnAnimatorMulti();

        // 重置按钮，因为每次点击，都会自动关闭
        mViewHolder.pvLayout.getViewHolder().btnClickOrLong.resetState();
        // 显示右上角
        setMenuVisibility(View.VISIBLE);

        // 设置当前模式是图片休闲并存模式
        mCameraStateManagement.setState(mCameraStateManagement.getPictureMultiple());

        // 禁用长按事件，即禁止录像
        mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
    }

    /**
     * 获取当前view的状态
     *
     * @return 状态
     */
    public StateInterface getState() {
        return mCameraStateManagement.getState();
    }

    /**
     * 设置闪关灯
     */
    private void setFlashLamp() {
        switch (mFlashModel) {
            case TYPE_FLASH_AUTO:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashAuto);
                mViewHolder.cameraView.setFlash(Flash.AUTO);
                break;
            case TYPE_FLASH_ON:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashOn);
                mViewHolder.cameraView.setFlash(Flash.TORCH);
                break;
            case TYPE_FLASH_OFF:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashOff);
                mViewHolder.cameraView.setFlash(Flash.OFF);
                break;
            default:
                break;
        }
    }

    /**
     * 返回最多选择的图片数量
     *
     * @return 数量
     */
    private int currentMaxSelectable() {
        // 返回最大选择数量
        return SelectableUtils.getImageMaxCount();
    }

    /**
     * 停止录像并且完成它，如果是因为视频过短则清除冗余数据
     *
     * @param isShort 是否因为视频过短而停止
     */
    public void stopRecord(boolean isShort) {
        mCameraStateManagement.stopRecord(isShort);
    }

    /**
     * 设置右上角菜单是否显示
     */
    public void setMenuVisibility(int viewVisibility) {
//        setSwitchVisibility(viewVisibility);
//        mViewHolder.imgFlash.setVisibility(viewVisibility);
    }

    /**
     * 设置闪光灯是否显示，如果不支持，是一直不会显示
     */
    private void setSwitchVisibility(int viewVisibility) {
        if (!PackageManagerUtils.isSupportCameraLedFlash(getContext().getPackageManager())) {
            mViewHolder.imgSwitch.setVisibility(View.GONE);
        } else {
            mViewHolder.imgSwitch.setVisibility(viewVisibility);
        }
    }

    /**
     * 设置界面的功能按钮可以使用
     * 场景：如果压缩或者移动文件时异常，则恢复
     */
    private void setUiEnableTrue() {
        mViewHolder.imgFlash.setEnabled(true);
        mViewHolder.imgSwitch.setEnabled(true);
        // 重置按钮进度
        mViewHolder.pvLayout.viewHolder.btnConfirm.reset();
    }

    /**
     * 设置界面的功能按钮禁止使用
     * 场景：确认图片时，压缩中途禁止某些功能使用
     */
    public void setUiEnableFalse() {
        mViewHolder.imgFlash.setEnabled(false);
        mViewHolder.imgSwitch.setEnabled(false);
    }

    /**
     * 多视频分段录制中止提交
     */
    public void stopVideoMultiple() {
        if (mCameraSpec.isMergeEnable()) {
            mCameraSpec.videoMergeCoordinator.onMergeDispose(CameraLayout.this.getClass());
        }
    }

    @Override
    public void onClick() {

    }

    /**
     * 多图进行删除的时候
     *
     * @param position 数据的索引
     */
    @Override
    public void onDelete(int position) {
        // 删除文件
        FileUtil.deleteFile(mBitmapData.get(position).getPath());

        // 回调接口：删除图片后剩下的相关数据
        mCaptureListener.remove(mBitmapData);

        // 当列表全部删掉的话，就隐藏,为什么是 <= 1，因为是先删除实体，再删除数据源，所以这个判断结束后，就会删除数据源实际是0了
        if (mBitmapData.size() <= 1) {
            // 隐藏横版列表
            mViewHolder.rlPhoto.setVisibility(View.GONE);

            // 隐藏横版列表的线条空间
            mViewHolder.vLine1.setVisibility(View.GONE);
            mViewHolder.vLine2.setVisibility(View.GONE);

            // 隐藏左右侧按钮
            mViewHolder.pvLayout.getViewHolder().btnCancel.setVisibility(View.GONE);
            mViewHolder.pvLayout.getViewHolder().btnConfirm.setVisibility(View.GONE);

            // 恢复长按事件，即重新启用录像
            mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setVisibility(View.VISIBLE);
            initPvLayoutButtonFeatures();

            // 设置空闲状态
            mCameraStateManagement.setState(mCameraStateManagement.getPreview());

            // 如果是单图编辑情况下
            mViewHolder.rlEdit.setVisibility(View.GONE);

            // 恢复底部
            showBottomMenu();
        }
    }

    /**
     * 记忆模式下获取闪光灯缓存的模式
     */
    private void flashGetCache() {
        // 判断闪光灯是否记忆模式，如果是记忆模式则使用上个闪光灯模式
        if (mCameraSpec.enableFlashMemoryModel) {
            mFlashModel = FlashCacheUtils.getFlashModel(getContext());
        }
    }

    /**
     * 记忆模式下缓存闪光灯模式
     */
    private void flashSaveCache() {
        // 判断闪光灯是否记忆模式，如果是记忆模式则存储当前闪光灯模式
        if (mCameraSpec.enableFlashMemoryModel) {
            FlashCacheUtils.saveFlashModel(getContext(), mFlashModel);
        }
    }

    public static class ViewHolder {

        View rootView;
        ChildClickableFrameLayout rlMain;
        public ImageViewTouch imgPhoto;
        public TextView tvDidplay;
        public FrameLayout flShow;
        public ImageView imgFlash;
        public ImageView imgSwitch;
        public PhotoVideoLayoutBase pvLayout;
        public RecyclerView rlPhoto;
        View vLine1;
        View vLine2;
        View vLine3;
        ImageView imgClose;
        public CameraView cameraView;
        ConstraintLayout clMenu;
        RelativeLayout rlEdit;

        ViewHolder(View rootView) {
            this.rootView = rootView;
            this.rlMain = rootView.findViewById(R.id.rlMain);
            this.imgPhoto = rootView.findViewById(R.id.imgPhoto);
            this.flShow = rootView.findViewById(R.id.flShow);
            this.imgFlash = rootView.findViewById(R.id.imgFlash);
            this.tvDidplay = rootView.findViewById(R.id.tv_didplay);
            this.imgSwitch = rootView.findViewById(R.id.imgSwitch);
            this.pvLayout = rootView.findViewById(R.id.pvLayout);
            this.rlPhoto = rootView.findViewById(R.id.rlPhoto);
            this.vLine1 = rootView.findViewById(R.id.vLine1);
            this.vLine2 = rootView.findViewById(R.id.vLine2);
            this.vLine3 = rootView.findViewById(R.id.vLine3);
            this.imgClose = rootView.findViewById(R.id.imgClose);
            this.cameraView = rootView.findViewById(R.id.cameraView);
            this.clMenu = rootView.findViewById(R.id.clMenu);
            this.rlEdit = rootView.findViewById(R.id.rlEdit);
        }

    }

}
