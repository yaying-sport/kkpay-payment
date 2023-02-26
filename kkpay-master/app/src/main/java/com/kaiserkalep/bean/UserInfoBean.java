package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class UserInfoBean implements Serializable {

    private String memberId;
    private String amount;
    private String username;
    private String phone;
    private String avatar;
    private String identityStatus;//0未申请，1实名认证申请，2实名认证驳回，3实名认证通过
    private String identityId;
    private String walletAddress;
    private String layerName;
    private String bankId;
    private String bankNo;
    private String wechatQrcode;
    private String aliAccount;
    private String aliQrcode;
    private String realName;


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAmount() {
        if (CommonUtils.StringNotNull(amount)) {
            return amount;
        }
        return "0";
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        if (CommonUtils.StringNotNull(avatar)) {
            return avatar;
        }
        return "";
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdentityStatus() {
        return identityStatus;
    }

    public void setIdentityStatus(String identityStatus) {
        this.identityStatus = identityStatus;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getWalletAddress() {
        if (CommonUtils.StringNotNull(walletAddress)) {
            return walletAddress;
        }
        return "";
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getWechatQrcode() {
        return wechatQrcode;
    }

    public void setWechatQrcode(String wechatQrcode) {
        this.wechatQrcode = wechatQrcode;
    }

    public String getAliAccount() {
        return aliAccount;
    }

    public void setAliAccount(String aliAccount) {
        this.aliAccount = aliAccount;
    }

    public String getAliQrcode() {
        return aliQrcode;
    }

    public void setAliQrcode(String aliQrcode) {
        this.aliQrcode = aliQrcode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 银行卡
     *
     * @return true 绑定，false未绑定
     */
    public boolean isHaveWeChat() {
        if (CommonUtils.StringNotNull(wechatQrcode)) {
            return true;
        }
        return false;
    }

    /**
     * 支付宝
     *
     * @return true 绑定，false未绑定
     */
    public boolean isHaveAlipay() {
        if (CommonUtils.StringNotNull(aliAccount) && CommonUtils.StringNotNull(aliQrcode)) {
            return true;
        }
        return false;
    }

    /**
     * 银行卡
     *
     * @return true 绑定，false未绑定
     */
    public boolean isHaveBank() {
        if (CommonUtils.StringNotNull(bankId) && CommonUtils.StringNotNull(bankNo)) {
            return true;
        }
        return false;
    }
}
