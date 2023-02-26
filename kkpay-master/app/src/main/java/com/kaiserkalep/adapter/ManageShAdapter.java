package com.kaiserkalep.adapter;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.AgentBindInfoBean;
import com.kaiserkalep.interfaces.ManageShInterface;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.FIVE;
import static com.kaiserkalep.constants.Config.FOUR;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class ManageShAdapter extends SimpleAdapter3<AgentBindInfoBean> {

    private ManageShInterface listener;

    public ManageShAdapter(Context context, List<AgentBindInfoBean> mData, ManageShInterface listener) {
        super(context, mData, R.layout.item_managesh);
        this.listener = listener;
    }

    @Override
    public void changeDataUi(List<AgentBindInfoBean> content) {
        super.changeDataUi(content);
    }

    @Override
    public void convert(ViewHolder holder, AgentBindInfoBean item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, View.VISIBLE);
            holder.setBackgroundRes(R.id.rl_content_bg, getBackGroupRes(position + ONE));
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(item.getLogo()), holder.getView(R.id.iv_logo));
            holder.setText(R.id.tv_name, item.getAgentName());
            holder.setText(R.id.tv_account, item.getWalletAddress());
            List<String> website = item.getWebsite();
            if ((null == website || website.size() <= ZERO)) {
                holder.setVisible(R.id.tv_more_url, View.GONE);
            } else {
                holder.setVisible(R.id.tv_more_url, View.VISIBLE);
            }

            //管理
            holder.setOnClickListener(R.id.sl_content, v -> {
                if (null != listener) {
                    listener.manage(item.getId());
                }
            });
            //网址
            holder.setOnClickListener(R.id.tv_more_url, v -> {
                if (null != listener) {
                    listener.more(item.getAgentName(), item.getWebsite());
                }
            });
        } else {
            holder.setBackgroundRes(R.id.rl_content_bg, R.drawable.icon_transparent_bg);
            holder.setVisible(R.id.sl_content, View.GONE);
            holder.setOnClickListener(R.id.sl_content, null);
            holder.setOnClickListener(R.id.tv_more_url, null);
        }
    }

    /**
     * 获取背景图片
     *
     * @param position
     * @return
     */
    private int getBackGroupRes(int position) {
        int index = ZERO;
        try {
            int s = position / FIVE;
            int yu = position % FIVE;
            index = s <= ZERO ? position : yu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (index) {
            case ONE:
                return R.drawable.sh_bg_one;
            case TWO:
                return R.drawable.sh_bg_two;
            case THREE:
                return R.drawable.sh_bg_thr;
            case FOUR:
                return R.drawable.sh_bg_four;
            default:
                return R.drawable.sh_bg_five;
        }
    }
}
