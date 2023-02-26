package com.kaiserkalep.bean;

import com.kaiserkalep.constants.Config;

import java.io.Serializable;
import java.util.List;

public class ConfigBean implements Serializable {

    private String dnsAppkey;
    private String dnsId;
    private String dnsKey;
    private String iOSDnsSwitch;
    private String androidDnsSwitch;
    private List<String> hostArr;

    public String getDnsAppkey() {
        return dnsAppkey;
    }

    public void setDnsAppkey(String dnsAppkey) {
        this.dnsAppkey = dnsAppkey;
    }

    public String getDnsId() {
        return dnsId;
    }

    public void setDnsId(String dnsId) {
        this.dnsId = dnsId;
    }

    public String getDnsKey() {
        return dnsKey;
    }

    public void setDnsKey(String dnsKey) {
        this.dnsKey = dnsKey;
    }

    public String getIOSDnsSwitch() {
        return iOSDnsSwitch;
    }

    public void setIOSDnsSwitch(String iOSDnsSwitch) {
        this.iOSDnsSwitch = iOSDnsSwitch;
    }

    public boolean  getAndroidDnsSwitch() {
        if (Config.ONE_STRING.equals(androidDnsSwitch)){
            return true;
        }
        return false;
    }

    public void setAndroidDnsSwitch(String androidDnsSwitch) {
        this.androidDnsSwitch = androidDnsSwitch;
    }

    public List<String> getHostArr() {
        return hostArr;
    }

    public void setHostArr(List<String> hostArr) {
        this.hostArr = hostArr;
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "dnsAppkey='" + dnsAppkey + '\'' +
                ", dnsId='" + dnsId + '\'' +
                ", dnsKey='" + dnsKey + '\'' +
                ", iOSDnsSwitch='" + iOSDnsSwitch + '\'' +
                ", androidDnsSwitch='" + androidDnsSwitch + '\'' +
                ", hostArr=" + hostArr +
                '}';
    }
}
