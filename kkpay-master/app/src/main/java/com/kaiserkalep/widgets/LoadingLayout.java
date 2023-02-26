/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaiserkalep.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.widget.NestedScrollView;

import com.kaiserkalep.MyApp;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityBase;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.attr.base.AttrFactory;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.HashMap;
import java.util.Map;


public class LoadingLayout extends FrameLayout {

    private ShadowLayout img;
    private int buttonBackgroundId;
    private NestedScrollView nestedScrollError, nestedScrollEmpty;
    private SVGAImageView svgaLoading;
    //    private ViewSkeletonScreen skeletonScreen;

    public interface OnInflateListener {
        void onInflate(View inflated);
    }

    public static LoadingLayout wrap(Activity activity) {
        return wrap(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
    }

    public static LoadingLayout wrap(Fragment fragment) {
        return wrap(fragment.getView());
    }

    public static LoadingLayout wrap(View view) {
        if (view == null) {
            throw new RuntimeException("content view can not be null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (view == null) {
            throw new RuntimeException("parent view can not be null");
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int index = parent.indexOfChild(view);
        parent.removeView(view);

        LoadingLayout layout = new LoadingLayout(view.getContext());
        parent.addView(layout, index, lp);
        layout.addView(view);
        layout.setContentView(view);
        return layout;
    }

    int mEmptyImage;
    CharSequence mEmptyText;

    int mErrorImage;
    CharSequence mErrorText, mRetryText;
    OnClickListener mRetryButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mRetryListener != null) {
                mRetryListener.onClick(v);
            }
        }
    };
    OnClickListener mRetryListener;

    OnInflateListener mOnEmptyInflateListener;
    OnInflateListener mOnErrorInflateListener;

    int mTextColor, mTextSize, loadingMarginTop;
    int mButtonTextColor = R.color.colorPrimary;
    int mButtonTextSize;
    Drawable mButtonBackground;
    int mEmptyResId = NO_ID, mLoadingResId = NO_ID, mErrorResId = NO_ID;
    int mContentId = NO_ID;
    boolean isNowLoading;
    boolean isCommLoading;
    boolean isScrollingEnabled;
    boolean needRemoveChild;

    Map<Integer, View> mLayouts = new HashMap<>();


    /**
     * 初始状态(未showLoading)
     */
    public final static int TYPE_INIT = -1;

    /**
     * 加载中
     */
    public final static int TYPE_LOADING = 0;

    /**
     * 显示内容
     */
    public final static int TYPE_CONTENT = 1;

    /**
     * 空太页,无按钮
     */
    public final static int TYPE_EMPTY = 2;

    /**
     * 网络错误页(也可作为带按钮空太页)
     */
    public final static int TYPE_ERROR = 3;

    /**
     * 隐藏所有内容
     */
    public final static int TYPE_HINT = 4;

    /**
     * 显示状态
     */
    int status = TYPE_INIT;

    public LoadingLayout(Context context) {
        this(context, null, R.attr.styleLoadingLayout);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.styleLoadingLayout);
    }

    @SuppressLint("ResourceAsColor")
    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, R.style.LoadingLayout_Style);
        mEmptyImage = a.getResourceId(R.styleable.LoadingLayout_llEmptyImage, NO_ID);
        mEmptyText = a.getString(R.styleable.LoadingLayout_llEmptyText);

        mErrorImage = a.getResourceId(R.styleable.LoadingLayout_llErrorImage, NO_ID);
        mErrorText = a.getString(R.styleable.LoadingLayout_llErrorText);
        mRetryText = a.getString(R.styleable.LoadingLayout_llRetryText);

//        mTextColor = a.getColor(R.styleable.LoadingLayout_llTextColor, R.color.loading_layout_text_color);
        mTextColor = SkinResourcesUtils.getColor(R.color.loading_layout_text_color);
        mTextSize = a.getDimensionPixelSize(R.styleable.LoadingLayout_llTextSize, dp2px(14));
        isNowLoading = a.getBoolean(R.styleable.LoadingLayout_isNowLoading, true);
        isCommLoading = a.getBoolean(R.styleable.LoadingLayout_isCommLoading, true);
        isScrollingEnabled = a.getBoolean(R.styleable.LoadingLayout_isScrollingEnabled, true);
        needRemoveChild = a.getBoolean(R.styleable.LoadingLayout_needRemoveChild, true);

//        mButtonTextColor = a.getColor(R.styleable.LoadingLayout_llButtonTextColor, R.color.loading_layout_btn_color);

        mButtonTextColor = SkinResourcesUtils.getColor(R.color.loading_layout_btn_color);
        mButtonTextSize = a.getDimensionPixelSize(R.styleable.LoadingLayout_llButtonTextSize, dp2px(14));
//        mButtonBackground = a.getDrawable(R.styleable.LoadingLayout_llButtonBackground);
        buttonBackgroundId = a.getResourceId(R.styleable.LoadingLayout_llButtonBackground, NO_ID);
        mButtonBackground = SkinResourcesUtils.getDrawable(buttonBackgroundId);

        mEmptyResId = a.getResourceId(R.styleable.LoadingLayout_llEmptyResId, R.layout._loading_layout_empty);
        mLoadingResId = a.getResourceId(R.styleable.LoadingLayout_llLoadingResId, R.layout._loading_layout_loading);
        mErrorResId = a.getResourceId(R.styleable.LoadingLayout_llErrorResId, R.layout._loading_layout_error);
        a.recycle();
    }

    int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }


    LayoutInflater mInflater;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }
        View view = getChildAt(0);
        setContentView(view);
        if (isNowLoading) {
            showLoading();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!needRemoveChild) {
            return;
        }
        clearAllChildView();
    }

    public void clearAllChildView() {
        if (getChildCount() > 1) {
            removeViews(0, getChildCount() - 1);
        }
        if (CommonUtils.MapNotNull(mLayouts)) {
            mLayouts.clear();
        }
    }

    public interface OnViewShowHideListener {
        /**
         * 状态变更
         *
         * @param status {@link LoadingLayout#status}
         */
        void onShowType(int status);
    }

    private OnViewShowHideListener viewShowHideListener;

    public void addViewShowHideListener(OnViewShowHideListener viewShowHideListener) {
        this.viewShowHideListener = viewShowHideListener;
    }

    private void setContentView(View view) {
        mContentId = view.getId();
        mLayouts.put(mContentId, view);
    }

    public LoadingLayout setLoading(@LayoutRes int id) {
        if (mLoadingResId != id) {
            remove(mLoadingResId);
            mLoadingResId = id;
        }
        return this;
    }

    public LoadingLayout setEmpty(@LayoutRes int id) {
        if (mEmptyResId != id) {
            remove(mEmptyResId);
            mEmptyResId = id;
        }
        return this;
    }

    public LoadingLayout setOnEmptyInflateListener(OnInflateListener listener) {
        mOnEmptyInflateListener = listener;
        if (mOnEmptyInflateListener != null && mLayouts.containsKey(mEmptyResId)) {
            listener.onInflate(mLayouts.get(mEmptyResId));
        }
        return this;
    }

    public LoadingLayout setOnErrorInflateListener(OnInflateListener listener) {
        mOnErrorInflateListener = listener;
        if (mOnErrorInflateListener != null && mLayouts.containsKey(mErrorResId)) {
            listener.onInflate(mLayouts.get(mErrorResId));
        }
        return this;
    }

    public LoadingLayout setLoadBackgroud(@DrawableRes int resId) {
        ViewGroupbackgroud(mLoadingResId, R.id.nested_scroll, resId);
        return this;
    }

    public LoadingLayout setEmptyImage(@DrawableRes int resId) {
        mEmptyImage = resId;
        image(mEmptyResId, R.id.empty_image, mEmptyImage);
        return this;
    }

    public LoadingLayout setEmptyText(String value) {
        mEmptyText = value;
        text(mEmptyResId, R.id.empty_text, mEmptyText);
        return this;
    }

    public LoadingLayout setErrorImage(@DrawableRes int resId) {
        mErrorImage = resId;
        image(mErrorResId, R.id.error_image, mErrorImage);
        return this;
    }

    public LoadingLayout setErrorText(String value) {
        mErrorText = value;
        text(mErrorResId, R.id.error_text, mErrorText);
        return this;
    }

    public LoadingLayout setRetryText(String text) {
        mRetryText = text;
        text(mErrorResId, R.id.retry_button, mRetryText);
        return this;
    }

    public LoadingLayout setRetryListener(OnClickListener listener) {
        mRetryListener = listener;
        return this;
    }


//    public LoadingLayout setTextColor(@ColorInt int color) {
//        mTextColor = color;
//        return this;
//    }
//    public LoadingLayout setTextSize(@ColorInt int dp) {
//        mTextColor = dp2px(dp);
//        return this;
//    }
//    public LoadingLayout setButtonTextColor(@ColorInt int color) {
//        mButtonTextColor = color;
//        return this;
//    }
//    public LoadingLayout setButtonTextSize(@ColorInt int dp) {
//        mButtonTextColor = dp2px(dp);
//        return this;
//    }
//    public LoadingLayout setButtonBackground(Drawable drawable) {
//        mButtonBackground = drawable;
//        return this;
//    }

    private void setStatus(int status) {
        this.status = status;
        if (viewShowHideListener != null) {
            viewShowHideListener.onShowType(status);
        }
    }

    public void showLoading() {
        if (getStatus() != TYPE_LOADING) {
            show(mLoadingResId);
            setStatus(TYPE_LOADING);
            SVGAManager.getManager().getLoadingEntity(listener);
        }
    }

    SVGAManager.LoadingSVGAListener listener = o -> {
        if (svgaLoading != null) {
            svgaLoading.setVideoItem(o);
            svgaLoading.stepToFrame(0, true);
        }
    };

    public void showContent() {
        if (getStatus() != TYPE_CONTENT) {
            show(mContentId);
            setStatus(TYPE_CONTENT);
        }
    }

    public void showEmpty() {
        if (getStatus() != TYPE_EMPTY) {
            show(mEmptyResId);
            setStatus(TYPE_EMPTY);
        }
    }

    public void showError(RefreshLayout refreshLayout) {
        showError();
        closeRefresh(refreshLayout);
    }

    public void showError() {
        if (getStatus() != TYPE_ERROR) {
            show(mErrorResId);
            setStatus(TYPE_ERROR);
        }
    }

    public void hintContent() {
        if (getStatus() != TYPE_HINT) {
            if (mLayouts != null && mLayouts.size() > 0) {
                for (View view : mLayouts.values()) {
                    view.setVisibility(GONE);
                }
            }
            setStatus(TYPE_HINT);
        }
    }

    private void show(int layoutId) {
        if (CommonUtils.MapNotNull(mLayouts)) {
            for (View view : mLayouts.values()) {
                view.setVisibility(GONE);
            }
            layout(layoutId).setVisibility(VISIBLE);
        }
    }

    private void remove(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            View vg = mLayouts.remove(layoutId);
            removeView(vg);
        }
    }

    @SuppressLint("ResourceAsColor")
    private View layout(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts.get(layoutId);
        }
        View layout = mInflater.inflate(layoutId, this, false);
        layout.setVisibility(GONE);
        addView(layout);
        mLayouts.put(layoutId, layout);
        Context context = getContext();
        ActivityBase activityBase = null;
        if (context != null && context instanceof ActivityBase) {
            activityBase = (ActivityBase) context;
        }

        if (layoutId == mEmptyResId) {
            ImageView img = layout.findViewById(R.id.empty_image);
            nestedScrollEmpty = layout.findViewById(R.id.nested_scroll);
            setNestedScrollEmpty(isScrollingEnabled);
            if (img != null) {
                img.setImageDrawable(SkinResourcesUtils.getDrawable(mEmptyImage));
                if (activityBase != null) {
                    activityBase.dynamicAddView(img, AttrFactory.src, mEmptyImage);
                }
            }
            TextView view = layout.findViewById(R.id.empty_text);
            if (view != null) {
                view.setText(mEmptyText);
                view.setTextColor(mTextColor);
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                if (activityBase != null) {
                    activityBase.dynamicAddView(view, AttrFactory.textColor, R.color.loading_layout_text_color);
                }
            }
            if (mOnEmptyInflateListener != null) {
                mOnEmptyInflateListener.onInflate(layout);
            }
        } else if (layoutId == mErrorResId) {
            nestedScrollError = layout.findViewById(R.id.nested_scroll);
            setNestedScrollError(isScrollingEnabled);
            ImageView img = layout.findViewById(R.id.error_image);
            if (img != null) {
                img.setImageDrawable(SkinResourcesUtils.getDrawable(mErrorImage));
                if (activityBase != null) {
                    activityBase.dynamicAddView(img, AttrFactory.src, mErrorImage);
                }
            }
            TextView txt = layout.findViewById(R.id.error_text);
            if (txt != null) {
                txt.setText(mErrorText);
                txt.setTextColor(mTextColor);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                if (activityBase != null) {
                    activityBase.dynamicAddView(txt, AttrFactory.textColor, R.color.loading_layout_text_color);
                }
            }
            TextView btn = layout.findViewById(R.id.retry_button);
            if (btn != null) {
                btn.setText(mRetryText);
                btn.setTextColor(mButtonTextColor);
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mButtonTextSize);
                btn.setBackground(mButtonBackground);
                btn.setOnClickListener(mRetryButtonClickListener);
                if (activityBase != null) {
                    activityBase.dynamicAddView(btn, AttrFactory.textColor, R.color.loading_layout_btn_color);
                    activityBase.dynamicAddView(btn, AttrFactory.background, buttonBackgroundId);
                }
            }
            if (mOnErrorInflateListener != null) {
                mOnErrorInflateListener.onInflate(layout);
            }
        } else if (layoutId == mLoadingResId) {
            img = layout.findViewById(R.id.iv_image_loading);
            svgaLoading = layout.findViewById(R.id.svga_loading);
//            ImageView imageView = layout.findViewById(R.id.iv_bg_view);
//            if (isCommLoading) {
//                imageView.setVisibility(GONE);
////                if (skeletonScreen != null) {
////                    skeletonScreen.hide();
////                }
//                img.setVisibility(VISIBLE);
//            } else {
//                img.setVisibility(VISIBLE);
//                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_main_home_list);
//                int measuredHeight = 0;
//                try {
//                    measuredHeight = ((MySmartRefreshLayout) ((MainHomeFragment) ((MainActivity)
//                            getContext()).mDataList.get(0)).getRefreshLayout()).getMeasuredHeight();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Bitmap bitmap = ImageUtil.scaleBitmap(getContext(), mBitmap, measuredHeight);
//                imageView.setImageBitmap(bitmap);
//                imageView.setVisibility(VISIBLE);
//                skeletonScreen = Skeleton.bind(imageView)
//                        .load(R.layout.item_comm_skeleton)//骨架屏UI
//                        .duration(1234)//动画时间，以毫秒为单位
//                        .shimmer(true)//是否开启动画
//                        .color(R.color.shimmer_color)//shimmer的颜色
//                        .angle(45)//shimmer的倾斜角度
//                        .show();
//                MyHandler myHandler = new MyHandler(this);
//                myHandler.sendEmptyMessageDelayed(1, 10000);
//                //关闭骨架屏，显示正常UI
//                skeletonScreen.hide()

//            }
//            img.start();
//            GlideUtil.loadImage(getContext(),
//                    R.drawable.default_list_loading, img, R.drawable.toumingtu_head);
            UIUtils.setMargins2(img, 0, loadingMarginTop, 0, 0);
        }
        return layout;
    }

    public LoadingLayout setLoadingViewMarginTop(int loadingMarginTop) {
        this.loadingMarginTop = loadingMarginTop;
        return this;
    }

    private void text(int layoutId, int ctrlId, CharSequence value) {
        if (mLayouts.containsKey(layoutId)) {
            TextView view = mLayouts.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setText(value);
            }
        }
    }

    private void image(int layoutId, int ctrlId, int resId) {
        if (mLayouts.containsKey(layoutId)) {
            ImageView view = (mLayouts.get(layoutId).findViewById(ctrlId));
            if (view != null) {
                view.setImageDrawable(SkinResourcesUtils.getDrawable(resId));
            }
        }
    }

    private void ViewGroupbackgroud(int layoutId, int ctrlId, int resId) {
        if (mLayouts.containsKey(layoutId)) {
            ViewGroup view = (mLayouts.get(layoutId).findViewById(ctrlId));
            if (view != null) {
                view.setBackground(SkinResourcesUtils.getDrawable(resId));
            }
        }
    }

    /**
     * 设置空太页和错误页嵌套滑动
     *
     * @param isScrollingEnabled
     */
    public void setNestedScrollAll(boolean isScrollingEnabled) {
        setNestedScrollEmpty(isScrollingEnabled);
        setNestedScrollError(isScrollingEnabled);
    }

    /**
     * 设置错误页嵌套滑动
     *
     * @param isScrollingEnabled
     */
    public void setNestedScrollEmpty(boolean isScrollingEnabled) {
        if (nestedScrollEmpty != null) {
            nestedScrollEmpty.setNestedScrollingEnabled(isScrollingEnabled);
        }
    }

    /**
     * 设置错误页嵌套滑动
     *
     * @param isScrollingEnabled
     */
    public void setNestedScrollError(boolean isScrollingEnabled) {
        if (nestedScrollError != null) {
            nestedScrollError.setNestedScrollingEnabled(isScrollingEnabled);
        }
    }

    public int getStatus() {
        return status;
    }

    /**
     * 内容或是空太页
     *
     * @return
     */
    public boolean notLoading() {
        return status == TYPE_CONTENT || status == TYPE_EMPTY;
    }


    public boolean isContent() {
        return status == TYPE_CONTENT;
    }

    public boolean isEmpty() {
        return status == TYPE_EMPTY;
    }

    public boolean isLoading() {
        return status == TYPE_LOADING;
    }

    public boolean isInit() {
        return status == TYPE_INIT;
    }

    public boolean isHint() {
        return status == TYPE_HINT;
    }


    /**
     * 错误页
     *
     * @return
     */
    public boolean isError() {
        return status == 3;
    }

    /**
     * 需要登录页面,登录已经初始后再退出登录,设置关闭刷新
     */
    private void closeRefresh(RefreshLayout refreshLayout) {
        if (refreshLayout != null && isError()) {
            refreshLayout.setEnablePureScrollMode(true);//是否启用纯滚动模式
        }
    }
}
