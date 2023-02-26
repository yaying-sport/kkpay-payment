package com.kaiserkalep.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.SimpleAdapter3;
import com.kaiserkalep.base.ViewHolder;
import com.kaiserkalep.bean.WalletManageBean;
import com.kaiserkalep.constants.NumberConstants;
import com.kaiserkalep.interfaces.WalletAddInterface;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.glide.GlideUtil;
import com.kaiserkalep.widgets.shadowLayout.TouchShadowLayout;

import java.util.List;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class WalletAdapter extends SimpleAdapter3<WalletManageBean> {


    private WalletAddInterface listener;


    public WalletAdapter(Context context, List<WalletManageBean> mData, WalletAddInterface listener) {
        super(context, mData, R.layout.item_walletmanage);
        this.listener = listener;
    }


    @Override
    public void changeDataUi(List<WalletManageBean> content) {
        super.changeDataUi(content);
    }

    @Override
    public void convert(ViewHolder holder, WalletManageBean item, int position) {
        if (null != item) {
            holder.setVisible(R.id.sl_content, View.VISIBLE);
            //0:微信;1:支付宝;2,银行卡
            if (item.getType() == 2 && !TextUtils.isEmpty(item.getIcon())) {
                GlideUtil.loadCircleImage(CommonUtils.getImgURL(item.getIcon()), holder.getView(R.id.iv_logo), item.getCircleIcon());
            } else {
                GlideUtil.loadLocalImage(item.getCircleIcon(), holder.getView(R.id.iv_logo));
            }
            holder.setVisible(R.id.view_line, position == ZERO && mData.size() >= NumberConstants.PAYMENT_MAX_NUM);
            String name;
            int type = item.getType();
            RelativeLayout view = holder.getView(R.id.rl_content_bg);
            switch (type) {
                case ZERO:
                    name = UIUtils.getString(R.string.Wechat_name);
                    view.setBackgroundResource(R.drawable.icon_wechat_bg);
                    holder.setText(R.id.tv_no, SPUtil.getRealName());
                    break;
                case ONE:
                    name = UIUtils.getString(R.string.Alipay_name);
                    view.setBackgroundResource(R.drawable.icon_alipay_bg);
                    String ShareAccount = MyApp.getLanguageString(mContext, R.string.Share_Account);
                    holder.setText(R.id.tv_no, ShareAccount + "：" + item.getBankNo());
                    break;
                case TWO:
//                    name = MyApp.getLanguageString(mContext, R.string.Bank_name);
                    name = item.getBankName();
                    view.setBackgroundResource(R.drawable.icon_bank_bg);
                    String ShareBankNo = MyApp.getLanguageString(mContext, R.string.Share_bank_no);
                    holder.setText(R.id.tv_no, ShareBankNo + "：" + item.getBankNo());
                    break;
                default:
                    name = "";
                    view.setBackgroundResource(R.drawable.icon_transparent_bg);
                    holder.setText(R.id.tv_no, "");
                    break;
            }
            item.setBankName(name);
            holder.setText(R.id.tv_name, name);
            holder.setOnClickListener(R.id.iv_click_delete, v -> {
                if (null != listener) {
                    listener.delete(position, item);
                }
            });
        } else {
            holder.setVisible(R.id.sl_content, View.GONE);
            holder.setOnClickListener(R.id.iv_click_delete, null);
        }

    }
}
