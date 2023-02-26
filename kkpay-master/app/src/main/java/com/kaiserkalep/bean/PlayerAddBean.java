package com.kaiserkalep.bean;

import java.io.Serializable;
import java.util.List;

public class PlayerAddBean implements Serializable {

    private String id;
    private String logo;
    private String agentName;
    private String agentAccount;
    private String walletAddress;
    private String qrCode;
    private List<String> website;
    private List<String> agentUsername;

    public String getId() {
        return id;
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

    public List<String> getWebsite() {
        return website;
    }

    public void setWebsite(List<String> website) {
        this.website = website;
    }

    public List<String> getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(List<String> agentUsername) {
        this.agentUsername = agentUsername;
    }
}
