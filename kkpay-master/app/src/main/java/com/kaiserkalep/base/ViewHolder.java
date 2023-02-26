package com.kaiserkalep.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

/**
 * 通用holder
 *
 * @auther: Administrator
 * @date: 2019/3/29 0029 17:54
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;
    public Context mContext;

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param convertView
     * @param parent
     * @param layoutId
     * @return
     */
    public static ViewHolder get(View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            ViewHolder holder = createViewHolder(parent, layoutId);
            holder.getConvertView().setTag(holder);
            return holder;
        }
        return (ViewHolder) convertView.getTag();
    }

    public static ViewHolder createViewHolder(Context context, View itemView) {
        return new ViewHolder(context, itemView);
    }

    public static ViewHolder createViewHolder(ViewGroup parent, View itemView) {
        return new ViewHolder(parent.getContext(), itemView);
    }

    public static ViewHolder createViewHolder(ViewGroup parent, int layoutId) {
        return createViewHolder(parent.getContext(), parent, layoutId);
    }

    public static ViewHolder createViewHolder(Context context,
                                              ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
//        AutoUtils.auto(itemView);
        return new ViewHolder(context, itemView);
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    /****以下为辅助方法*****/

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(!TextUtils.isEmpty(text) ? text : "");
        }
        return this;
    }

    /**
     * 设置TextView的大小
     *
     * @param viewId
     * @param size
     * @return
     */
    public ViewHolder setTextSize(int viewId, float size) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setTextSize(size);
        }
        return this;
    }

    /**
     * 设置TextView的图片
     *
     * @param viewId
     * @return
     */
    public ViewHolder setCompoundDrawablesWithIntrinsicBounds(int viewId, @Nullable Drawable left,
                                                              @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }
        return this;
    }

    /**
     * 设置TextView的图片
     *
     * @param viewId
     * @return
     */
    public ViewHolder setCompoundDrawablesWithIntrinsicBounds(int viewId, int left, int top, int right, int bottom) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        if (view != null) {
            if (resId != 0) {
                view.setImageDrawable(SkinResourcesUtils.getDrawable(resId));
            } else {
                view.setImageDrawable(null);
            }
        }
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundColor(SkinResourcesUtils.getColor(color));
        }
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackground(SkinResourcesUtils.getDrawable(backgroundRes));
        }
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(textColor);
        }
        return this;
    }

    public ViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(SkinResourcesUtils.getColor(textColorRes));
        }
        return this;
    }

    @SuppressLint("NewApi")
    public ViewHolder setAlpha(int viewId, float value) {
        View view = getView(viewId);
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                view.setAlpha(value);
            } else {
                // Pre-honeycomb hack to set Alpha value
                AlphaAnimation alpha = new AlphaAnimation(value, value);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                view.startAnimation(alpha);
            }
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, int visibility) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
        return this;
    }

    public ViewHolder linkify(int viewId) {
        TextView view = getView(viewId);
        if (view != null) {
            Linkify.addLinks(view, Linkify.ALL);
        }
        return this;
    }

    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            if (view != null) {
                view.setTypeface(typeface);
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        }
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setProgress(progress);
        }
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setProgress(progress);
        }
        return this;
    }

    public ViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
        }
        return this;
    }

    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        if (view != null) {
            view.setRating(rating);
        }
        return this;
    }

    public ViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setRating(rating);
        }
        return this;
    }

    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        if (view != null) {
            view.setTag(tag);
        }
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        if (view != null) {
            view.setTag(key, tag);
        }
        return this;
    }

    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        if (view != null) {
            view.setChecked(checked);
        }
        return this;
    }


    /**
     * 多个id设置同一个事件
     */
    public ViewHolder setOnClickListeners(View.OnClickListener listener, int... viewIds) {
        if (viewIds != null && viewIds.length > 1) {
            for (int i : viewIds) {
                View view = getView(i);
                if (view != null) {
                    view.setOnClickListener(listener);
                }
            }
        }
        return this;
    }


    /**
     * 关于事件的
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 关于条目点击
     */
    public ViewHolder setRootOnClickListener(View.OnClickListener listener) {
        if (mConvertView != null) {
            mConvertView.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnTouchListener(int viewId,
                                         View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId,
                                             View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

    public ViewHolder setImageView(int viewId, String url) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadImage(CommonUtils.getImgURL(url), view);
        }
        return this;
    }

    public ViewHolder setRoundedImageView(int viewId, String url) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadRoundedImage(CommonUtils.getImgURL(url), view);
        }
        return this;
    }

    public ViewHolder setRoundedImageTransformation(int viewId, String url) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadImageTransformationRounded(CommonUtils.getImgURL(url), view);
        }
        return this;
    }

    public ViewHolder setRoundedImageView(int viewId, String url, int corners) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadRoundedImage(CommonUtils.getImgURL(url), view, corners);
        }
        return this;
    }

    public ViewHolder setCircleImageView(int viewId, String url) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadCircleImage(CommonUtils.getImgURL(url), view);
        }
        return this;
    }

    public ViewHolder setCircleImageView(int viewId, String url, int placeholder) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadCircleImage(CommonUtils.getImgURL(url), view, placeholder);
        }
        return this;
    }

    public ViewHolder setImageView(ImageView imageView, String url) {
        GlideUtil.loadImage(CommonUtils.getImgURL(url), imageView);
        return this;
    }

    public ViewHolder setImageView(int viewId, String url, int placeholder) {
        ImageView view = getView(viewId);
        if (view != null) {
            GlideUtil.loadImage(view.getContext(), CommonUtils.getImgURL(url), view, placeholder);
        }
        return this;
    }

    public ViewHolder setImageView(ImageView imageView, String url, int placeholder) {
        GlideUtil.loadImage(imageView.getContext(), CommonUtils.getImgURL(url), imageView, placeholder);
        return this;
    }

    public Lifecycle getLifecycle() {
        return ((AppCompatActivity) mContext).getLifecycle();
    }

    public ViewHolder setViewSize(int viewId, int width, int height) {
        View view = getView(viewId);
        if (view != null) {
            setViewSize(view, width, height);
        }
        return this;
    }

    public ViewHolder setViewSize(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            if (width >= 0) {
                layoutParams.width = width;
            }
            if (height >= 0) {
                layoutParams.height = height;
            }
            view.setLayoutParams(layoutParams);
        }
        return this;
    }


    /**
     * 设置选中
     *
     * @param viewId
     * @param selected
     * @return
     */
    public ViewHolder setSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        if (view != null) {
            view.setSelected(selected);
        }
        return this;
    }

}
