package com.kaiserkalep.bean;

import java.io.Serializable;
import java.util.List;

public class AgentBindListBean implements Serializable {

    private Integer code;
    private List<AgentBindInfoBean> rows;
    private Integer total;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<AgentBindInfoBean> getRows() {
        return rows;
    }

    public void setRows(List<AgentBindInfoBean> rows) {
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
