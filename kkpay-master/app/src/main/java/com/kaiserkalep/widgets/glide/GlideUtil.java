package com.kaiserkalep.widgets.glide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.TransformationTarget;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;
import com.kaiserkalep.widgets.bigimageViewpage.glide.FileTarget;

import java.io.File;


public class GlideUtil {

    /**
     * 默认图
     */
    private static final int PLACEHOLDER_DEFAULT = R.drawable.shape_default_image;
    /**
     * 圆角默认图
     */
    private static final int PLACEHOLDER_DEFAULT_ROUNDED = R.drawable.shape_default_image_rounded;

    /**
     * 圆形默认图
     */
    public static final int PLACEHOLDER_DEFAULT_CIRCLE = R.drawable.shape_default_image_circle;


    public static void loadImage(final Object src, final ImageView view) {
        if (view != null && src != null) {
            loadImage(view.getContext(), src, view);
        }
    }

    public static void loadImage(final Object src, final ImageView view, int placeholder, final boolean asGif) {
        if (view != null && src != null) {
            loadImage(view.getContext(), src, view, placeholder, asGif);
        }
    }

    public static void loadImage(final Object src, final ImageView view, int width, int height) {
        if (view != null && src != null) {
            loadImage(view.getContext(), src, view, width, height);
        }
    }

    public static void loadImage(final Object src, final ImageView view, int placeholder) {
        if (view != null && src != null) {
            loadImage(view.getContext(), src, view, placeholder);
        }
    }

    public static void loadNoPlaceImage(final Object src, final ImageView view, int placeholder) {
        if (view != null && src != null) {
            loadNoPlaceholderImage(view.getContext(), src, view, placeholder);
        }
    }


    public static void loadCircleImage(final Object src, final ImageView view) {
        if (view != null && src != null) {
            loadCircleImage(view.getContext(), src, view, R.drawable.icon_default_photo);
        }
    }

    public static void loadCircleImage(final Object src, final ImageView view, int placeholder) {
        if (view != null && src != null) {
            loadCircleImage(view.getContext(), src, view, placeholder);
        }
    }

    public static void loadRoundedImage(final Object src, final ImageView view, int corners) {
        if (view != null && src != null) {
            loadRoundedImage(view.getContext(), src, view, corners, PLACEHOLDER_DEFAULT_ROUNDED);
        }
    }

    public static void loadRoundedImage(final Object src, final ImageView view) {
        if (view != null && src != null) {
            loadRoundedImage(view.getContext(), src, view, UIUtils.dip2px(view.getContext(), 5), PLACEHOLDER_DEFAULT_ROUNDED);
        }
    }


    public static void loadLocalImage(final Object src, final ImageView view) {
        if (view != null && src != null) {
            Context context = view.getContext();
            if (getContextNotNull(context)) {
                try {
                    Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                    GlideApp.with(context)
                            .load(src)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(drawable)
                            .error(drawable)
                            .into(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void loadLocalRoundedImage(final Object src, final ImageView view) {
        if (view != null && src != null) {
            Context context = view.getContext();
            if (getContextNotNull(context)) {
                try {
                    Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                    GlideApp.with(context)
                            .load(src)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8)))
                            .placeholder(drawable)
                            .error(drawable)
                            .into(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadRoundedImage(Context context, final Object src, final ImageView view) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void loadImage(Context context, final Object src, final ImageView view, int width, int height) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .override(width, height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CheckResult")
    public static void loadImage(Context context, final Object src, final ImageView view, final int placeholder, final boolean asGif) {
        if (getContextNotNull(context)) {
            try {
                GlideRequests with = GlideApp.with(context);
                if (asGif) {
                    with.asGif();
                }
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
                with.load(checkUrl(src))
                        .format(DecodeFormat.PREFER_ARGB_8888)//防止gif变绿
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImage(Context context, final Object src, final ImageView view) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadCircleImage(Context context, final Object src, final ImageView view, int placeholder) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadRoundedImage(Context context, final Object src, final ImageView view, int corners, int placeholder) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
//                Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT_ROUNDED);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(corners)))
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 联赛推荐 HorizontalAdapter 定制
     *
     * @param context
     * @param src
     * @param view
     * @param corners
     */
    public static void loadRoundedImage2(Context context, final Object src, final ImageView view, int corners) {
        if (getContextNotNull(context)) {
            try {

                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions().transform(new FitCenter(), new RoundedCorners(corners)))
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void loadRoundedImage(Context context, final Object src, final ImageView view,
                                        RoundCornersTransformation transformation, int placeholder) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions().transform(transformation))
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void loadImage(Context context, final Object src, final ImageView view, int placeholder) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadNoPlaceholderImage(Context context, final Object src, final ImageView view, int error) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(error);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImageDontAnimate(Context context, final Object src, final ImageView view, int placeholder) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(placeholder);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImageNoError(Context context, final Object src, final ImageView view) {
        if (getContextNotNull(context)) {
            try {
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void loadImage(Context context, final Object src, final ImageView view, RequestListener<Drawable> listener) {
        if (getContextNotNull(context)) {
            try {
                Drawable drawable = SkinResourcesUtils.getDrawable(PLACEHOLDER_DEFAULT);
                GlideApp.with(context)
                        .load(checkUrl(src))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(drawable)
                        .error(drawable)
                        .listener(listener)
                        .into(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置等比缩放
     *
     * @param src
     * @param view
     */
    public static void loadImageTransformationRounded(final Object src, final ImageView view) {
        Context context = view.getContext();
        if (getContextNotNull(context)) {
            try {
                GlideApp.with(context)
                        .load(checkUrl(src))
//                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply(new RequestOptions().transform(new FitCenter(), new RoundedCorners(5)))
                        .placeholder(R.drawable.toumingtu_point)
                        .error(R.drawable.toumingtu_point)
                        .into(new TransformationTarget(view));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置等比缩放
     *
     * @param src
     * @param view
     */
    public static void loadImageTransformation(final Object src, final ImageView view) {
        Context context = view.getContext();
        if (getContextNotNull(context)) {
            try {
                GlideApp.with(context)
                        .load(checkUrl(src))
//                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.toumingtu_point)
                        .error(R.drawable.toumingtu_point)
                        .into(new TransformationTarget(view));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 下载网络图片带回调
     *
     * @param context
     * @param url
     */
    public static void loadImage(Context context, String url, SucceedCallBackListener<Drawable> listener) {
        try {
            GlideApp.with(context)
                    .asDrawable()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(url)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (listener != null && resource != null) {
                                listener.succeedCallBack(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (listener != null) {
                                listener.succeedCallBack(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载网络连接判断地址
     *
     * @param src
     * @return
     */
    private static Object checkUrl(Object src) {
        if (src != null && src instanceof String) {
            String url = (String) src;
            if (CommonUtils.StringNotNull(url) && CommonUtils.isContainsHttp(url)) {
                return url;
            } else {
                return "";
            }
        }
        return src;
    }


    private static boolean getContextNotNull(Context context) {
        if (context != null) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
                    return true;
                }
            } else {
                return context != null;
            }
        }
        return false;
    }


    /**
     * 需要在子线程执行
     *
     * @param context
     * @param url
     * @return
     */
    public static Bitmap load(Context context, String url) {
        try {
            return GlideApp.with(context)
                    .asBitmap()
                    .load(checkUrl(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 需要在子线程执行
     *
     * @param context
     * @param url
     * @return
     */
    public static File loadFile(Context context, String url) {
        try {
            return GlideApp.with(context)
                    .asFile()
                    .load(checkUrl(url))
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 图片下载转成Bitmap
     */
    public static void loadImageBitmap(Context context, String url, SucceedCallBackListener<Bitmap> listener) {
        try {
            GlideApp.with(context)
                    .asBitmap()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (listener != null && resource != null) {
                                listener.succeedCallBack(resource);
                            } else {
                                listener.succeedCallBack(null);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (listener != null) {
                                listener.succeedCallBack(null);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            if (listener != null) {
                                listener.succeedCallBack(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImageDrawable(Context context, String url, SucceedCallBackListener<Drawable> listener) {
        try {
            GlideApp.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (listener != null && resource != null) {
                                listener.succeedCallBack(resource);
                            } else {
                                listener.succeedCallBack(null);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (listener != null) {
                                listener.succeedCallBack(null);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            if (listener != null) {
                                listener.succeedCallBack(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 仅下载file
     *
     * @param context
     * @param url
     */
    public static void loadFileImage(Context context, String url, SucceedCallBackListener<File> listener) {
        GlideApp.with(context).downloadOnly().load(url).addListener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<File> target, boolean isFirstResource) {
                if (listener != null) {
                    listener.succeedCallBack(null);
                }
                return true;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target,
                                           DataSource dataSource, boolean isFirstResource) {
                if (listener != null && resource != null) {
                    listener.succeedCallBack(resource);
                }
                return true;
            }
        }).into(new FileTarget() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }
        });
    }


    public static void clearMemory(Activity activity) {
        Glide.get(activity.getApplicationContext()).clearMemory();
    }

    public static void cleanDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        }).start();
    }


}
