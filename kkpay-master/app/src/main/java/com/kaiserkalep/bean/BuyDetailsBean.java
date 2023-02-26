package com.kaiserkalep.bean;

import java.io.Serializable;

public class BuyDetailsBean implements Serializable {

    private String orderNo;
    private String sellOrderNo;
    private String username;
    private String amount;
    private Integer status;
    private String payProof;
    private SellUserInfoDTO sellUserInfo;
    private BuyUserInfoDTO buyUserInfo;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayProof() {
        return payProof;
    }

    public void setPayProof(String payProof) {
        this.payProof = payProof;
    }

    public SellUserInfoDTO getSellUserInfo() {
        return sellUserInfo;
    }

    public void setSellUserInfo(SellUserInfoDTO sellUserInfo) {
        this.sellUserInfo = sellUserInfo;
    }

    public BuyUserInfoDTO getBuyUserInfo() {
        return buyUserInfo;
    }

    public void setBuyUserInfo(BuyUserInfoDTO buyUserInfo) {
        this.buyUserInfo = buyUserInfo;
    }

    public static class SellUserInfoDTO implements Serializable{
        private String username;
        private String realName;
        private String bank;
        private String account;
        private String qrcode;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public Object getBank() {
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
    }

    public static class BuyUserInfoDTO implements Serializable  {
        private String username;
        private String realName;
        private String bank;
        private String account;
        private String qrcode;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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
    }
}
