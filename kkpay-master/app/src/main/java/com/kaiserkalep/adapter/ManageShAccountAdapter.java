package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.interfaces.ManageShInterface;
import com.kaiserkalep.interfaces.SelectBankInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.FIVE;
import static com.kaiserkalep.constants.Config.FOUR;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class ManageShAccountAdapter extends SimpleAdapter3<String> {

    private String logo;
    private SelectBankInterface<String> listener;

    public ManageShAccountAdapter(Context context, List<String> mData, SelectBankInterface<String> listener) {
        super(context, mData, R.layout.item_managesh_account);
        this.listener = listener;
    }

    public void changeDataUi(String logo, List<String> content) {
        this.logo = logo;
        super.changeDataUi(content);
    }


    @Override
    public void convert(ViewHolder holder, String item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, View.VISIBLE);
            holder.setVisible(R.id.v_line, position == mData.size() - ONE ? View.GONE : View.VISIBLE);
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(logo), holder.getView(R.id.iv_logo));
            holder.setText(R.id.tv_account, item);
            holder.setOnClickListener(R.id.tv_del, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item, position);
                }
            });
        } else {
            holder.setVisible(R.id.sl_content, View.GONE);
            holder.setOnClickListener(R.id.tv_del, null);
        }
    }
}
