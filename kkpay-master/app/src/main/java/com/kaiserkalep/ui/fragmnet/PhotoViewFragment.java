package com.kaiserkalep.ui.fragmnet;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.PhotoView.PhotoView;
import com.kaiserkalep.widgets.glide.GlideUtil;

import butterknife.BindView;
import butterknife.OnClick;


public class PhotoViewFragment extends PageBaseFragment {

    //    @BindView(R.id.loading)
//    LoadingLayout mLoading;
    @BindView(R.id.iv_photo)
    PhotoView ivPhoto;
    @BindView(R.id.iv_error)
    ImageView ivError;

    @BindView(R.id.delete_Btn)
    Button delete_Btn;

    public String imgUrl;
    private boolean isFail;
    public boolean canDelete;

//    public PhotoViewFragment(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }


    @Override
    public int getViewId() {
        return R.layout.fragment_photo_view;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        Bundle bundle = getArguments();
        if (bundle != null) {
            imgUrl = bundle.getString(StringConstants.URL);
            canDelete = bundle.getBoolean(StringConstants.DELETE);
        } else {
            getActivity().finish();
            return;
        }
        ivError.setOnClickListener(v -> {
            if (isFail) {
                isFail = false;
                showImage(imgUrl);
            }
        });
        showImage(imgUrl);
        delete_Btn.setVisibility(canDelete ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.delete_Btn)
    void delete_Btn() {
        setResult(RESULT_OK, null);
        getActivity().finish();
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
//        initData();
    }

    @Override
    protected void onUserDoubleVisible() {
        super.onUserDoubleVisible();

    }

    /**
     * 初始化magicIndicator 和viewpager
     */
    private void initData() {
        Context context = getContext();
        if (null == context) {
            return;
        }
        showImage(imgUrl);
    }


    /**
     * 加载图片
     *
     * @param Url
     */
    public void showImage(String Url) {
//        if (null != mLoading) {
//            mLoading.showLoading();
        showDialog();
        ivPhoto.setVisibility(View.VISIBLE);
        ivError.setVisibility(View.GONE);
//        }

        GlideUtil.loadImageBitmap(getContext(), BaseNetUtil.jointUrl(Url), bitmap -> {
//            if (null != mLoading) {
//                mLoading.showContent();
//            }
            closeDialog();
            isFail = null == bitmap;
            if (isFail) {
                ivPhoto.setVisibility(View.GONE);
                ivError.setVisibility(View.VISIBLE);
            } else {
                ivPhoto.setImageBitmap(bitmap);
                ivPhoto.setVisibility(View.VISIBLE);
                ivError.setVisibility(View.GONE);
            }
        });
    }

}
