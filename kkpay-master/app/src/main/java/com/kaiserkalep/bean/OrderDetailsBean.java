package com.kaiserkalep.bean;

import android.content.Context;
import android.text.TextUtils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class OrderDetailsBean implements Serializable {

    private String orderNo;
    private String sellOrderNo;
    private String username;
    private String memberId;//卖家id
    private String avatar;
    private String amount;
    private String status;//状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消(当前状态，页面展示下一个状态)
    private String payProof;
    private String payType;
    private PayInfoDTO sellUserInfo;
    private PayInfoDTO buyUserInfo;
    private String createTime;
    private String confirmTime;
    private String transferTime;
    private String payTime;
    private String cancelTime;
    private String buySubmitSellerCancel;//买家提单，卖家多少分钟无响应取消  10
    private String sellConfirmBuyerCancel;//卖家确认交易后，买家需要再X分钟内支付  20


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSellOrderNo() {
        return sellOrderNo;
    }

    public void setSellOrderNo(String sellOrderNo) {
        this.sellOrderNo = sellOrderNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayProof() {
        return payProof;
    }

    public void setPayProof(String payProof) {
        this.payProof = payProof;
    }

    public String getPayType() {
        if (CommonUtils.StringNotNull(payType)) {
            return payType;
        }
        return "";
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public PayInfoDTO getSellUserInfo() {
        return sellUserInfo;
    }

    public void setSellUserInfo(PayInfoDTO sellUserInfo) {
        this.sellUserInfo = sellUserInfo;
    }

    public PayInfoDTO getBuyUserInfo() {
        return buyUserInfo;
    }

    public void setBuyUserInfo(PayInfoDTO buyUserInfo) {
        this.buyUserInfo = buyUserInfo;
    }

    public String getCreateTime() {
        if (null == createTime || TextUtils.isEmpty(createTime)) {
            return "";
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getConfirmTime() {
        if (null == confirmTime || TextUtils.isEmpty(confirmTime)) {
            return "";
        }
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getTransferTime() {
        if (null == transferTime || TextUtils.isEmpty(transferTime)) {
            return "";
        }
        return transferTime;
    }

    public void setTransferTime(String transferTime) {
        this.transferTime = transferTime;
    }

    public String getPayTime() {
        if (null == payTime || TextUtils.isEmpty(payTime)) {
            return "";
        }
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getCancelTime() {
        if (null == cancelTime || TextUtils.isEmpty(cancelTime)) {
            return "";
        }
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getBuySubmitSellerCancel() {
        return buySubmitSellerCancel;
    }

    public void setBuySubmitSellerCancel(String buySubmitSellerCancel) {
        this.buySubmitSellerCancel = buySubmitSellerCancel;
    }

    public String getSellConfirmBuyerCancel() {
        return sellConfirmBuyerCancel;
    }

    public void setSellConfirmBuyerCancel(String sellConfirmBuyerCancel) {
        this.sellConfirmBuyerCancel = sellConfirmBuyerCancel;
    }

    public String getTime() {
        if (Config.ZERO_STRING.equals(status)) {
            return buySubmitSellerCancel;
        } else if (Config.ONE_STRING.equals(status) || Config.TWO_STRING.equals(status)) {
            return sellConfirmBuyerCancel;
        } else {
            return Config.TEN_STRING;
        }
    }

    public static class PayInfoDTO {
        private String username;
        private String memberId;
        private String realName;
        private String bank;
        private String account;
        private String qrcode;
        private boolean isSell = false;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public boolean isSell() {
            return isSell;
        }

        public OrderDetailsBean.PayInfoDTO setSell(String str) {
            isSell = str.equals(memberId);
            return this;
        }
    }

    /**
     * (当前状态，页面展示下一个状态)
     * <p>
     * 状态 默认0买家下单，1卖家确认，2买家转账，3，暂停，4卖家打币，-1取消
     *
     * @param context
     * @param type
     * @return
     */
    public static String getOrderDetailStatusTitle(Context context, String type) {
        if (CommonUtils.StringNotNull(type)) {
            switch (type) {
                case Config.ZERO_STRING:
                case Config.ONE_STRING:
                case Config.TWO_STRING:
                    return MyApp.getLanguageString(context, R.string.sale_jinxingzhong);
                case Config.THREE_STRING:
                    return MyApp.getLanguageString(context, R.string.Share_pasue_coin);
                case Config.FOUR_STRING:
                    return MyApp.getLanguageString(context, R.string.sale_yiwancheng);
                case Config.OWN_ONE_STRING:
                    return MyApp.getLanguageString(context, R.string.sale_yiquxiao);
                default:
                    return "";
            }
        } else {
            return "";
        }
    }
}
