package com.zhongjh.albumcamerarecorder.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.model.SelectedItemCollection;
import com.zhongjh.albumcamerarecorder.album.utils.AlbumCompressFileTask;
import com.zhongjh.albumcamerarecorder.album.utils.PhotoMetadataUtils;
import com.zhongjh.albumcamerarecorder.album.widget.CheckRadioView;
import com.zhongjh.albumcamerarecorder.album.widget.CheckView;
import com.zhongjh.albumcamerarecorder.album.widget.PreviewViewPager;
import com.zhongjh.albumcamerarecorder.camera.util.FileUtil;
import com.zhongjh.albumcamerarecorder.preview.adapter.PreviewPagerAdapter;
import com.zhongjh.albumcamerarecorder.preview.previewitem.PreviewItemFragment;
import com.zhongjh.albumcamerarecorder.settings.AlbumSpec;
import com.zhongjh.albumcamerarecorder.settings.GlobalSpec;
import com.zhongjh.albumcamerarecorder.utils.MediaStoreUtils;
import com.zhongjh.common.entity.IncapableCause;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.listener.VideoEditListener;
import com.zhongjh.common.utils.MediaStoreCompat;
import com.zhongjh.common.utils.StatusBarUtils;
import com.zhongjh.common.utils.ThreadUtils;
import com.zhongjh.common.utils.UriUtils;
import com.zhongjh.common.widget.IncapableDialog;
import com.zhongjh.imageedit.ImageEditActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.zhongjh.albumcamerarecorder.utils.MediaStoreUtils.MediaTypes.TYPE_PICTURE;
import static com.zhongjh.albumcamerarecorder.utils.MediaStoreUtils.MediaTypes.TYPE_VIDEO;
import static com.zhongjh.imageedit.ImageEditActivity.REQ_IMAGE_EDIT;

/**
 * ???????????????
 *
 * @author zhongjh
 */
public class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private final String TAG = BasePreviewActivity.this.getClass().getSimpleName();

    public static final String EXTRA_IS_ALLOW_REPEAT = "extra_is_allow_repeat";
    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";
    public static final String EXTRA_RESULT_IS_EDIT = "extra_result_is_edit";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String CHECK_STATE = "checkState";
    public static final String ENABLE_OPERATION = "enable_operation";
    public static final String IS_SELECTED_LISTENER = "is_selected_listener";
    public static final String IS_SELECTED_CHECK = "is_selected_check";
    public static final String IS_EXTERNAL_USERS = "is_external_users";
    public static final String IS_BY_ALBUM = "is_by_album";
    public static final String IS_BY_PROGRESS_GRIDVIEW = "is_by_progress_gridview";

    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    protected GlobalSpec mGlobalSpec;
    protected AlbumSpec mAlbumSpec;

    protected PreviewPagerAdapter mAdapter;

    /**
     * ????????????
     */
    protected boolean mOriginalEnable;
    /**
     * ?????????????????????
     */
    private boolean mIsEdit;

    /**
     * ??????????????????????????????,???????????????
     */
    protected int mPreviousPos = -1;

    /**
     * ?????????????????????true,??????????????????????????????????????????????????????
     */
    protected boolean mEnableOperation = true;
    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????uri?????????????????????????????????
     */
    protected boolean mIsSelectedListener = true;
    /**
     * ?????????????????????????????????
     */
    protected boolean mIsSelectedCheck = true;
    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????onActivityResult????????????
     */
    protected boolean mIsExternalUsers = false;
    /**
     * ??????????????????????????????
     */
    protected boolean mIsByAlbum = false;
    /**
     * ???????????????????????????
     */
    protected boolean mIsByProgressGridView = false;

    /**
     * ???????????????
     */
    private MediaStoreCompat mPictureMediaStoreCompat;
    /**
     * ????????????????????????
     */
    private MediaStoreCompat mVideoMediaStoreCompat;
    /**
     * ??????????????????????????????
     */
    private File mEditImageFile;
    /**
     * ????????????-?????????????????????
     */
    ThreadUtils.SimpleTask<Void> mCompressFileTask;
    /**
     * ?????????????????????
     */
    private AlbumCompressFileTask mAlbumCompressFileTask;

    protected ViewHolder mViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // ????????????
        setTheme(GlobalSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);
        StatusBarUtils.initStatusBar(BasePreviewActivity.this);
        setContentView(R.layout.activity_media_preview_zjh);

        mGlobalSpec = GlobalSpec.getInstance();
        mAlbumSpec = AlbumSpec.getInstance();
        boolean isAllowRepeat = getIntent().getBooleanExtra(EXTRA_IS_ALLOW_REPEAT, false);
        mEnableOperation = getIntent().getBooleanExtra(ENABLE_OPERATION, true);
        mIsSelectedListener = getIntent().getBooleanExtra(IS_SELECTED_LISTENER, true);
        mIsSelectedCheck = getIntent().getBooleanExtra(IS_SELECTED_CHECK, true);
        mIsExternalUsers = getIntent().getBooleanExtra(IS_EXTERNAL_USERS, false);
        mIsByAlbum = getIntent().getBooleanExtra(IS_BY_ALBUM, false);
        mIsByProgressGridView = getIntent().getBooleanExtra(IS_BY_PROGRESS_GRIDVIEW, false);

        // ??????????????????
        if (mGlobalSpec.pictureStrategy != null) {
            // ?????????????????????????????????????????????????????????
            mPictureMediaStoreCompat = new MediaStoreCompat(this, mGlobalSpec.pictureStrategy);
        } else {
            // ?????????????????????
            if (mGlobalSpec.saveStrategy == null) {
                throw new RuntimeException("Don't forget to set SaveStrategy.");
            } else {
                mPictureMediaStoreCompat = new MediaStoreCompat(this, mGlobalSpec.saveStrategy);
            }
        }
        mVideoMediaStoreCompat = new MediaStoreCompat(getApplicationContext(),
                mGlobalSpec.videoStrategy == null ? mGlobalSpec.saveStrategy : mGlobalSpec.videoStrategy);
        if (savedInstanceState == null) {
            // ??????????????????????????????????????????
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE), isAllowRepeat);
            mOriginalEnable = getIntent().getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false);
        } else {
            // ????????????????????????
            mSelectedCollection.onCreate(savedInstanceState, isAllowRepeat);
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }

        mViewHolder = new ViewHolder(this);

        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, null);
        mViewHolder.pager.setAdapter(mAdapter);
        mViewHolder.checkView.setCountable(mAlbumSpec.countable);

        mAlbumCompressFileTask = new AlbumCompressFileTask(getApplicationContext(), TAG,
                BasePreviewActivity.class, mGlobalSpec, mPictureMediaStoreCompat, mVideoMediaStoreCompat);


        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMAGE_EDIT) {
                mIsEdit = true;
                refreshMultiMediaItem();
            }
        }
    }

    /**
     * ??????MultiMedia
     */
    private void refreshMultiMediaItem() {
        // ?????????????????????multimedia
        MultiMedia multiMedia = mAdapter.getMediaItem(mViewHolder.pager.getCurrentItem());
        // ????????????uri
        Uri editUri = mPictureMediaStoreCompat.getUri(mEditImageFile.getPath());
        multiMedia.setOldUri(multiMedia.getUri());
        // ????????????path
        String oldPath = null;
        if (multiMedia.getPath() == null) {
            File file = UriUtils.uriToFile(getApplicationContext(), multiMedia.getUri());
            if (file != null) {
                oldPath = UriUtils.uriToFile(getApplicationContext(), multiMedia.getUri()).getAbsolutePath();
            }
        } else {
            oldPath = multiMedia.getPath();
        }
        multiMedia.setOldPath(oldPath);
        // ????????????fragment
        multiMedia.setUri(editUri);
        multiMedia.setPath(mEditImageFile.getPath());
        mAdapter.setMediaItem(mViewHolder.pager.getCurrentItem(), multiMedia);
        ((PreviewItemFragment) mAdapter.getFragment(mViewHolder.pager.getCurrentItem())).init();

        // ???????????????mSelectedCollection????????????????????????????????????????????????????????????????????????????????????????????????????????????item?????????????????????new???????????????????????????new???????????????????????????
        for (MultiMedia item : mSelectedCollection.asList()) {
            if (item.getId() == multiMedia.getId()) {
                // ????????????id???????????????????????????????????????????????????????????????
                if (!item.equals(multiMedia)) {
                    // ????????????????????????????????????????????????????????????
                    item.setOldPath(multiMedia.getOldPath());
                    item.setOldUri(multiMedia.getOldUri());
                    item.setPath(multiMedia.getPath());
                    item.setUri(multiMedia.getUri());
                }
            }
        }

    }

    /**
     * ????????????
     */
    private void initListener() {
        // ??????
        mViewHolder.tvEdit.setOnClickListener(this);
        // ??????
        mViewHolder.iBtnBack.setOnClickListener(this);
        // ??????
        mViewHolder.buttonApply.setOnClickListener(this);
        // ?????????????????????
        mViewHolder.pager.addOnPageChangeListener(this);
        // ?????????????????????
        mViewHolder.checkView.setOnClickListener(this);
        // ??????????????????
        mViewHolder.originalLayout.setOnClickListener(v -> {
            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.z_multi_library_error_over_original_count, count, mAlbumSpec.originalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            mOriginalEnable = !mOriginalEnable;
            mViewHolder.original.setChecked(mOriginalEnable);
            if (!mOriginalEnable) {
                mViewHolder.original.setColor(Color.WHITE);
            }

            if (mAlbumSpec.onCheckedListener != null) {
                mAlbumSpec.onCheckedListener.onCheck(mOriginalEnable);
            }
        });
        // ??????Loading??????
        mViewHolder.pbLoading.setOnClickListener(v -> {
            // ????????????
            if (mCompressFileTask != null) {
                mCompressFileTask.cancel();
            }
            // ??????????????????
            setControlTouchEnable(true);
        });

        updateApplyButton();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mSelectedCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        setResultOkByIsCompress(false);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        if (mGlobalSpec.isCutscenes)
        //????????????????????????
        {
            this.overridePendingTransition(0, R.anim.activity_close);
        }
    }

    @Override
    protected void onDestroy() {
        if (mCompressFileTask != null) {
            ThreadUtils.cancel(mCompressFileTask);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibtnBack) {
            onBackPressed();
        } else if (v.getId() == R.id.buttonApply) {
            setResultOkByIsCompress(true);
        } else if (v.getId() == R.id.tvEdit) {
            MultiMedia item = mAdapter.getMediaItem(mViewHolder.pager.getCurrentItem());

            File file;

            file = mPictureMediaStoreCompat.createFile(0, true, "jpg");
            mEditImageFile = file;

            Intent intent = new Intent();
            intent.setClass(BasePreviewActivity.this, ImageEditActivity.class);
            intent.putExtra(ImageEditActivity.EXTRA_IMAGE_SCREEN_ORIENTATION, getRequestedOrientation());
            intent.putExtra(ImageEditActivity.EXTRA_IMAGE_URI, item.getUri());
            intent.putExtra(ImageEditActivity.EXTRA_IMAGE_SAVE_PATH, mEditImageFile.getAbsolutePath());
            startActivityForResult(intent, REQ_IMAGE_EDIT);
        } else if (v.getId() == R.id.checkView) {
            MultiMedia item = mAdapter.getMediaItem(mViewHolder.pager.getCurrentItem());
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                if (mAlbumSpec.countable) {
                    mViewHolder.checkView.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mViewHolder.checkView.setChecked(false);
                }
            } else {
                boolean isTrue = true;
                if (mIsSelectedCheck) {
                    isTrue = assertAddSelection(item);
                }
                if (isTrue) {
                    mSelectedCollection.add(item);
                    if (mAlbumSpec.countable) {
                        mViewHolder.checkView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
                    } else {
                        mViewHolder.checkView.setChecked(true);
                    }
                }
            }
            updateApplyButton();

            if (mAlbumSpec.onSelectedListener != null && mIsSelectedListener) {
                // ???????????????????????????
                mAlbumSpec.onSelectedListener.onSelected(mSelectedCollection.asListOfLocalFile());
            } else {
                mSelectedCollection.updatePath();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * ????????????
     *
     * @param position ??????
     */
    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mViewHolder.pager.getAdapter();
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((PreviewItemFragment) adapter.instantiateItem(mViewHolder.pager, mPreviousPos)).resetView();

            MultiMedia item = adapter.getMediaItem(position);
            if (mAlbumSpec.countable) {
                int checkedNum = mSelectedCollection.checkedNumOf(item);
                mViewHolder.checkView.setCheckedNum(checkedNum);
                if (checkedNum > 0) {
                    mViewHolder.checkView.setEnabled(true);
                } else {
                    mViewHolder.checkView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            } else {
                boolean checked = mSelectedCollection.isSelected(item);
                mViewHolder.checkView.setChecked(checked);
                if (checked) {
                    mViewHolder.checkView.setEnabled(true);
                } else {
                    mViewHolder.checkView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            }
            updateUI(item);
        }
        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * ????????????????????????
     */
    private void updateApplyButton() {
        // ?????????????????????
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            // ??????
            mViewHolder.buttonApply.setText(R.string.z_multi_library_button_sure_default);
            mViewHolder.buttonApply.setEnabled(false);
        } else if (selectedCount == 1 && mAlbumSpec.singleSelectionModeEnabled()) {
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mViewHolder.buttonApply.setText(R.string.z_multi_library_button_sure_default);
            mViewHolder.buttonApply.setEnabled(true);
        } else {
            // ?????????????????????
            mViewHolder.buttonApply.setEnabled(true);
            mViewHolder.buttonApply.setText(getString(R.string.z_multi_library_button_sure, selectedCount));
        }

        // ????????????????????????
        if (mAlbumSpec.originalable) {
            // ??????
            mViewHolder.originalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            // ??????
            mViewHolder.originalLayout.setVisibility(View.GONE);
        }

        // ????????????????????????
        if (!mEnableOperation) {
            mViewHolder.buttonApply.setVisibility(View.GONE);
            mViewHolder.checkView.setVisibility(View.GONE);
        } else {
            mViewHolder.buttonApply.setVisibility(View.VISIBLE);
            mViewHolder.checkView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ????????????????????????
     */
    private void updateOriginalState() {
        // ?????????????????????????????????
        mViewHolder.original.setChecked(mOriginalEnable);
        if (!mOriginalEnable) {
            mViewHolder.original.setColor(Color.WHITE);
        }

        if (countOverMaxSize() > 0) {
            // ???????????????????????????
            if (mOriginalEnable) {
                // ????????????????????????
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.z_multi_library_error_over_original_size, mAlbumSpec.originalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                // ?????????????????????????????????
                mViewHolder.original.setChecked(false);
                mViewHolder.original.setColor(Color.WHITE);
                mOriginalEnable = false;
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return ??????
     */
    private int countOverMaxSize() {
        int count = 0;
        int selectedCount = mSelectedCollection.count();
        for (int i = 0; i < selectedCount; i++) {
            MultiMedia item = mSelectedCollection.asList().get(i);
            if (item.isImage()) {
                float size = PhotoMetadataUtils.getSizeInMb(item.getSize());
                if (size > mAlbumSpec.originalMaxSize) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * ??????ui
     * ????????????item???gif???????????????M?????????
     * ????????????item???video?????????????????????
     *
     * @param item ????????????
     */
    @SuppressLint("SetTextI18n")
    protected void updateUI(MultiMedia item) {
        if (item.isGif()) {
            mViewHolder.size.setVisibility(View.VISIBLE);
            mViewHolder.size.setText(PhotoMetadataUtils.getSizeInMb(item.getSize()) + "M");
        } else {
            mViewHolder.size.setVisibility(View.GONE);
        }

        if (item.isVideo()) {
            mViewHolder.originalLayout.setVisibility(View.GONE);
        } else if (mAlbumSpec.originalable) {
            mViewHolder.originalLayout.setVisibility(View.VISIBLE);
        }

        // ???????????? & ????????? & ????????????????????????????????????
        if (item.isImage() && mGlobalSpec.isImageEdit && !mIsByProgressGridView) {
            mViewHolder.tvEdit.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.tvEdit.setVisibility(View.GONE);
        }
    }

    /**
     * ??????Activity??????????????????,????????????????????????????????????????????????
     *
     * @param apply ????????????
     */
    private void setResultOkByIsCompress(boolean apply) {
        // ????????????????????????
        if (mGlobalSpec.imageCompressionInterface != null) {
            if (apply) {
                compressFile();
            } else {
                // ????????????
                setResultOk(false);
            }
        } else {
            if (apply) {
                moveStoreCompatFile();
            }
            setResultOk(apply);
        }
    }

    /**
     * ??????????????????????????????
     */
    private void moveStoreCompatFile() {
        // ???????????????????????????????????????
        for (LocalFile item : mSelectedCollection.asList()) {
            if (item.getPath() != null) {
                File oldFile = new File(item.getPath());
                if (oldFile.exists()) {
                    if (item.isImage() || item.isVideo()) {
                        File newFile;
                        if (item.isImage()) {
                            newFile = mPictureMediaStoreCompat.createFile(0, false, mAlbumCompressFileTask.getNameSuffix(item.getPath()));
                        } else {
                            // ???????????????
                            newFile = mVideoMediaStoreCompat.createFile(1, false, mAlbumCompressFileTask.getNameSuffix(item.getPath()));
                        }
                        HandleEditImages(item, newFile, oldFile);
                    }
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    private void compressFile() {
        // ??????loading??????
        setControlTouchEnable(false);

        // ?????????????????????
        ThreadUtils.executeByIo(getCompressFileTask());
    }

    private ThreadUtils.SimpleTask<Void> getCompressFileTask() {
        mCompressFileTask = new ThreadUtils.SimpleTask<Void>() {

            @Override
            public Void doInBackground() {
                // ??????????????????????????????????????????????????????
                if (mIsByAlbum) {
                    // ??? ???????????? ????????? ????????????
                    for (LocalFile item : mSelectedCollection.asList()) {
                        Log.d(TAG, "item " + item.getId());
                        // ????????????????????????
                        LocalFile isCompressItem = mAlbumCompressFileTask.isCompress(item);
                        if (isCompressItem != null) {
                            continue;
                        }
                        // ???????????????????????????????????????
                        String path = mAlbumCompressFileTask.getPath(item);
                        if (path != null) {
                            handleCompress(item, path);
                        }
                    }
                }
                return null;
            }

            @Override
            public void onSuccess(Void result) {
                setResultOk(true);
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                Log.d(TAG, "getCompressFileTask onFail " + t.getMessage());
            }
        };
        return mCompressFileTask;
    }

    /**
     * ????????????????????????????????????
     *
     * @param item LocalFile
     * @param path ??????????????????
     */
    private void handleCompress(LocalFile item, String path) {
        // ????????????????????????
        if (!item.isImage() && !item.isVideo()) {
            return;
        }
        String newFileName = mAlbumCompressFileTask.getNewFileName(item, path);
        // ???????????????????????????????????????
        File newFile = mAlbumCompressFileTask.getNewFile(item, path, newFileName);
        // ???????????????????????????????????????????????? ???????????????
        if (newFile.exists() && item.getOldPath() == null) {
            if (item.isImage()) {
                item.updateFile(getApplicationContext(), mPictureMediaStoreCompat, item, newFile);
            } else {
                item.updateFile(getApplicationContext(), mVideoMediaStoreCompat, item, newFile);
            }
            Log.d(TAG, "??????????????????");
        } else {
            // ????????????
            if (item.isImage()) {
                // ??????????????????????????????
                File compressionFile = mAlbumCompressFileTask.handleImage(path);
                // ???????????????????????????????????????
                if (item.getOldPath() != null) {
                    newFile = mPictureMediaStoreCompat.createFile(0, false, mAlbumCompressFileTask.getNameSuffix(item.getOldPath()));
                }
                HandleEditImages(item, newFile, compressionFile);
                Log.d(TAG, "?????????????????????");
            } else if (item.isVideo()) {
                // ????????????
                if (mGlobalSpec.isCompressEnable()) {
                    // ???????????????????????????????????????
                    if (item.getOldPath() != null) {
                        newFile = mPictureMediaStoreCompat.createFile(0, false, mAlbumCompressFileTask.getNameSuffix(item.getOldPath()));
                    }
                    File finalNewFile = newFile;
                    mGlobalSpec.videoCompressCoordinator.setVideoCompressListener(BasePreviewActivity.class, new VideoEditListener() {
                        @Override
                        public void onFinish() {
                            item.updateFile(getApplicationContext(), mPictureMediaStoreCompat, item, finalNewFile);
                            // ?????????????????????????????????
                            if (item.getOldPath() != null) {
                                Uri uri = MediaStoreUtils.displayToGallery(getApplicationContext(), finalNewFile, TYPE_VIDEO,
                                        item.getDuration(), item.getWidth(), item.getHeight(),
                                        mVideoMediaStoreCompat.getSaveStrategy().getDirectory(), mVideoMediaStoreCompat);
                                item.setId(MediaStoreUtils.getId(uri));
                            }
                            Log.d(TAG, "?????????????????????");
                        }

                        @Override
                        public void onProgress(int progress, long progressTime) {
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(@NotNull String message) {
                        }
                    });
                    mGlobalSpec.videoCompressCoordinator.compressAsync(BasePreviewActivity.class, path, finalNewFile.getPath());
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     * ????????????????????????????????????????????????????????????
     *
     * @param item    ????????????
     * @param newFile ??????????????????
     * @param oldFile ???????????????
     */
    private void HandleEditImages(LocalFile item, File newFile, File oldFile) {
        // ????????????????????????(????????????)
        if (item.getOldPath() != null) {
            // ???????????????????????? ?????? ??????
            FileUtil.move(oldFile, newFile);
        } else {
            // ????????????????????????????????????????????????????????????????????????
            FileUtil.copy(oldFile, newFile);
        }
        item.updateFile(getApplicationContext(), mPictureMediaStoreCompat, item, newFile);
        // ?????????????????????????????????
        if (item.getOldPath() != null) {
            Uri uri = MediaStoreUtils.displayToGallery(this, newFile, TYPE_PICTURE,
                    item.getDuration(), item.getWidth(), item.getHeight(),
                    mPictureMediaStoreCompat.getSaveStrategy().getDirectory(), mPictureMediaStoreCompat);
            item.setId(MediaStoreUtils.getId(uri));
        }
    }

    /**
     * ???????????????
     *
     * @param apply ????????????
     */
    protected synchronized void setResultOk(boolean apply) {
        Log.d(TAG, "setResultOk");
        refreshMultiMediaItem(apply);
        if (mGlobalSpec.onResultCallbackListener == null || !mIsExternalUsers) {
            // ???????????????????????????????????????????????????RESULT_OK
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(EXTRA_RESULT_APPLY, apply);
            intent.putExtra(EXTRA_RESULT_IS_EDIT, mIsEdit);
            intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            if (mIsExternalUsers && !apply) {
                setResult(RESULT_CANCELED, intent);
            } else {
                setResult(RESULT_OK, intent);
            }
        } else {
            mGlobalSpec.onResultCallbackListener.onResultFromPreview(mSelectedCollection.asList(), apply);
        }
        finish();
    }

    /**
     * ?????????????????? ??? ???????????????????????????
     *
     * @param apply ???????????? TODO
     */
    private void refreshMultiMediaItem(boolean apply) {
        if (mIsEdit) {
            // ????????????????????????????????????
            for (MultiMedia multiMedia : mAdapter.getmItems()) {
                if (apply) {
                    // ??????????????????
                    String path = null;
                    if (multiMedia.getPath() == null) {
                        File file = UriUtils.uriToFile(getApplicationContext(), multiMedia.getUri());
                        if (file != null) {
                            path = file.getAbsolutePath();
                        }
                    } else {
                        path = multiMedia.getPath();
                    }

                    // ?????????old??????????????????
                    if (path != null && !TextUtils.isEmpty(multiMedia.getOldPath())) {
                        File file = new File(path);
                        multiMedia.setUri(mPictureMediaStoreCompat.getUri(path));
                        multiMedia.setPath(file.getAbsolutePath());
                    }
                } else {
                    // ?????????????????????
                    if (multiMedia.getOldUri() != null) {
                        multiMedia.setUri(multiMedia.getOldUri());
                    }
                    if (!TextUtils.isEmpty(multiMedia.getOldPath())) {
                        multiMedia.setPath(multiMedia.getOldPath());
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private void setControlTouchEnable(boolean enable) {
        // ???????????????????????? ????????? view,????????????
        if (!enable) {
            mViewHolder.pbLoading.setVisibility(View.VISIBLE);
            mViewHolder.buttonApply.setVisibility(View.GONE);
            mViewHolder.checkView.setEnabled(false);
            mViewHolder.checkView.setOnClickListener(null);
            mViewHolder.tvEdit.setEnabled(false);
            mViewHolder.originalLayout.setEnabled(false);
        } else {
            mViewHolder.pbLoading.setVisibility(View.GONE);
            mViewHolder.buttonApply.setVisibility(View.VISIBLE);
            mViewHolder.checkView.setEnabled(true);
            mViewHolder.checkView.setOnClickListener(this);
            mViewHolder.tvEdit.setEnabled(true);
            mViewHolder.originalLayout.setEnabled(true);
        }
    }

    /**
     * ????????????
     *
     * @param item ????????????
     * @return ???true?????????????????????
     */
    private boolean assertAddSelection(MultiMedia item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }

    public static class ViewHolder {
        public Activity activity;
        public PreviewViewPager pager;
        ImageButton iBtnBack;
        TextView tvEdit;
        public CheckRadioView original;
        public LinearLayout originalLayout;
        public TextView size;
        public TextView buttonApply;
        public FrameLayout bottomToolbar;
        public CheckView checkView;
        public ProgressBar pbLoading;

        ViewHolder(Activity activity) {
            this.activity = activity;
            this.pager = activity.findViewById(R.id.pager);
            this.iBtnBack = activity.findViewById(R.id.ibtnBack);
            this.tvEdit = activity.findViewById(R.id.tvEdit);
            this.original = activity.findViewById(R.id.original);
            this.originalLayout = activity.findViewById(R.id.originalLayout);
            this.size = activity.findViewById(R.id.size);
            this.buttonApply = activity.findViewById(R.id.buttonApply);
            this.bottomToolbar = activity.findViewById(R.id.bottomToolbar);
            this.checkView = activity.findViewById(R.id.checkView);
            this.pbLoading = activity.findViewById(R.id.pbLoading);
        }

    }
}
