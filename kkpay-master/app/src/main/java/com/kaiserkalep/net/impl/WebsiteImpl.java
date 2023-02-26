package com.kaiserkalep.net.impl;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.CallbackBase;
import com.kaiserkalep.base.NetManagerBase;
import com.kaiserkalep.net.api.WebsiteApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.kaiserkalep.constants.StringConstants.*;
import static com.kaiserkalep.net.url.HomeUrl.*;

/**
 * 网站相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 11:54
 * @Description:
 */
public class WebsiteImpl extends NetManagerBase implements WebsiteApi {


    public WebsiteImpl(CallbackBase callBack) {
        super(callBack);
    }

    /**
     * 获取启动页广告
     */
    @Override
    public void getWebsiteStartAd() {
        BaseNetUtil.get(GET_WEBSITE_START_AD, null, callBack);
    }

    /**
     * 获取客服链接
     */
    @Override
    public void getServiceUrl() {
        BaseNetUtil.post(GET_SERVICE_URL, null, callBack);
    }

    /**
     * 获取用户须知
     */
    @Override
    public void getAbout() {
        BaseNetUtil.get(GET_ABOUT, null, callBack);
    }

}
