package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.ChannelDetailsBean;
import com.kaiserkalep.bean.FeedbackTitleBean;
import com.kaiserkalep.interfaces.OnItemClickListener;

import java.util.List;

/**
 * 银行卡选择
 *
 * @Auther: Jack
 * @Date: 2020/12/29 14:08
 * @Description:
 */
public class SelectBankAdapter extends SimpleAdapter3<Object> {

    private int logoPlaceholder = R.drawable.icon_bank_logo;

    public void setLogoPlaceholder(int logoPlaceholder) {
        this.logoPlaceholder = logoPlaceholder;
    }

    public SelectBankAdapter(Context context, List<Object> mData, OnItemClickListener listener) {
        super(context, mData, R.layout.items_select_bank, listener);
    }

    @Override
    public void convert(ViewHolder holder, Object item, int position) {
        if (item instanceof ChannelDetailsBean) {
            holder.setVisible(R.id.iv_bank_logo, View.VISIBLE);
            holder.setImageView(R.id.iv_bank_logo, BaseNetUtil.jointUrl(((ChannelDetailsBean) item).getIcon()), logoPlaceholder);
            holder.setText(R.id.tv_name, ((ChannelDetailsBean) item).getName());
            holder.setImageResource(R.id.iv_select, ((ChannelDetailsBean) item).isSelect() ? R.drawable.icon_select_check : 0);
        } else if (item instanceof FeedbackTitleBean) {
            holder.setVisible(R.id.iv_bank_logo, View.GONE);
            holder.setImageView(R.id.iv_bank_logo, "", logoPlaceholder);
            holder.setText(R.id.tv_name, ((FeedbackTitleBean) item).getDictLabel());
            holder.setImageResource(R.id.iv_select, ((FeedbackTitleBean) item).isSelect() ? R.drawable.icon_select_check : 0);
        }

        holder.setVisible(R.id.view_line, position != mData.size() - 1);
        holder.getConvertView().setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, position);
            }
        });
    }
}
