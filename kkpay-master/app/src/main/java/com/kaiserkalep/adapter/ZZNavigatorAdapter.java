package com.kaiserkalep.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.viewpager.widget.ViewPager;


import com.kaiserkalep.R;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jack
 * @Date: 2020/8/15 15:53
 * @Description:
 */
public class ZZNavigatorAdapter extends CommonNavigatorAdapter {


    private List<String> mTitles;
    private ViewPager mViewPager;
    private int mTextSize = 13;//默认字体大小
    //文本显示单行
    private boolean isSingleLine = true;
    private int padding10;
    private int normal;
    private int select;
    private int mMode = LinePagerIndicator.MODE_EXACTLY;  // MODE_EXACTLY
    private boolean needIndicator = true;
    private Typeface typeface;
    private float mMinScale = 1f;

    public ZZNavigatorAdapter(Context context, List<String> mTitles, ViewPager mViewPager) {
        this.mTitles = mTitles;
        this.mViewPager = mViewPager;
        normal = SkinResourcesUtils.getColor(R.color.rr_navigator_normal);
        select = SkinResourcesUtils.getColor(R.color.rr_navigator_select);
        padding10 = UIUtil.dip2px(context, 10);
    }

    public ZZNavigatorAdapter(Context context, List<String> mTitles, ViewPager mViewPager, int select, int normal) {
        this.mTitles = mTitles;
        this.mViewPager = mViewPager;
        this.select = select;
        this.normal = normal;
        padding10 = UIUtil.dip2px(context, 10);
    }

    @Override
    public int getCount() {
        return mTitles == null ? 0 : mTitles.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ScaleTransitionPagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setText(mTitles != null && mTitles.size() > 0 ? mTitles.get(index) : "");
        simplePagerTitleView.setSingleLine(isSingleLine);
        simplePagerTitleView.setTextSize(mTextSize);

        if (typeface != null) {
            simplePagerTitleView.setTypeface(typeface);
        }
        simplePagerTitleView.setMinScale(mMinScale);
        simplePagerTitleView.setPadding(padding10, 0, padding10, 0);
        simplePagerTitleView.setNormalColor(normal);
        simplePagerTitleView.setSelectedColor(select);
        simplePagerTitleView.setOnClickListener(v -> {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(index, true);
            }
        });

        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        if (!needIndicator) {
            return null;
        }
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(mMode);
        indicator.setLineHeight(UIUtil.dip2px(context, 2));
        indicator.setLineWidth(UIUtil.dip2px(context, 39));
        indicator.setRoundRadius(UIUtil.dip2px(context, 2));
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2f));
        indicator.setColors(select);
        return indicator;
    }

    @Override
    public float getTitleWeight(Context context, int index) {
        return 1.0f;
    }


    public boolean isNeedIndicator() {
        return needIndicator;
    }

    public void setNeedIndicator(boolean needIndicator) {
        this.needIndicator = needIndicator;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float mMinScale) {
        this.mMinScale = mMinScale;
    }

    public void setPadding10(int padding10) {
        this.padding10 = padding10;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }
}

