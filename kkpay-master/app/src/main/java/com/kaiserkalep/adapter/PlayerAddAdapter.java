package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

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

public class PlayerAddAdapter extends SimpleAdapter3<String> {

    private SucceedCallBackListener<String> listener;
    private String logo;

    public PlayerAddAdapter(Context context, String logo, List<String> mData, SucceedCallBackListener<String> listener) {
        super(context, mData, R.layout.item_playeradd);
        this.logo = logo;
        this.listener = listener;
    }

    public void changeDataUi(String logo, List<String> content) {
        this.logo = logo;
        super.changeDataUi(content);
    }

    @Override
    public void convert(ViewHolder holder, String item, int position) {
        if (null != item) {
            holder.setVisible(R.id.ll_content_bg, View.VISIBLE);
            holder.setVisible(R.id.v_line, position == mData.size() - ONE ? View.GONE : View.VISIBLE);
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(logo), holder.getView(R.id.iv_logo));
            holder.setText(R.id.tv_name, item);
            holder.setOnClickListener(R.id.ll_content_bg, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item);
                }
            });

        } else {
            holder.setVisible(R.id.ll_content_bg, View.GONE);
            holder.setOnClickListener(R.id.ll_content_bg, null);
        }
    }
}
