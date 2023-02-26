package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.MerchantBean;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.ZERO;

public class MerchantAdapter extends SimpleAdapter3<MerchantBean.RowsDTO> {

    private SucceedCallBackListener<MerchantBean.RowsDTO> listener;
    private int type;//0 最近 ； 1 全部

    public MerchantAdapter(Context context, int type, List<MerchantBean.RowsDTO> mData, SucceedCallBackListener<MerchantBean.RowsDTO> listener) {
        super(context, mData, R.layout.item_merchant);
        this.type = type;
        this.listener = listener;
    }


    public void changeDataUi(int type, List<MerchantBean.RowsDTO> content) {
        this.type = type;
        super.changeDataUi(content);
    }

    @Override
    public void convert(ViewHolder holder, MerchantBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.ll_content_bg, View.VISIBLE);
            holder.setVisible(R.id.v_line, position == mData.size() - ONE ? View.GONE : View.VISIBLE);
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(item.getLogo()), holder.getView(R.id.iv_logo));
            holder.setVisible(R.id.ll_account_parent_view, ZERO == type ? View.VISIBLE : View.GONE);
            holder.setText(R.id.tv_name, item.getAgentName());
            holder.setText(R.id.tv_address, item.getWalletAddress());
            holder.setText(R.id.tv_account, item.getAgentUsername());

            holder.setOnClickListener(R.id.ll_content_bg, v -> {
                if (null != listener) {
                    item.setType(type);
                    listener.succeedCallBack(item);
                }
            });

        } else {

            holder.setVisible(R.id.ll_content_bg, View.GONE);
            holder.setOnClickListener(R.id.ll_content_bg, null);
        }
    }
}
