package com.kaiserkalep.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.fragment.app.FragmentActivity;

import com.kaiserkalep.R;
import com.kaiserkalep.base.ViewBase;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.attr.base.AttrFactory;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;

import q.rorbin.badgeview.QBadgeView;

/**
 * 通用标题
 *
 * @Auther: Administrator
 * @Date: 2019/4/17 0017 13:46
 * @Description:
 */
public class CommTitle extends ViewBase {

    private TextView tvBack;
    private TextView tvTitle;
    private TextView tvFunction;
    private View view;

    protected OnBackListener onBackListener;
    private View viewLine;
    private QBadgeView newMessageRight;

    public interface OnBackListener {
        void back();
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public CommTitle(Context context) {
        super(context);
    }

    public CommTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getViewId() {
        return R.layout.view_title;
    }

    @Override
    public void afterViews(View view) {
        this.view = view;
        tvBack = view.findViewById(R.id.tv_back);
        tvTitle = view.findViewById(R.id.tv_title);
        tvFunction = view.findViewById(R.id.tv_function);
        viewLine = view.findViewById(R.id.view_line_title);

        //返回键的监听
        tvBack.setOnClickListener(view1 -> {
            if (onBackListener != null) {
                onBackListener.back();
            } else {
                Context context = getContext();
                if (context != null && context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });
    }

    /**
     * 通用标题
     *
     * @param title 标题文字
     */
    public void init(String title) {
        init(title, true);
    }

    /**
     * 通用标题
     *
     * @param title 标题文字
     * @param back  返回文字
     */
    public void init(String title, String back) {
        init(title, true, back, false, "", 0, null);
    }

    /**
     * 通用标题
     *
     * @param title  标题文字
     * @param isBack 是否显示返回
     */
    public void init(String title, boolean isBack) {
        init(title, isBack, "", false, "", 0, null);
    }

    /**
     * 功能描述: 通用标题
     *
     * @auther: Administrator
     * @date: 2019/4/17 0017 14:28
     */
    public void init(String title, String back, String function, int selectResId, View.OnClickListener functionListener) {
        init(title, true, back, false, function, selectResId, functionListener);
    }


    /**
     * 功能描述: 通用标题
     *
     * @auther: Administrator
     * @date: 2019/4/17 0017 14:28
     */
    public void init(String title, String back, boolean backColor, String function, int selectResId, View.OnClickListener functionListener) {
        init(title, true, back, backColor, function, selectResId, functionListener);
    }


    /**
     * 功能描述: 设置标题文字颜色
     *
     * @param color
     */
    public void setTvTitleColor(@ColorInt int color) {
        if (null != tvTitle) {
            tvTitle.setTextColor(color);
        }
    }

    /**
     * 功能描述: 设置右侧副标题文本颜色
     *
     * @param color
     */
    public void setTvFunctionColor(@ColorInt int color) {
        if (null != tvFunction) {
            tvFunction.setTextColor(color);
        }
    }

    /**
     * 功能描述: 通用标题
     *
     * @auther: Administrator
     * @date: 2019/4/17 0017 14:28
     */
    private void init(String title, boolean isBack, String back, boolean backColor,
                     String function, int selectResId, View.OnClickListener functionListener) {
        tvTitle.setText(CommonUtils.StringNotNull(title) ? title : "");
        tvBack.setText(CommonUtils.StringNotNull(back) ? back : "");
        tvBack.setVisibility(isBack ? VISIBLE : GONE);
        tvFunction.setVisibility(functionListener != null && (CommonUtils.StringNotNull(function) || selectResId > 0) ? VISIBLE : GONE);
        tvFunction.setText(CommonUtils.StringNotNull(function) ? function : "");
        if (selectResId > 0) {
            UIUtils.setRightDrawable(getContext(), tvFunction, selectResId);
        }
        int leftRes;
//        int color = SkinResourcesUtils.getColor(R.color.status_bar_color);
        if (backColor) {//状态栏文字颜色,只有黑与白
            leftRes = R.drawable.icon_top_back_white;
        } else {
            leftRes = R.drawable.left_black;
        }
        UIUtils.setLeftDrawable(getContext(), tvBack, leftRes);
        tvBack.setCompoundDrawablePadding(UIUtils.dip2px(10));
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof ZZActivity) {
            if (selectResId != 0) {
                ((ZZActivity) activity).dynamicAddView(tvFunction, AttrFactory.drawableRight, selectResId);
            }
            ((ZZActivity) activity).dynamicAddView(tvBack, AttrFactory.drawableLeft, leftRes);
        }
        if (functionListener != null && tvFunction.getVisibility() == VISIBLE) {
            tvFunction.setOnClickListener(functionListener);
        }
    }

    /**
     * 设置意见反馈未读红点提醒
     *
     * @param unread
     */
    public void setUnReadMsgConunt(int unread) {
        if (null != tvFunction) {
            if (newMessageRight == null) {
                newMessageRight = new QBadgeView(getContext());
                newMessageRight.setBadgeBackgroundColor(SkinResourcesUtils.getColor(R.color.unread_message_dot));
                newMessageRight.setBadgePadding(3, true);
                newMessageRight.setBadgeTextSize(4, true);
                newMessageRight.setBadgeGravity(Gravity.END | Gravity.TOP);
                newMessageRight.setGravityOffset(8, 8, true);

                newMessageRight.bindTarget(tvFunction);

            }
            if (unread > 0) {
                newMessageRight.setBadgeNumber(-1);
            } else {
                newMessageRight.hide(true);
            }
        }
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvFunction() {
        return tvFunction;
    }

    public void setTvFunction(String function, int selectResId, View.OnClickListener functionListener) {
        tvFunction.setVisibility(functionListener != null && (CommonUtils.StringNotNull(function) || selectResId > 0) ? VISIBLE : GONE);
        tvFunction.setText(CommonUtils.StringNotNull(function) ? function : "");
        if (selectResId > 0) {
            UIUtils.setRightDrawable(getContext(), tvFunction, selectResId);
        }
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof ZZActivity) {
            if (selectResId != 0) {
                ((ZZActivity) activity).dynamicAddView(tvFunction, AttrFactory.drawableRight, selectResId);
            }
        }
        if (functionListener != null && tvFunction.getVisibility() == VISIBLE) {
            tvFunction.setOnClickListener(functionListener);
        }
    }

    public TextView getTvBack() {
        return tvBack;
    }

    public void setViewLine(boolean isVisibility) {
        if (viewLine != null) {
            viewLine.setVisibility(isVisibility ? VISIBLE : GONE);
        }
    }

    public void setMyBackground(Drawable drawable) {
        if (view != null && drawable != null) {
            view.setBackground(drawable);
        }
    }

    public void setMyBackgroundColor(@ColorInt int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }
}
