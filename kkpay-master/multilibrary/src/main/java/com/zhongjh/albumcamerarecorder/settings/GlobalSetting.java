package com.zhongjh.albumcamerarecorder.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

import com.zhongjh.albumcamerarecorder.MainActivity;
import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.engine.ImageEngine;
import com.zhongjh.albumcamerarecorder.listener.ImageCompressionInterface;
import com.zhongjh.albumcamerarecorder.listener.OnMainListener;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.preview.AlbumPreviewActivity;
import com.zhongjh.albumcamerarecorder.preview.BasePreviewActivity;
import com.zhongjh.albumcamerarecorder.settings.api.GlobalSettingApi;
import com.zhongjh.albumcamerarecorder.utils.SelectableUtils;
import com.zhongjh.common.coordinator.VideoCompressCoordinator;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_BEHIND;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
import static com.zhongjh.albumcamerarecorder.album.model.SelectedItemCollection.COLLECTION_IMAGE;
import static com.zhongjh.albumcamerarecorder.album.model.SelectedItemCollection.STATE_COLLECTION_TYPE;
import static com.zhongjh.albumcamerarecorder.album.model.SelectedItemCollection.STATE_SELECTION;

/**
 * ???????????????????????????????????? API???
 *
 * @author zhongjh
 * @date 2018/9/28
 */
public final class GlobalSetting implements GlobalSettingApi {

    private final MultiMediaSetting mMultiMediaSetting;
    private final GlobalSpec mGlobalSpec;

    @IntDef(value = {
            SCREEN_ORIENTATION_UNSPECIFIED,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_USER,
            SCREEN_ORIENTATION_BEHIND,
            SCREEN_ORIENTATION_SENSOR,
            SCREEN_ORIENTATION_NOSENSOR,
            SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_FULL_SENSOR,
            SCREEN_ORIENTATION_USER_LANDSCAPE,
            SCREEN_ORIENTATION_USER_PORTRAIT,
            SCREEN_ORIENTATION_FULL_USER,
            SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenOrientation {
    }

    @Override
    public void onDestroy() {
        mGlobalSpec.onMainListener = null;
        mGlobalSpec.onResultCallbackListener = null;
        if (mGlobalSpec.albumSetting != null) {
            mGlobalSpec.albumSetting.onDestroy();
        }
        if (mGlobalSpec.cameraSetting != null) {
            mGlobalSpec.cameraSetting.onDestroy();
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param multiMediaSetting ??? requester context wrapper.
     * @param mimeTypes         ?????????????????? {@link MimeType} ??????
     */
    GlobalSetting(MultiMediaSetting multiMediaSetting, @NonNull Set<MimeType> mimeTypes) {
        mMultiMediaSetting = multiMediaSetting;
        mGlobalSpec = GlobalSpec.getCleanInstance();
        mGlobalSpec.setMimeTypeSet(mimeTypes);

//        mGlobalSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    @Override
    public GlobalSetting albumSetting(AlbumSetting albumSetting) {
        mGlobalSpec.albumSetting = albumSetting;
        return this;
    }

    @Override
    public GlobalSetting cameraSetting(CameraSetting cameraSetting) {
        mGlobalSpec.cameraSetting = cameraSetting;
        return this;
    }

    @Override
    public GlobalSetting recorderSetting(RecorderSetting recorderSetting) {
        mGlobalSpec.recorderSetting = recorderSetting;
        return this;
    }

    @Override
    public GlobalSetting theme(@StyleRes int themeId) {
        mGlobalSpec.themeId = themeId;
        return this;
    }

    @Override
    public GlobalSetting defaultPosition(int defaultPosition) {
        mGlobalSpec.defaultPosition = defaultPosition;
        return null;
    }


    @Override
    public GlobalSetting maxSelectablePerMediaType(Integer maxSelectable, Integer maxImageSelectable, Integer maxVideoSelectable, Integer maxAudioSelectable,
                                                   int alreadyImageCount, int alreadyVideoCount, int alreadyAudioCount) {
        if (maxSelectable == null && maxImageSelectable == null) {
            throw new IllegalStateException("maxSelectablePerMediaType ??????????????? maxSelectable ???null????????? maxImageSelectable ?????????0??????0????????????");
        }
        if (maxSelectable == null && maxVideoSelectable == null) {
            throw new IllegalStateException("maxSelectablePerMediaType ??????????????? maxSelectable ???null????????? maxVideoSelectable ?????????0??????0????????????");
        }
        if (maxSelectable == null && maxAudioSelectable == null) {
            throw new IllegalStateException("maxSelectablePerMediaType ??????????????? maxSelectable ???null????????? maxAudioSelectable ?????????0??????0????????????");
        }
        if (maxSelectable != null && maxImageSelectable != null && maxImageSelectable > maxSelectable) {
            throw new IllegalStateException("maxSelectable ????????? maxImageSelectable ???");
        }
        if (maxSelectable != null && maxVideoSelectable != null && maxVideoSelectable > maxSelectable) {
            throw new IllegalStateException("maxSelectable ????????? maxVideoSelectable ???");
        }
        if (maxSelectable != null && maxAudioSelectable != null && maxAudioSelectable > maxSelectable) {
            throw new IllegalStateException("maxSelectable ????????? maxAudioSelectable ???");
        }

        // ??????
        if (maxSelectable != null) {
            mGlobalSpec.maxSelectable = maxSelectable - (alreadyImageCount + alreadyVideoCount + alreadyAudioCount);
        }
        if (maxImageSelectable != null) {
            mGlobalSpec.maxImageSelectable = maxImageSelectable - alreadyImageCount;
        } else {
            mGlobalSpec.maxImageSelectable = null;
        }
        if (maxVideoSelectable != null) {
            mGlobalSpec.maxVideoSelectable = maxVideoSelectable - alreadyVideoCount;
        } else {
            mGlobalSpec.maxVideoSelectable = null;
        }
        if (maxAudioSelectable != null) {
            mGlobalSpec.maxAudioSelectable = maxAudioSelectable - alreadyAudioCount;
        } else {
            mGlobalSpec.maxAudioSelectable = null;
        }
        return this;
    }

    @Override
    public GlobalSetting alreadyCount(int alreadyImageCount, int alreadyVideoCount, int alreadyAudioCount) {
        // ??????
        if (mGlobalSpec.maxSelectable != null) {
            mGlobalSpec.maxSelectable = mGlobalSpec.maxSelectable - (alreadyImageCount + alreadyVideoCount + alreadyAudioCount);
        }
        if (mGlobalSpec.maxImageSelectable != null) {
            mGlobalSpec.maxImageSelectable = mGlobalSpec.maxImageSelectable - alreadyImageCount;
        }
        if (mGlobalSpec.maxVideoSelectable != null) {
            mGlobalSpec.maxVideoSelectable = mGlobalSpec.maxVideoSelectable - alreadyVideoCount;
        }
        if (mGlobalSpec.maxAudioSelectable != null) {
            mGlobalSpec.maxAudioSelectable = mGlobalSpec.maxAudioSelectable - alreadyAudioCount;
        }
        return this;
    }

    @Override
    public GlobalSetting allStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.saveStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting pictureStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.pictureStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting videoStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.videoStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting audioStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.audioStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting imageEngine(ImageEngine imageEngine) {
        mGlobalSpec.imageEngine = imageEngine;
        return this;
    }

    @Override
    public GlobalSetting isCutscenes(boolean isCutscenes) {
        mGlobalSpec.isCutscenes = isCutscenes;
        return this;
    }

    @Override
    public GlobalSetting setRequestedOrientation(@ScreenOrientation int requestedOrientation) {
        mGlobalSpec.orientation = requestedOrientation;
        return this;
    }

    @Override
    public GlobalSetting isImageEdit(boolean isImageEdit) {
        mGlobalSpec.isImageEdit = isImageEdit;
        return this;
    }

    @Override
    public GlobalSetting setOnImageCompressionInterface(@Nullable ImageCompressionInterface listener) {
        mGlobalSpec.imageCompressionInterface = listener;
        return this;
    }

    @Override
    public GlobalSetting videoCompress(VideoCompressCoordinator videoCompressManager) {
        mGlobalSpec.videoCompressCoordinator = videoCompressManager;
        return this;
    }

    @NonNull
    @Override
    public GlobalSetting setOnMainListener(@Nullable OnMainListener listener) {
        mGlobalSpec.onMainListener = listener;
        return this;
    }

    @Override
    public void forResult(int requestCode) {
        mGlobalSpec.requestCode = requestCode;
        // ??????????????????null
        mGlobalSpec.onResultCallbackListener = null;
        openMain(requestCode);
    }

    @Override
    public void forResult(OnResultCallbackListener listener) {
        // ??????????????????
        mGlobalSpec.onResultCallbackListener = new WeakReference<>(listener).get();
        openMain(null);
    }

    /**
     * ????????????????????????????????? - ???????????????????????????
     *
     * @param activity    ??????
     * @param requestCode ?????????
     * @param list        ?????????
     * @param position    ?????????????????????
     */
    @Override
    public void openPreviewData(Activity activity, int requestCode,
                                ArrayList<? extends MultiMedia> list, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, list);
        bundle.putInt(STATE_COLLECTION_TYPE, COLLECTION_IMAGE);

        Intent intent = new Intent(activity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, list.get(position));
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, bundle);
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
        intent.putExtra(BasePreviewActivity.EXTRA_IS_ALLOW_REPEAT, true);
        intent.putExtra(BasePreviewActivity.IS_SELECTED_CHECK, false);
        intent.putExtra(BasePreviewActivity.IS_EXTERNAL_USERS, true);
        intent.putExtra(BasePreviewActivity.IS_BY_PROGRESS_GRIDVIEW, true);
        GlobalSpec globalSpec = GlobalSpec.getInstance();
        activity.startActivityForResult(intent, requestCode);
        if (globalSpec.isCutscenes) {
            activity.overridePendingTransition(R.anim.activity_open, 0);
        }
    }

    /**
     * ???????????????????????? - ?????????????????????
     *
     * @param activity ??????
     * @param list     ??????id?????????
     * @param position ?????????????????????
     */
    @Override
    public void openPreviewResourceId(Activity activity, ArrayList<Integer> list, int position) {
        ArrayList<MultiMedia> multiMedias = new ArrayList<>();
        for (Integer item : list) {
            MultiMedia multiMedia = new MultiMedia();
            multiMedia.setDrawableId(item);
            multiMedias.add(multiMedia);
        }
        openPreview(activity, multiMedias, position);
    }

    /**
     * ???????????????????????? - ?????????????????????
     *
     * @param activity ??????
     * @param list     ????????????????????????
     * @param position ?????????????????????
     */
    @Override
    public void openPreviewPath(Activity activity, ArrayList<String> list, int position) {
        ArrayList<MultiMedia> multiMedias = new ArrayList<>();
        for (String item : list) {
            MultiMedia multiMedia = new MultiMedia();
            multiMedia.setUrl(item);
            multiMedias.add(multiMedia);
        }
        openPreview(activity, multiMedias, position);
    }

    private void openMain(Integer requestCode) {
        Activity activity = mMultiMediaSetting.getActivity();
        if (activity == null) {
            return;
        }
        // ??????
        int numItems = 0;
        // ???????????????????????????????????????
        if (mGlobalSpec.albumSetting != null) {
            numItems++;
        }
        if (mGlobalSpec.cameraSetting != null) {
            numItems++;
        }
        if (mGlobalSpec.recorderSetting != null && numItems <= 0) {
            if (SelectableUtils.getAudioMaxCount() > 0) {
                numItems++;
            } else {
                if (mGlobalSpec.onMainListener != null) {
                    mGlobalSpec.onMainListener.onOpenFail(activity.getResources().getString(R.string.z_multi_library_the_recording_limit_has_been_reached));
                } else {
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.z_multi_library_the_recording_limit_has_been_reached), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (numItems <= 0) {
            throw new IllegalStateException(activity.getResources().getString(R.string.z_one_of_these_three_albumSetting_camerasSetting_and_recordDerSetting_must_be_set));
        }

        Intent intent = new Intent(activity, MainActivity.class);

        Fragment fragment = mMultiMediaSetting.getFragment();
        if (fragment != null) {
            if (requestCode != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                fragment.startActivity(intent);
            }
        } else {
            if (requestCode != null) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
            }
            if (mGlobalSpec.isCutscenes) {
                activity.overridePendingTransition(R.anim.activity_open, 0);
            }
        }
    }

    /**
     * ????????? {@link #openPreviewResourceId} ??? {@link #openPreviewPath} ???????????????
     *
     * @param activity    ??????
     * @param multiMedias ?????????
     * @param position    ?????????????????????
     */
    private static void openPreview(Activity activity, ArrayList<MultiMedia> multiMedias, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, multiMedias);
        bundle.putInt(STATE_COLLECTION_TYPE, COLLECTION_IMAGE);

        Intent intent = new Intent(activity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, multiMedias.get(position));
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, bundle);
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
        intent.putExtra(BasePreviewActivity.EXTRA_IS_ALLOW_REPEAT, true);
        intent.putExtra(BasePreviewActivity.IS_SELECTED_CHECK, false);
        intent.putExtra(BasePreviewActivity.ENABLE_OPERATION, false);
        intent.putExtra(BasePreviewActivity.IS_EXTERNAL_USERS, true);
        GlobalSpec globalSpec = GlobalSpec.getInstance();
        activity.startActivityForResult(intent, globalSpec.requestCode);
        if (globalSpec.isCutscenes) {
            activity.overridePendingTransition(R.anim.activity_open, 0);
        }
    }

}
