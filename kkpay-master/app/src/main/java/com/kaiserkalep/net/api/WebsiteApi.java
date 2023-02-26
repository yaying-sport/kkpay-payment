package com.kaiserkalep.net.api;

/**
 * 网站相关api
 *
 * @Auther: Jack
 * @Date: 2020/8/15 11:53
 * @Description:
 */
public interface WebsiteApi {

    /**
     * 获取启动页广告
     */
    void getWebsiteStartAd();

    /**
     * 获取客服链接
     */
    void getServiceUrl();

    /**
     * 获取用户须知
     */
    void getAbout();

}
