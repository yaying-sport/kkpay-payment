package com.kaiserkalep.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * 设置图片等比缩放
 *
 * @Auther: Administrator
 * @Date: 2019/6/8 0008 17:21
 * @Description:
 */
public class TransformationTarget extends ImageViewTarget<Drawable> {

    private ImageView target;

    public TransformationTarget(ImageView target) {
        super(target);
        this.target = target;
    }

    @Override
    protected void setResource(Drawable resource) {
        try {
            if (resource != null && target != null) {
                //获取原图的宽高
                int width = resource.getIntrinsicWidth();
                int height = resource.getIntrinsicHeight();

                //获取imageView的宽
                int imageViewWidth = target.getMeasuredWidth();
                //获取屏幕宽

//                int imageViewWidth = UIUtils.getScreenWidth(target.getContext());

                //计算缩放比例
                float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

                //计算图片等比例放大后的高
                int imageViewHeight = (int) (height * sy);
                ViewGroup.LayoutParams params = target.getLayoutParams();
                if (params != null) {
                    view.setImageDrawable(resource);
                    int i = params.height;

//                    params.height = imageViewHeight;
//                    target.setLayoutParams(params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}

