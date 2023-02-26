package com.kaiserkalep.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.PayTypeBean;
import com.kaiserkalep.interfaces.OnItemClickListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.bigimageViewpage.tool.utility.ui.PhoneUtil;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.ShadowLayout;

import java.util.List;

import static com.kaiserkalep.constants.Config.ZERO;


/**
 * 取款方式
 */
public class MySellPayTypeAdapter extends SimpleAdapter3<PayTypeBean> {

    public MySellPayTypeAdapter(Context context, List<PayTypeBean> mData, OnItemClickListener listener) {
        super(context, mData, R.layout.items_my_pay_type, listener);
        this.mData = mData;
    }

    public void changeDataUi(List<PayTypeBean> content) {
        this.mData = content;
        notifyDataSetChanged();
    }


    @Override
    public void convert(ViewHolder holder, PayTypeBean item, int position) {
        String icon = item.getChannelIcon();
        int widthHeight = PhoneUtil.dp2px(mContext, 5);
//        if (CommonUtils.StringNotNull(icon)) {
//            GlideUtil.loadImageDrawable(mContext, CommonUtils.getImgURL(icon), o -> {
//                o.setBounds(ZERO, ZERO, widthHeight, widthHeight);
//                holder.setCompoundDrawablesWithIntrinsicBounds(R.id.tv_withdrawal_type, o, null, null, null);
//            });
//        } else {
//            int localIcon = PayTypeBean.getLocalIcon(item.isIsbind(), item.getChannelType());
//            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = mContext.getResources().getDrawable(localIcon);
//            drawable.setBounds(ZERO, ZERO, widthHeight, widthHeight);
//            holder.setCompoundDrawablesWithIntrinsicBounds(R.id.tv_withdrawal_type, drawable, null, null, null);
//        }

        if (CommonUtils.StringNotNull(icon) && item.isIsbind()) {
            holder.setImageView(R.id.icon_IV, CommonUtils.getImgURL(icon), PayTypeBean.getLocalIcon(item.isIsbind(), item.getChannelType()));
        } else {
            int localIcon = PayTypeBean.getLocalIcon(item.isIsbind(), item.getChannelType());
            Drawable drawable = mContext.getResources().getDrawable(localIcon);
            holder.setImageDrawable(R.id.icon_IV, drawable);
        }
        holder.setText(R.id.tv_withdrawal_type, item.getChannelTypeName());
        boolean select = item.isSelect();
        ((ShadowLayout) holder.getView(R.id.sl_withdrawal_type)).setSelected(select);
        holder.setVisible(R.id.iv_select_check, select ? View.VISIBLE : View.GONE);

        holder.getConvertView().setOnClickListener(v -> {
            if (listener != null && item.isIsbind()) {
                listener.onItemClick(v, position);
            }
        });

    }
}
