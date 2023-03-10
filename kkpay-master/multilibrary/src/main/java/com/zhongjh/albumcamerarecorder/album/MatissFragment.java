package com.zhongjh.albumcamerarecorder.album;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.zhongjh.albumcamerarecorder.MainActivity;
import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.entity.Album;
import com.zhongjh.albumcamerarecorder.album.model.AlbumCollection;
import com.zhongjh.albumcamerarecorder.album.model.SelectedItemCollection;
import com.zhongjh.albumcamerarecorder.album.ui.mediaselection.MediaSelectionFragment;
import com.zhongjh.albumcamerarecorder.album.ui.mediaselection.adapter.AlbumMediaAdapter;
import com.zhongjh.albumcamerarecorder.album.utils.AlbumCompressFileTask;
import com.zhongjh.albumcamerarecorder.album.utils.PhotoMetadataUtils;
import com.zhongjh.albumcamerarecorder.album.widget.AlbumsSpinner;
import com.zhongjh.albumcamerarecorder.album.widget.CheckRadioView;
import com.zhongjh.albumcamerarecorder.preview.AlbumPreviewActivity;
import com.zhongjh.albumcamerarecorder.preview.BasePreviewActivity;
import com.zhongjh.albumcamerarecorder.preview.SelectedPreviewActivity;
import com.zhongjh.albumcamerarecorder.settings.AlbumSpec;
import com.zhongjh.albumcamerarecorder.settings.GlobalSpec;
import com.zhongjh.albumcamerarecorder.widget.ControlTouchFrameLayout;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.utils.ColorFilterUtil;
import com.zhongjh.common.utils.MediaStoreCompat;
import com.zhongjh.common.utils.StatusBarUtils;
import com.zhongjh.common.utils.ThreadUtils;
import com.zhongjh.common.widget.IncapableDialog;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.zhongjh.albumcamerarecorder.constants.Constant.EXTRA_RESULT_SELECTION_LOCAL_FILE;

/**
 * ??????
 *
 * @author zhongjh
 * @date 2018/8/22
 */
public class MatissFragment extends Fragment implements AlbumCollection.AlbumCallbacks,
        MediaSelectionFragment.SelectionProvider,
        AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener {

    private final String TAG = MatissFragment.this.getClass().getSimpleName();

    private static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String ARGUMENTS_MARGIN_BOTTOM = "arguments_margin_bottom";

    private static final String CHECK_STATE = "checkState";

    private AppCompatActivity mActivity;
    private Context mContext;
    /**
     * ?????????Fragment,???????????????????????????????????????????????????????????????
     */
    MediaSelectionFragment mFragmentLast;

    /**
     * ????????????
     */
    private GlobalSpec mGlobalSpec;
    /**
     * ????????????
     */
    private MediaStoreCompat mPictureMediaStoreCompat;
    /**
     * ????????????????????????
     */
    private MediaStoreCompat mVideoMediaStoreCompat;

    /**
     * ?????????????????????
     */
    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private SelectedItemCollection mSelectedCollection;
    private AlbumSpec mAlbumSpec;

    /**
     * ?????????????????????
     */
    private AlbumsSpinner mAlbumsSpinner;
    /**
     * ??????????????????????????????
     */
    private AlbumsSpinnerAdapter mAlbumsSpinnerAdapter;

    /**
     * ????????????
     */
    private boolean mOriginalEnable;
    /**
     * ????????????
     */
    private boolean mIsRefresh;

    /**
     * ??????????????????
     */
    ThreadUtils.SimpleTask<ArrayList<LocalFile>> mCompressFileTask;
    /**
     * ?????????????????????
     */
    private AlbumCompressFileTask mAlbumCompressFileTask;

    private ViewHolder mViewHolder;

    /**
     * @param marginBottom ????????????
     */
    public static MatissFragment newInstance(int marginBottom) {
        MatissFragment matissFragment = new MatissFragment();
        Bundle args = new Bundle();
        matissFragment.setArguments(args);
        args.putInt(ARGUMENTS_MARGIN_BOTTOM, marginBottom);
        return matissFragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.mActivity = (AppCompatActivity) activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
        mSelectedCollection = new SelectedItemCollection(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matiss_zjh, container, false);
        this.view = view;
        mViewHolder = new ViewHolder(view);
        initConfig();
        mAlbumCompressFileTask = new AlbumCompressFileTask(mContext, TAG, MatissFragment.class, mGlobalSpec, mPictureMediaStoreCompat, mVideoMediaStoreCompat);
        initView(savedInstanceState);
        initListener();
        return view;
    }


    /**
     * ???????????????
     */
    private void initConfig() {
        // ???????????????
        mAlbumSpec = AlbumSpec.getInstance();
        mGlobalSpec = GlobalSpec.getInstance();
        // ??????????????????
        if (mGlobalSpec.pictureStrategy != null) {
            // ?????????????????????????????????????????????????????????
            mPictureMediaStoreCompat = new MediaStoreCompat(getContext(), mGlobalSpec.pictureStrategy);
        } else {
            // ?????????????????????
            if (mGlobalSpec.saveStrategy == null) {
                throw new RuntimeException("Don't forget to set SaveStrategy.");
            } else {
                mPictureMediaStoreCompat = new MediaStoreCompat(getContext(), mGlobalSpec.saveStrategy);
            }
        }
        mVideoMediaStoreCompat = new MediaStoreCompat(getContext(),
                mGlobalSpec.videoStrategy == null ? mGlobalSpec.saveStrategy : mGlobalSpec.videoStrategy);
    }

    /**
     * ?????????view
     */
    private void initView(Bundle savedInstanceState) {
        // ?????????????????????
        ViewGroup.LayoutParams layoutParams = mViewHolder.toolbar.getLayoutParams();
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(mContext);
        layoutParams.height = layoutParams.height + statusBarHeight;
        mViewHolder.toolbar.setLayoutParams(layoutParams);
        mViewHolder.toolbar.setPadding(mViewHolder.toolbar.getPaddingLeft(), statusBarHeight,
                mViewHolder.toolbar.getPaddingRight(), mViewHolder.toolbar.getPaddingBottom());
        Drawable navigationIcon = mViewHolder.toolbar.getNavigationIcon();
        TypedArray ta = mContext.getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
        int color = ta.getColor(0, 0);
        ta.recycle();
        if (navigationIcon != null) {
            ColorFilterUtil.setColorFilterSrcIn(navigationIcon, color);
        }
        mSelectedCollection.onCreate(savedInstanceState, false);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        updateBottomToolbar();

        mAlbumsSpinnerAdapter = new AlbumsSpinnerAdapter(mContext, null, false);
        mAlbumsSpinner = new AlbumsSpinner(mContext);
        mAlbumsSpinner.setSelectedTextView(mViewHolder.selectedAlbum);
        mAlbumsSpinner.setPopupAnchorView(mViewHolder.toolbar);
        mAlbumsSpinner.setAdapter(mAlbumsSpinnerAdapter);
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    private void initListener() {
        // ????????????
        mViewHolder.imgClose.setOnClickListener(v -> mActivity.finish());

        // ????????????????????????
        mAlbumsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // ???????????????
                mAlbumCollection.setStateCurrentSelection(position);
                // ?????????????????????????????????
                mAlbumsSpinnerAdapter.getCursor().moveToPosition(position);
                // ????????????????????????
                Album album = Album.valueOf(mAlbumsSpinnerAdapter.getCursor());
                onAlbumSelected(album);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // ????????????
        mViewHolder.buttonPreview.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            intent.putExtra(BasePreviewActivity.IS_BY_ALBUM, true);
            startActivityForResult(intent, mGlobalSpec.requestCode);
            if (mGlobalSpec.isCutscenes) {
                mActivity.overridePendingTransition(R.anim.activity_open, 0);
            }
        });

        // ???????????????????????????
        mViewHolder.buttonApply.setOnClickListener(view -> {
            ArrayList<LocalFile> localFiles = mSelectedCollection.asListOfLocalFile();
            compressFile(localFiles);
        });

        // ????????????
        mViewHolder.originalLayout.setOnClickListener(view -> {
            if (getFragmentManager() != null) {
                // ??????????????????????????????????????????
                int count = countOverMaxSize();
                if (count > 0) {
                    IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                            getString(R.string.z_multi_library_error_over_original_count, count, mAlbumSpec.originalMaxSize));
                    incapableDialog.show(getFragmentManager(),
                            IncapableDialog.class.getName());
                    return;
                }

                // ????????????
                mOriginalEnable = !mOriginalEnable;
                mViewHolder.original.setChecked(mOriginalEnable);

                // ????????????????????????
                if (mAlbumSpec.onCheckedListener != null) {
                    mAlbumSpec.onCheckedListener.onCheck(mOriginalEnable);
                }

            }
        });

        // ??????Loading??????
        mViewHolder.pbLoading.setOnClickListener(v -> {
            // ????????????
            mCompressFileTask.cancel();
            // ??????????????????
            setControlTouchEnable(true);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mGlobalSpec.isCompressEnable()) {
            mGlobalSpec.videoCompressCoordinator.onCompressDestroy(MatissFragment.this.getClass());
            mGlobalSpec.videoCompressCoordinator = null;
        }
        // ????????????model
        mAlbumCollection.onDestroy();
        if (mCompressFileTask != null) {
            ThreadUtils.cancel(mCompressFileTask);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == android.R.id.home || super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        // ?????????????????????
        if (requestCode == mGlobalSpec.requestCode) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            // ?????????????????????
            ArrayList<MultiMedia> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            // ??????????????????
            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            // ????????????????????????????????????
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                if (selected != null) {
                    ArrayList<LocalFile> localFiles = new ArrayList<>(selected);
                    // ???????????????????????????????????????????????????????????????
                    setResultOk(localFiles);
                }
            } else {
                // ???????????????
                mSelectedCollection.overwrite(selected, collectionType);
                if (getFragmentManager() != null) {
                    Fragment mediaSelectionFragment = getFragmentManager().findFragmentByTag(
                            MediaSelectionFragment.class.getSimpleName());
                    if (mediaSelectionFragment instanceof MediaSelectionFragment) {
                        if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_IS_EDIT, false)) {
                            mIsRefresh = true;
                            albumsSpinnerNotifyData();
                            // ?????????????????????
                            ((MediaSelectionFragment) mediaSelectionFragment).restartLoaderMediaGrid();
                        } else {
                            // ???????????????
                            ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
                        }
                    }
                    // ????????????
                    updateBottomToolbar();
                }

            }
        }
    }

    /**
     * ??????????????????
     */
    private void updateBottomToolbar() {
        int selectedCount = mSelectedCollection.count();

        if (selectedCount == 0) {
            // ??????????????????????????????????????????
            mViewHolder.buttonPreview.setEnabled(false);
            mViewHolder.buttonApply.setEnabled(false);
            mViewHolder.buttonApply.setText(getString(R.string.z_multi_library_button_sure_default));
        } else if (selectedCount == 1 && mAlbumSpec.singleSelectionModeEnabled()) {
            // ????????????????????????
            mViewHolder.buttonPreview.setEnabled(true);
            mViewHolder.buttonApply.setText(R.string.z_multi_library_button_sure_default);
            mViewHolder.buttonApply.setEnabled(true);
        } else {
            // ?????????????????????
            mViewHolder.buttonPreview.setEnabled(true);
            mViewHolder.buttonApply.setEnabled(true);
            mViewHolder.buttonApply.setText(getString(R.string.z_multi_library_button_sure, selectedCount));
        }

        // ????????????????????????
        if (mAlbumSpec.originalable) {
            mViewHolder.originalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            mViewHolder.originalLayout.setVisibility(View.INVISIBLE);
        }

        showBottomView(selectedCount);
    }

    /**
     * ????????????????????????
     */
    private void updateOriginalState() {
        // ??????????????????
        mViewHolder.original.setChecked(mOriginalEnable);
        if (countOverMaxSize() > 0) {
            // ??????????????????
            if (mOriginalEnable) {
                // ???????????????????????? xx mb
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.z_multi_library_error_over_original_size, mAlbumSpec.originalMaxSize));
                if (this.getFragmentManager() == null) {
                    return;
                }
                incapableDialog.show(this.getFragmentManager(),
                        IncapableDialog.class.getName());

                // ????????????????????????
                mViewHolder.original.setChecked(false);
                mOriginalEnable = false;
            }
        }
    }

    /**
     * ??????????????????mb???????????????
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

    @Override
    public void onAlbumLoadFinished(final Cursor cursor) {
        // ??????????????????
        mAlbumsSpinnerAdapter.swapCursor(cursor);
        // ??????????????????
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
            mAlbumsSpinner.setSelection(getContext(),
                    mAlbumCollection.getCurrentSelection());
            Album album = Album.valueOf(cursor);
            onAlbumSelected(album);
        });
    }

    @Override
    public void onAlbumReset() {
        // ??????????????????
        mAlbumsSpinnerAdapter.swapCursor(null);
    }

    public void albumsSpinnerNotifyData() {
        mAlbumCollection.mLoadFinished = false;
        mAlbumCollection.restartLoadAlbums();
    }

    /**
     * ???????????????????????????
     *
     * @param album ??????
     */
    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            // ????????????????????????????????????????????????????????????view
            mViewHolder.container.setVisibility(View.GONE);
            mViewHolder.emptyView.setVisibility(View.VISIBLE);
        } else {
            // ?????????????????????????????????fragment???????????????????????????
            mViewHolder.container.setVisibility(View.VISIBLE);
            mViewHolder.emptyView.setVisibility(View.GONE);
            if (!mIsRefresh) {
                assert getArguments() != null;
                if (mFragmentLast != null) {
                    // ???????????????????????????????????????????????????????????????
                    mFragmentLast.onDestroyData();
                }
                mFragmentLast = MediaSelectionFragment.newInstance(album, getArguments().getInt(ARGUMENTS_MARGIN_BOTTOM));
                mActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mFragmentLast, MediaSelectionFragment.class.getSimpleName())
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();
        // ???????????????????????????
        if (mAlbumSpec.onSelectedListener != null) {
            mAlbumSpec.onSelectedListener.onSelected(mSelectedCollection.asListOfLocalFile());
        } else {
            // ?????????????????????????????????????????????????????????path
            mSelectedCollection.updatePath();
        }
    }

    @Override
    public void onMediaClick(Album album, MultiMedia item, int adapterPosition) {
        Intent intent = new Intent(mActivity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        intent.putExtra(BasePreviewActivity.IS_BY_ALBUM, true);
        startActivityForResult(intent, mGlobalSpec.requestCode);
        if (mGlobalSpec.isCutscenes) {
            mActivity.overridePendingTransition(R.anim.activity_open, 0);
        }
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    /**
     * ?????????????????????
     * ??????????????????table
     * ??????????????????????????????????????????????????????????????????
     *
     * @param count ?????????????????????
     */
    private void showBottomView(int count) {
        if (count > 0) {
            // ????????????
            mViewHolder.bottomToolbar.setVisibility(View.VISIBLE);
            // ??????????????????table
            ((MainActivity) mActivity).showHideTableLayout(false);
        } else {
            // ????????????
            mViewHolder.bottomToolbar.setVisibility(View.GONE);
            // ??????????????????table
            ((MainActivity) mActivity).showHideTableLayout(true);
        }
    }

    /**
     * ??????????????????
     *
     * @param localFiles ??????????????????????????????
     */
    private void compressFile(ArrayList<LocalFile> localFiles) {
        // ??????loading??????
        setControlTouchEnable(false);

        // ?????????????????????
        ThreadUtils.executeByIo(getCompressFileTask(localFiles));
    }

    /**
     * ????????????-?????????????????????
     *
     * @param localFiles ????????????????????????
     */
    private ThreadUtils.SimpleTask<ArrayList<LocalFile>> getCompressFileTask(ArrayList<LocalFile> localFiles) {
        mCompressFileTask = new ThreadUtils.SimpleTask<ArrayList<LocalFile>>() {

            @Override
            public ArrayList<LocalFile> doInBackground() {
                return mAlbumCompressFileTask.compressFileTaskDoInBackground(localFiles);
            }

            @Override
            public void onSuccess(ArrayList<LocalFile> result) {
                setResultOk(result);
            }

        };
        return mCompressFileTask;
    }

    /**
     * ??????Activity??????????????????
     *
     * @param localFiles ??????????????????????????????
     */
    private void setResultOk(ArrayList<LocalFile> localFiles) {
        Log.d(TAG, "setResultOk");
        if (mGlobalSpec.onResultCallbackListener == null) {
            // ????????????????????????url??????
            Intent result = new Intent();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION_LOCAL_FILE, localFiles);
            // ??????????????????
            result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            mActivity.setResult(RESULT_OK, result);
            mActivity.finish();
        } else {
            mGlobalSpec.onResultCallbackListener.onResult(localFiles);
            mActivity.finish();
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private void setControlTouchEnable(boolean enable) {
        mViewHolder.container.setEnabled(enable);
        // ???????????????????????? ????????? view,????????????
        if (!enable) {
            mViewHolder.pbLoading.setVisibility(View.VISIBLE);
            mViewHolder.buttonApply.setVisibility(View.GONE);
            mViewHolder.buttonPreview.setEnabled(false);
        } else {
            mViewHolder.pbLoading.setVisibility(View.GONE);
            mViewHolder.buttonApply.setVisibility(View.VISIBLE);
            mViewHolder.buttonPreview.setEnabled(true);
        }
    }

    public static class ViewHolder {
        public View rootView;
        public TextView selectedAlbum;
        public Toolbar toolbar;
        public TextView buttonPreview;
        public CheckRadioView original;
        public LinearLayout originalLayout;
        public TextView buttonApply;
        public FrameLayout bottomToolbar;
        public ControlTouchFrameLayout container;
        public TextView emptyViewContent;
        public FrameLayout emptyView;
        public RelativeLayout root;
        public ImageView imgClose;
        public ProgressBar pbLoading;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.selectedAlbum = rootView.findViewById(R.id.selectedAlbum);
            this.toolbar = rootView.findViewById(R.id.toolbar);
            this.buttonPreview = rootView.findViewById(R.id.buttonPreview);
            this.original = rootView.findViewById(R.id.original);
            this.originalLayout = rootView.findViewById(R.id.originalLayout);
            this.buttonApply = rootView.findViewById(R.id.buttonApply);
            this.bottomToolbar = rootView.findViewById(R.id.bottomToolbar);
            this.container = rootView.findViewById(R.id.container);
            this.emptyViewContent = rootView.findViewById(R.id.emptyViewContent);
            this.emptyView = rootView.findViewById(R.id.emptyView);
            this.root = rootView.findViewById(R.id.root);
            this.imgClose = rootView.findViewById(R.id.imgClose);
            this.pbLoading = rootView.findViewById(R.id.pbLoading);
        }

    }
}
