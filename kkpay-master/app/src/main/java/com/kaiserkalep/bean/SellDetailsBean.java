package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;

public class SellDetailsBean implements Serializable {

    private String orderNo;
    private String status;
    private String username;
    private String amount;
    private String amountDeal;
    private String amountCancel;
    private String amountFreeze;
    private String avatar;
    private String receiveType;
    private String canSplit;
//    private String bankNo;
//    private String wechatQrcode;
//    private String aliAccount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        if (CommonUtils.StringNotNull(username)) {
            return username;
        }
        return "";
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAmountDeal() {
        if (CommonUtils.StringNotNull(amountDeal)) {
            return amountDeal;
        }
        return "0";
    }

    public void setAmountDeal(String amountDeal) {
        this.amountDeal = amountDeal;
    }

    public String getAmountCancel() {
        if (CommonUtils.StringNotNull(amountCancel)) {
            return amountCancel;
        }
        return "0";
    }

    public void setAmountCancel(String amountCancel) {
        this.amountCancel = amountCancel;
    }

    public String getAmountFreeze() {
        if (CommonUtils.StringNotNull(amountFreeze)) {
            return amountFreeze;
        }
        return "0";
    }

    public void setAmountFreeze(String amountFreeze) {
        this.amountFreeze = amountFreeze;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getCanSplit() {
        return canSplit;
    }

    public void setCanSplit(String canSplit) {
        this.canSplit = canSplit;
    }

//    public String getBankNo() {
//        return bankNo;
//    }
//
//    public void setBankNo(String bankNo) {
//        this.bankNo = bankNo;
//    }
//
//    public String getWechatQrcode() {
//        return wechatQrcode;
//    }
//
//    public void setWechatQrcode(String wechatQrcode) {
//        this.wechatQrcode = wechatQrcode;
//    }
//
//    public String getAliAccount() {
//        return aliAccount;
//    }
//
//    public void setAliAccount(String aliAccount) {
//        this.aliAccount = aliAccount;
//    }
}
