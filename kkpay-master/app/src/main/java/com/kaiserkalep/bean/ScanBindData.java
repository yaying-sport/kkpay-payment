package com.kaiserkalep.bean;

import java.io.Serializable;

public class ScanBindData implements Serializable {

    String agentName;
    String logo;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
