package com.kaiserkalep.bean;

import androidx.annotation.DrawableRes;

import com.kaiserkalep.R;
import com.kaiserkalep.constants.StringConstants;

import java.io.Serializable;

public class PayTypeBean implements Serializable {
    /**
     * (bank-银行卡提款，ali-支付宝，wechat-支付宝)
     */
    private String channelType;
    /**
     * 如果为空，则前端需要使用UI给的默认图
     */
    private String channelIcon;
    /**
     * 渠道的名称
     */
    private String channelTypeName;
    /**
     * 是否绑定过
     */
    private boolean isbind;//true绑定

    /**
     * 是否选中
     */
    private boolean isSelect;
    //银行id
    private String memberBankId;

    public PayTypeBean(boolean isbind, String channelType, String channelTypeName) {
        this.isbind = isbind;
        this.channelType = channelType;
        this.channelTypeName = channelTypeName;
    }


    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public String getChannelTypeName() {
        return channelTypeName;
    }

    public void setChannelTypeName(String channelTypeName) {
        this.channelTypeName = channelTypeName;
    }

    public boolean isIsbind() {
        return isbind;
    }

    public void setIsbind(boolean isbind) {
        this.isbind = isbind;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getMemberBankId() {
        return memberBankId;
    }

    public void setMemberBankId(String memberBankId) {
        this.memberBankId = memberBankId;
    }

    /**
     * 网络图片未空使用本地图片
     *
     * @return
     */
    @DrawableRes
    public static int getLocalIcon(boolean isbind, String type) {
        if (isbind) {
            switch (type) {
                case StringConstants.PAYTYPE_WECHAT:
                    return R.drawable.icon_wechat_logo;
                case StringConstants.PAYTYPE_ALI:
                    return R.drawable.icon_aliba_logo;
                case StringConstants.PAYTYPE_BANK:
                    return R.drawable.icon_bank_logo;
            }
        } else {
            switch (type) {
                case StringConstants.PAYTYPE_WECHAT:
                    return R.drawable.icon_wechat_logo_unbind;
                case StringConstants.PAYTYPE_ALI:
                    return R.drawable.icon_aliba_logo_unbind;
                case StringConstants.PAYTYPE_BANK:
                    return R.drawable.icon_bank_logo_unbind;
            }
        }
        return 0;
    }
}
