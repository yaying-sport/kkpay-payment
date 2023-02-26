package com.kaiserkalep.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.adapter.PagerBaseAdapter;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.ui.fragmnet.MineFragment;
import com.kaiserkalep.ui.fragmnet.OrderFragment;
import com.kaiserkalep.ui.fragmnet.PhotoViewFragment;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.PhotoView.PhotoView;
import com.kaiserkalep.widgets.ViewPagerFixed;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import static com.kaiserkalep.constants.Config.ZERO;

import androidx.viewpager.widget.ViewPager;

/**
 * 查看图片
 */
public class PhotoDetailActivity extends ZZActivity {

    //    @BindView(R.id.loading)
//    LoadingLayout mLoading;
    @BindView(R.id.viewPager)
    ViewPagerFixed mViewPager;
//    @BindView(R.id.iv_photo)
//    PhotoView ivPhoto;
//    @BindView(R.id.iv_error)
//    ImageView ivError;

    private String title = "";
    private String imgUrl;
    private String canDelete = "0";
    private boolean isFail;
    public List<PageBaseFragment> mDataList = new ArrayList<>();

    @Override
    public int getViewId() {
        return R.layout.activity_paypingzeng;
    }

    @Override
    public void onResume() {
        super.onResume();
        onPageStart(title);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPageEnd(title);
    }

    @Override
    public void afterViews() {
        title = MyApp.getLanguageString(getContext(), R.string.photo_preview);
        try {
            HashMap<String, String> urlParam = getUrlParam();
            if (null != urlParam && urlParam.size() > ZERO) {
                title = urlParam.get(StringConstants.TITLE);
                imgUrl = urlParam.get(StringConstants.DATA);
                canDelete = urlParam.get(StringConstants.DELETE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        commTitle.init(title);
        mDataList.clear();
        String[] imgUrlList = imgUrl.split(StringConstants.comma);
        for (String s : imgUrlList) {
            if (!TextUtils.isEmpty(s)) {
                PhotoViewFragment pf = new PhotoViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(StringConstants.URL, s);
                bundle.putBoolean(StringConstants.DELETE, "1".equals(canDelete));
                pf.setArguments(bundle);
                mDataList.add(pf);
            }
        }
        mViewPager.setAdapter(new PagerBaseAdapter(getSupportFragmentManager(), mDataList));
        mViewPager.setOffscreenPageLimit(mDataList.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (null != mDataList && mDataList.size() > position) {
//                    MainActivity.this.position = position;
                    mDataList.get(position).onUserViewVisible();
                }
            }
        });
//        initListener();
//        showImage(imgUrl);
    }


}