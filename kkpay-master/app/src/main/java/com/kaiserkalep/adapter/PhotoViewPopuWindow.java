package com.kaiserkalep.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.LoadingLayout;
import com.kaiserkalep.widgets.PhotoView.PhotoView;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.king.zxing.util.CodeUtils;

import static com.zhongjh.common.utils.ThreadUtils.runOnUiThread;

/**
 *
 */
public class PhotoViewPopuWindow extends PopupWindow {

    private Context context;
    private PhotoView ivPhoto;
    private ImageView ivError;
    private LoadingLayout photoLoading;
    private FrameLayout flParentView;
    private Button btnAdd;
    private boolean isFail;
    private boolean isBitmap = false;//true:bitmap ; false:url
    private String imgUrl;
    private Bitmap bitmap;

    public PhotoViewPopuWindow(Context context) {
        super(context);
        try {
            if (null == context) {
                return;
            }
            this.context = context;
            this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            this.setFocusable(true);
            this.setAnimationStyle(R.style.popupAnimation);
            this.setOutsideTouchable(true);
            this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.photoview_backgroud)));
            View pPopView = View.inflate(context, R.layout.image_photoview, null);
            this.setContentView(pPopView);
            this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            ivPhoto = pPopView.findViewById(R.id.iv_photo);
            ivError = pPopView.findViewById(R.id.iv_error);
            photoLoading = pPopView.findViewById(R.id.photo_loading);
            photoLoading.setLoadBackgroud(R.color.photoview_backgroud);
            flParentView = pPopView.findViewById(R.id.fl_parent_view);
            btnAdd = pPopView.findViewById(R.id.btn_add);
            initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听事件
     */
    private void initListener() {
        flParentView.setOnClickListener(v -> {
            if (isFail) {
                dismiss();
            }
        });

        ivError.setOnClickListener(v -> {
            if (isFail) {
                isFail = false;
                showImage(isBitmap, imgUrl);
            }
        });

        btnAdd.setOnClickListener(v -> {
            if (null != context) {
                if (null != bitmap) {
                    Config.saveImageToGallery(context, bitmap);
                } else {
                    Toast.makeText(context, MyApp.getLanguageString(context, R.string.Share_Save_Error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 加载图片
     *
     * @param Url
     */
    public void showImage(boolean isBitmap, String Url) {
        this.isBitmap = isBitmap;
        this.imgUrl = Url;
        if (null != photoLoading) {
            photoLoading.showLoading();
            setVisibility(ivPhoto, View.VISIBLE);
            setVisibility(ivError, View.GONE);
        }

        if (isBitmap) {
            createQRCode(context, Url, bit -> {
                runOnUiThread(() -> {
                    if (null != photoLoading) {
                        photoLoading.showContent();
                    }
                    bitmap = bit;
                    isFail = null == bit;
                    if (isFail) {
                        setVisibility(ivPhoto, View.GONE);
                        setVisibility(btnAdd, View.GONE);
                        setVisibility(ivError, View.VISIBLE);
                        if (null != ivPhoto) {
                            ivPhoto.setOnOutsidePhotoTapListener(null);
                        }
                    } else {
                        if (null != ivPhoto) {
                            ivPhoto.setImageBitmap(bitmap);
                            ivPhoto.setOnOutsidePhotoTapListener(imageView -> {
                                dismiss();
                            });
                        }
                        setVisibility(ivPhoto, View.VISIBLE);
                        setVisibility(btnAdd, View.VISIBLE);
                        setVisibility(ivError, View.GONE);
                    }
                });
            });
        } else {
            GlideUtil.loadImageBitmap(context, imgUrl, bit -> {
                if (null != photoLoading) {
                    photoLoading.showContent();
                }
                bitmap = bit;
                isFail = null == bitmap;
                if (isFail) {
                    setVisibility(ivPhoto, View.GONE);
                    setVisibility(btnAdd, View.GONE);
                    setVisibility(ivError, View.VISIBLE);
                    if (null != ivPhoto) {
                        ivPhoto.setOnOutsidePhotoTapListener(null);
                    }
                } else {
                    if (null != ivPhoto) {
                        ivPhoto.setImageBitmap(bitmap);
                        ivPhoto.setOnOutsidePhotoTapListener(imageView -> {
                            dismiss();
                        });
                    }
                    setVisibility(ivPhoto, View.VISIBLE);
                    setVisibility(btnAdd, View.VISIBLE);
                    setVisibility(ivError, View.GONE);
                }
            });
        }
    }

    /**
     * 生成二维码
     *
     * @param str
     */
    public static void createQRCode(Context context, String str, SucceedCallBackListener<Bitmap> listener) {
        if (null != context && CommonUtils.StringNotNull(str) && null != listener) {
            new Thread(() -> {
                //生成二维码相关放在子线程里面
//                Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                Bitmap bitmap = CodeUtils.createQRCode(str, 600, null);
                listener.succeedCallBack(bitmap);
            }).start();
        }
    }

    @Override
    public void dismiss() {
        this.isFail = false;
        this.imgUrl = "";
        if (null != bitmap) {
            bitmap = null;
        }
        if (null != ivPhoto) {
            ivPhoto.setImageBitmap(null);
        }
        setVisibility(ivPhoto, View.VISIBLE);
        setVisibility(ivError, View.GONE);
        super.dismiss();
    }

    /**
     * @param view
     * @param visibility
     */
    private void setVisibility(View view, int visibility) {
        if (null != view) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 关闭页面时，销毁所有数据
     */
    public void release() {
        try {
            context = null;
            isFail = false;
            imgUrl = "";
            if (null != bitmap) {
                bitmap = null;
            }
            if (null != ivPhoto) {
                ivPhoto.setImageBitmap(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
