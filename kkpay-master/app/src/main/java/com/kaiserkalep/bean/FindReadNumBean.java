package com.kaiserkalep.bean;

import java.io.Serializable;

public class FindReadNumBean implements Serializable {

    private String num;
    private String numId;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNumId() {
        return numId;
    }

    public void setNumId(String numId) {
        this.numId = numId;
    }
}
