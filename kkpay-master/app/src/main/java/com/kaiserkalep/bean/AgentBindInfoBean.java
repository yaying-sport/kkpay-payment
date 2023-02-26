package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;

import java.io.Serializable;
import java.util.List;

public class AgentBindInfoBean implements Serializable {

    private String id;
    private String logo;
    private String agentName;
    private String agentAccount;
    private String walletAddress;
    private String qrCode;
    private String isBand;
    private List<String> website;

    public String getId() {
        if (CommonUtils.StringNotNull(id)) {
            return id;
        }
        return "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentAccount() {
        return agentAccount;
    }

    public void setAgentAccount(String agentAccount) {
        this.agentAccount = agentAccount;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getIsBand() {
        if (CommonUtils.StringNotNull(isBand)) {
            return isBand;
        }
        return "";
    }

    public void setIsBand(String isBand) {
        this.isBand = isBand;
    }

    public List<String> getWebsite() {
        return website;
    }

    public void setWebsite(List<String> website) {
        this.website = website;
    }
}
