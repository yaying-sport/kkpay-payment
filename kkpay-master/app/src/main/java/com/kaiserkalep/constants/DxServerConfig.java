package com.kaiserkalep.constants;

import java.io.Serializable;

public class DxServerConfig implements Serializable {

    public static int PERCENT_WIDTH = -1;

    public String appid = "";
    public String api_server = "";
    public String ua_js = "";
    public String cap_js = "";
    public String const_id_server = "";
    public String const_id_js = "";

    private DxServerConfig() {
    }

    public DxServerConfig(String id, String apiServer) {
        this.appid = id;
        this.api_server = apiServer;
        this.ua_js = api_server + "/dx-captcha/libs/greenseer.js";
        this.cap_js = api_server + "/dx-captcha/index.js";
        this.const_id_server = api_server + "/udid/m1";
        this.const_id_js = api_server + "/dx-captcha/libs/const-id.js";
    }

    public String getAppid() {
        return appid;
    }

    public String getApi_server() {
        return api_server;
    }

    public String getUa_js() {
        return ua_js;
    }

    public String getCap_js() {
        return cap_js;
    }

    public String getConst_id_server() {
        return const_id_server;
    }

    public String getConst_id_js() {
        return const_id_js;
    }
}
