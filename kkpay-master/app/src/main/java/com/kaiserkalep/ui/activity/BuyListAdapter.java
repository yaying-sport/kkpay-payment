package com.kaiserkalep.ui.activity;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.MySellBean;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.bean.BuyListBean;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.util.List;

import static com.kaiserkalep.constants.Config.ONE_STRING;
import static com.kaiserkalep.constants.Config.ZERO;
import static com.kaiserkalep.constants.Config.ZERO_STRING;

public class BuyListAdapter extends SimpleAdapter3<BuyListBean.RowsDTO> {

    private SucceedCallBackListener<BuyListBean.RowsDTO> listener;

    public BuyListAdapter(Context context, List<BuyListBean.RowsDTO> mData, SucceedCallBackListener<BuyListBean.RowsDTO> listener) {
        super(context, mData, R.layout.items_buycenter_item_list);
        this.listener = listener;
    }

    @Override
    public void convert(ViewHolder holder, BuyListBean.RowsDTO item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, true);
            TextView tvBuyMoney = holder.getView(R.id.tv_buy_money);
            tvBuyMoney.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvBuyMoney.setText(Config.subOneAndDot(item.getAmount()));
            GlideUtil.loadCircleImage(BaseNetUtil.jointUrl(item.getAvatar()), holder.getView(R.id.iv_buy_logo));
            String receiveType = item.getReceiveType();
            LinearLayout llSaleItemStateIcon = holder.getView(R.id.ll_pya_type_icon);
            if (llSaleItemStateIcon.getChildCount() > ZERO) {
                llSaleItemStateIcon.removeAllViews();
            }
            if (CommonUtils.StringNotNull(receiveType)) {
                String[] split = receiveType.split(StringConstants.comma);
                int wheight = UIUtils.dip2px(17);
                int Margin = UIUtils.dip2px(4);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wheight, wheight);
                layoutParams.rightMargin = Margin;
                layoutParams.gravity = Gravity.CENTER;
                for (int i = 0; i < split.length; i++) {
                    int payStrIcon = WalletManageBean.getPayStrIcon(split[i]);
                    if (Config.EGATIVE_ONE != payStrIcon) {
                        ImageView imageView = new ImageView(mContext);
                        imageView.setImageResource(payStrIcon);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setLayoutParams(layoutParams);
                        llSaleItemStateIcon.addView(imageView);
                    }
                }
            }
            String cansplit = item.getCanSplit();
            TextView tvChaiItemState = holder.getView(R.id.tv_chai_state);
            if (ONE_STRING.equalsIgnoreCase(cansplit)) {
                tvChaiItemState.setTextColor(mContext.getResources().getColor(R.color.buy_canSplit_true));
                tvChaiItemState.setText(MyApp.getLanguageString(mContext, R.string.buy_item_chai));
            } else if (ZERO_STRING.equals(cansplit)) {
                tvChaiItemState.setTextColor(mContext.getResources().getColor(R.color.buy_canSplit_false));
                tvChaiItemState.setText(MyApp.getLanguageString(mContext, R.string.buy_item_nochai));
            } else {
                tvChaiItemState.setTextColor(mContext.getResources().getColor(R.color.buy_canSplit_false));
                tvChaiItemState.setText("*");
            }
            holder.setOnClickListener(R.id.btn_buy, v -> {
                if (null != listener) {
                    listener.succeedCallBack(item);
                }
            });
        } else {
            holder.setVisible(R.id.sl_content, false);
            holder.setOnClickListener(R.id.tv_chai_state, null);
        }
    }
}
