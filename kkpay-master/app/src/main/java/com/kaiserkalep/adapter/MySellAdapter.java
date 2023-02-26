package com.kaiserkalep.adapter;

import android.content.Context;
import android.widget.LinearLayout;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.interfaces.SucceedCallBackListener;

import java.util.List;

public class MySellAdapter extends SimpleAdapter3<MySellBean.RowsDTO> {

    private SucceedCallBackListener<MySellBean.RowsDTO> listener;


    public MySellAdapter(Context context, List<MySellBean.RowsDTO> mData, SucceedCallBackListener<MySellBean.RowsDTO> listener) {
        super(context, mData, R.layout.mysell_item_layout);
        this.listener = listener;
    }

    @Override
    public void convert(ViewHolder holder, MySellBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_user_content, true);
            holder.setText(R.id.tv_money, Config.subOneAndDot(item.getAmount()));
            holder.setText(R.id.tv_item_date_time, MyApp.getLanguageString(mContext, R.string.guadan_time) + "：" + item.getCreateTime());
            holder.setOnClickListener(R.id.ll_parent_view, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item);
                }
            });
        } else {
            holder.setVisible(R.id.sl_user_content, false);
            holder.setOnClickListener(R.id.ll_parent_view, null);
        }
    }
}
