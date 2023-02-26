package com.kaiserkalep.bean;

import com.kaiserkalep.utils.CommonUtils;
import com.tencent.msdk.dns.core.IStatisticsMerge;

import java.io.Serializable;
import java.util.List;

public class MerchantBean implements Serializable {

    private String code;
    private List<RowsDTO> rows;
    private int total;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RowsDTO> getRows() {
        return rows;
    }

    public void setRows(List<RowsDTO> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static class RowsDTO {
        private String id;
        private String logo;
        private String agentName;
        private String agentAccount;
        private String walletAddress;
        private String agentUsername;
        private int type; // 0最近；1全部

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLogo() {
            if (CommonUtils.StringNotNull(logo)) {
                return logo;
            }
            return "";
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAgentName() {
            if (CommonUtils.StringNotNull(agentName)) {
                return agentName;
            }
            return "";
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getAgentAccount() {
            if (CommonUtils.StringNotNull(agentAccount)) {
                return agentAccount;
            }
            return "";
        }

        public void setAgentAccount(String agentAccount) {
            this.agentAccount = agentAccount;
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

        public String getAgentUsername() {
            if (CommonUtils.StringNotNull(agentUsername)) {
                return agentUsername;
            }
            return "";
        }

        public void setAgentUsername(String agentUsername) {
            this.agentUsername = agentUsername;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }
}
