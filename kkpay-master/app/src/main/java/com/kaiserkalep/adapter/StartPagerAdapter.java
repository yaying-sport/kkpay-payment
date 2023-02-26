package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kaiserkalep.R;
import com.kaiserkalep.bean.AdGuideData;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 启动页广告
 *
 * @Auther: Jack
 * @Date: 2019/10/21 11:02
 * @Description:
 */
public class StartPagerAdapter extends PagerAdapter {

    private Context context;
    private List<AdGuideData.ListBean> data;
    private SucceedCallBackListener<AdGuideData.ListBean> listener;

    public StartPagerAdapter(Context context, List<AdGuideData.ListBean> data, SucceedCallBackListener<AdGuideData.ListBean> listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View inflate = View.inflate(context, R.layout.view_ad_guide_image_view, null);
        if (CommonUtils.ListNotNull(data)) {
            AdGuideData.ListBean imageData = data.get(position);
            if (imageData != null) {
                ImageView imageView = inflate.findViewById(R.id.iv_ad_guide);
                File file = imageData.getFile();
////                //文件在本地的路径
//                String filePath = file.getPath();
//                BitmapFactory.Options options = new BitmapFactory.Options();
////                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(filePath, options);
//
//                String mimeType = options.outMimeType;
//                LogUtils.d("图片类型1：" + mimeType);
                GlideUtil.loadImage(file, imageView, R.drawable.toumingtu_head/*,mimeType.contains("gif")*/);
                inflate.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.succeedCallBack(imageData);
                    }
                });
            }
        }
        container.addView(inflate);
        return inflate;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
