package com.kaiserkalep.utils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.bean.AdGuideData;
import com.kaiserkalep.bean.ServiceUrlData;
import com.kaiserkalep.widgets.CommTitle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 接口缓存,时间戳每次接口请求都不一样,不能使用okhttp缓存,自己维护缓存
 *
 * @Auther: Jack
 * @Date: 2020/12/11 10:34
 * @Description:
 */
public class NetWorkCaCheUtils {


    public static final String CACHE_NAME_PATH_O = "CACHE_NAME_PATH_O";
    public static final String CACHE_NAME_PATH_T = "CACHE_NAME_PATH_T";
    public static final String CACHE_ORDER_NOTICE_ID = "CACHE_ORDER_NOTICE_ID";


    /**
     * 退出登录,清除用户相关缓存
     */
    public static void clearUserInfo() {
        setMember(null);
        setOrderNoticeId(null);
    }


    /**
     * 加密压缩保存
     *
     * @param name 输出文件名,位于data包下cache内
     * @param path
     */
    public static void encryption(String name, String path) {
        String deFlaterStrokeJson = DeflaterUtils.zipString(JSONUtils.getStringByAss(path, MyApp.getContext()));
        DeflaterUtils.writeFile(deFlaterStrokeJson, name);
    }


    /**
     * 解密压缩
     *
     * @param path
     * @return
     */
    private static String decode(String path) {
        String json = "";
        try {
            json = DeflaterUtils.unzipString(JSONUtils.getStringByAss(path, MyApp.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 设置用户信息
     *
     * @param response
     */
    public static void setMember(Objects response) {
        SPUtil.setStringValue(CACHE_NAME_PATH_O, "MEMBER", JSONUtils.toJson(response));
        if (response != null) {
//            SPUtil.setNickName(response.getNickname());
        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static Objects getMember() {
        String json = SPUtil.getStringValue(CACHE_NAME_PATH_O, "MEMBER");
        Objects list = null;
        if (CommonUtils.StringNotNull(json)) {
            try {
                list = JSONUtils.fromJson(json, Objects.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 设置启动图
     *
     * @param response
     */
    public static void setAdGuideData(AdGuideData response) {
        SPUtil.setStringValue(CACHE_NAME_PATH_O, "AD_GUIDE", JSONUtils.toJson(response));
    }

    /**
     * 获取启动图
     *
     * @return
     */
    public static AdGuideData getAdGuideData() {
        String json = SPUtil.getStringValue(CACHE_NAME_PATH_O, "AD_GUIDE");
        AdGuideData list = null;
        if (CommonUtils.StringNotNull(json)) {
            try {
                list = JSONUtils.fromJson(json, AdGuideData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 设置客服链接
     *
     * @param response
     */
    public static void setServiceUrl(ServiceUrlData response) {
        SPUtil.setStringValue(CACHE_NAME_PATH_T, "SERVICE_URL", JSONUtils.toJson(response));
    }

    /**
     * 获取客服链接
     *
     * @return
     */
    public static ServiceUrlData getServiceUrl() {
        String json = SPUtil.getStringValue(CACHE_NAME_PATH_T, "SERVICE_URL");
        ServiceUrlData list = null;
        if (CommonUtils.StringNotNull(json)) {
            try {
                list = JSONUtils.fromJson(json, ServiceUrlData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 设置已显示过的订单通知
     *
     * @param map
     */
    public static void setOrderNoticeId(HashMap<String,String> map) {
        SPUtil.setStringValue(CACHE_ORDER_NOTICE_ID, "ORDER_NOTICE_ID", JSONUtils.toJson(map));
    }

    /**
     * 获取设置已显示过的订单通知
     *
     * @return
     */
    public static HashMap<String,String> getOrderNoticeId() {
        String json = SPUtil.getStringValue(CACHE_ORDER_NOTICE_ID, "ORDER_NOTICE_ID");
        HashMap<String,String> hashmap = new HashMap<String,String>();
        if (CommonUtils.StringNotNull(json)) {
            try {
                hashmap = JSONUtils.fromJson(json, HashMap.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashmap;
    }

}
