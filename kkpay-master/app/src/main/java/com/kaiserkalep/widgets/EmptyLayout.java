package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.kaiserkalep.R;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 *
 */
public class EmptyLayout extends LinearLayout {

    private Context eContext;

    private ViewGroup mLoadingView;
    private ViewGroup mEmptyView;
    private ViewGroup mErrorView;

    private TextView mEmptyText;
    private ImageView mEmptyImage;
    private TextView mErrorText;
    private ImageView mErrorImage;
    private Button mErrorBtnText;

    private RelativeLayout mEmptyRelativeLayout;

    private LayoutInflater mInflater;
    private boolean mViewsAdded;

    /**
     * Normal state
     */
    public final static int TYPE_NORMAL = 0;

    /**
     * empty state
     */
    public final static int TYPE_EMPTY = 1;
    /**
     * loading state
     */
    public final static int TYPE_LOADING = 2;
    /**
     * error state
     */
    public final static int TYPE_ERROR = 3;

    private int mEmptyType = TYPE_NORMAL;

    private final String SVGNAME = "s/loading.svga";

    private List<View> childViews;
    private ShadowLayout ivImageLoading;

    public EmptyLayout(Context context) {
        this(context, null);
        init();
    }


    public EmptyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.eContext = context;
        init();
    }

    private void init() {
        childViews = new ArrayList<>();
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 设置空页面文本
     */
    public void setEmptyText(String text) {
        if (null != mEmptyText)
            mEmptyText.setText(text);
    }

    /**
     * 设置空页面图片
     */
    public void setEmptyImage(@DrawableRes int resId) {
        if (null != mEmptyImage)
            mEmptyImage.setBackgroundResource(resId);
    }

    /**
     * 设置异常页面文本
     */
    public void setErrorText(String text) {
        if (null != mErrorText)
            mErrorText.setText(text);
    }

    /**
     * 设置异常页面button文本
     */
    public void setErrorBtnText(String text) {
        if (null != mErrorBtnText)
            mErrorBtnText.setText(text);
    }

    /**
     * 设置异常页面图片
     */
    public void setErrorImage(@DrawableRes int resId) {
        if (null != mErrorImage)
            mErrorImage.setBackgroundResource(resId);
    }

    public interface onErrorButtonClickListener {
        void errorOnClick(View view);
    }

    private onErrorButtonClickListener onerrorbuttonclicklistener;

    /**
     * 设置异常button点击监听
     */
    public void setErrorButtonClickListener(onErrorButtonClickListener onerrorbuttonclicklistener) {
        this.onerrorbuttonclicklistener = onerrorbuttonclicklistener;
    }


    public interface OnViewShowHideListener {
        /**
         * @TYPE_NORMAL ：全部隐藏
         * @TYPE_EMPTY :空数据
         * @TYPE_LOADING ：加载中
         * @TYPE_ERROR ：异常
         */
        void onShowType(int type);
    }

    private OnViewShowHideListener viewshowhidelistener;

    public void addViewShowHideListener(OnViewShowHideListener viewshowhidelistener) {
        this.viewshowhidelistener = viewshowhidelistener;
    }

    /**
     * 单独适配不显示状态下初始化View
     * （正常View初始化必须先show后再View文本图片）
     */
    public void initView() {
        mEmptyType = TYPE_NORMAL;
        changeEmptyType();
    }

    private void getChildViews() {
        int childCount = getChildCount();
        View view;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            if (isEmptyView(view)) {
                continue;
            }
            childViews.add(view);
        }
    }

    private void hideChildView() {
        for (View view : childViews) {
            if (isEmptyView(view)) {
                continue;
            }
            view.setVisibility(GONE);
        }
    }

    /**
     * 判断view 对象是否是EmptyView
     */
    private boolean isEmptyView(View view) {
        if ((view == null ||
                mEmptyRelativeLayout == view ||
                view == mLoadingView ||
                view == mEmptyView ||
                view == mErrorView)) {
            return true;
        }
        return false;
    }

    private void showChildView() {
        for (View view : childViews) {
            if (isEmptyView(view))
                continue;
            view.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏EmptyView
     */
    private void hideEmptyView() {
        if (mLoadingView != null)
            mLoadingView.setVisibility(GONE);
        if (mEmptyView != null)
            mEmptyView.setVisibility(GONE);
        if (mErrorView != null)
            mErrorView.setVisibility(GONE);
    }

    /**
     * 显示showError
     */
    public void showError() {
        if (null != viewshowhidelistener)
            viewshowhidelistener.onShowType(TYPE_ERROR);
        getChildViews();
        hideChildView();
        this.mEmptyType = TYPE_ERROR;
        changeEmptyType();
    }

    /**
     * 显示showEmpty
     */
    public void showEmpty() {
        if (null != viewshowhidelistener)
            viewshowhidelistener.onShowType(TYPE_EMPTY);
        getChildViews();
        hideChildView();
        this.mEmptyType = TYPE_EMPTY;
        changeEmptyType();
    }

    /**
     * 显示showEmpty
     */
    public void showEmpty(String text) {
        if (null != viewshowhidelistener)
            viewshowhidelistener.onShowType(TYPE_EMPTY);
        getChildViews();
        hideChildView();
        this.mEmptyType = TYPE_EMPTY;
        changeEmptyType();
        if (null != mEmptyText) {
            mEmptyText.setText(text);
        }
        SVGAParser.Companion.shareParser();
    }

    /**
     * 显示showLoading
     */
    public void showLoading() {
        if (null != viewshowhidelistener)
            viewshowhidelistener.onShowType(TYPE_LOADING);
        getChildViews();
        hideChildView();
        this.mEmptyType = TYPE_LOADING;
        changeEmptyType();
    }

    /**
     * 隐藏EmptyLayout
     */
    public void hide() {
        if (null != viewshowhidelistener)
            viewshowhidelistener.onShowType(TYPE_NORMAL);
        showChildView();
        hideEmptyView();
    }

    private void changeEmptyType() {
        setDefaultValues();
        if (!mViewsAdded) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            mEmptyRelativeLayout = new RelativeLayout(getContext());
            mEmptyRelativeLayout.setGravity(Gravity.CENTER);
            mEmptyRelativeLayout.setLayoutParams(lp);
            if (mEmptyView != null)
                mEmptyRelativeLayout.addView(mEmptyView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if (mLoadingView != null)
                mEmptyRelativeLayout.addView(mLoadingView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if (mErrorView != null)
                mEmptyRelativeLayout.addView(mErrorView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mViewsAdded = true;
            mEmptyRelativeLayout.setVisibility(VISIBLE);
            addView(mEmptyRelativeLayout);
        }

        switch (mEmptyType) {
            case TYPE_EMPTY:
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.VISIBLE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                break;
            case TYPE_ERROR:
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.VISIBLE);
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.GONE);
                break;
            case TYPE_LOADING:
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                if (mErrorView != null)
                    mErrorView.setVisibility(View.GONE);
                if (mLoadingView != null)
                    mLoadingView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 重设加载动画效果
     * （popupwindows调用dismiss()后，会走onDetachedFromWindow方法清除动画）
     */
    private void loadAnimation() {
        ivImageLoading.removeAllViews();
        SVGAImageView svgaImageView = new SVGAImageView(eContext);
        FrameLayout.LayoutParams flfp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        flfp.gravity = Gravity.CENTER;
        ivImageLoading.addView(svgaImageView, flfp);

        SVGAParser svgaParser = SVGAParser.Companion.shareParser();
        svgaParser.decodeFromAssets(SVGNAME, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                svgaImageView.setVideoItem(videoItem);
                svgaImageView.stepToFrame(ZERO, true);
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 停止加载动画效果
     */
    private void stopAnimation() {
        if (null != ivImageLoading && ivImageLoading.getChildCount() > ZERO) {
            View childAt = ivImageLoading.getChildAt(ZERO);
            if (childAt instanceof SVGAImageView) {
                ((SVGAImageView) childAt).stopAnimation();
            }
        }
    }

    private void setDefaultValues() {
        if (mEmptyView == null) {
            mEmptyView = (ViewGroup) mInflater.inflate(R.layout._loading_layout_empty, null);
            mEmptyText = mEmptyView.findViewById(R.id.empty_text);
            mEmptyImage = mEmptyView.findViewById(R.id.empty_image);
        }

        if (mLoadingView == null) {
            mLoadingView = (ViewGroup) mInflater.inflate(R.layout._loading_layout_loading, null);
            ivImageLoading = mLoadingView.findViewById(R.id.iv_image_loading);
        } else {
            if (mEmptyType == TYPE_LOADING) {
                loadAnimation();
            } else {
                stopAnimation();
            }
        }

        if (mErrorView == null) {
            mErrorView = (ViewGroup) mInflater.inflate(R.layout._loading_layout_error, null);
            mErrorText = mErrorView.findViewById(R.id.error_text);
            mErrorImage = mErrorView.findViewById(R.id.error_image);
            mErrorBtnText = mErrorView.findViewById(R.id.retry_button);
            if (mErrorBtnText != null) {
                mErrorBtnText.setOnClickListener(view -> {
                    if (null != onerrorbuttonclicklistener)
                        onerrorbuttonclicklistener.errorOnClick(view);
                });
            }
        }
    }
}