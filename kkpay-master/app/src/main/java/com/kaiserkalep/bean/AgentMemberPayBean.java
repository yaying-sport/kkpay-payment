package com.kaiserkalep.bean;

import java.io.Serializable;

public class AgentMemberPayBean implements Serializable {
    //，0未绑定，1已绑定
    private String agent;//商户
    private String username;//玩家

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
