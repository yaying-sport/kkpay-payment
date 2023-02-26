package com.kaiserkalep.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.MyApp;

import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.bean.UserInfoBean;
import com.kaiserkalep.bean.WalletManageBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SPUtil {

    public static final String VERSION_CODE = "version_code";
    public static final String SP_NAME = "com.zz.settings";
    public static final String TOKEN = "token";
    public static final String NICK_NAME = "nick_name";//用户昵称
    public static final String REAL_NAME = "real_name";//真实姓名
    public static final String USER_AVATAR = "user_avatar";//用户头像
    public static final String USER_WALLET_ADDRESS = "user_Wallet_Address";//用户钱包地址
    public static final String VERIFY_IDENTITYSTATUS = "verify_IdentityStatus";//是否实名认证 0未申请，1实名认证申请，2实名认证驳回，3实名认证通过

    public static final String DEVICE_ID = "deviceId";
    public static final String USER_PHONE = "userPhone";//用户手机
    public static final String UPDATE_MD5 = "update_md5";//更新下载apkmd5
    public static final String DOWN_BROWSER_URL = "down_browser_url";//浏览器更新链接
    public static final String APK_DOWNLOAD_ID = "apk_download_id";//更新下载任务id
    public static final String DEFAULT_DOMAIN_URL = "default_domain_url";//默认域名
    public static final String DEFAULT_DOMAIN_DNS_URL = "default_domain_DNS_url";//默认dns域名
    public static final String SYSTEM_START_TIME = "system_start_time"; //app启动时间

    public static final String PAYMENT_MODE_LIST = "payment_mode_list"; //支付方式集合
    public static final String REMEMBER_NO_TIP = "remember_no_tip";// 弹框公告今日不再提示

    public static final String SOFT_INPUT_HEIGHT = "softInputHeight";

    public static final String REGIST_NOTICE = "Regist_Notice";//注册须知
    public static final String SELL_ORDER_NOTICE = "Sell_Order_Notice";//卖单须知
    public static final String BUY_ORDER_NOTICE = "Buy_Order_Notice";//买单须知
    public static final String HELP_URL = "help_url";//帮助url

    public static final String USER_ID = "userId";//用户id
    public static final String AD_STRING = "ad_string"; //广告数据
    public static final String VERSION_STATUS = "scratch_status";//点击更新红点
    public static final String IS_FIRSTSTARTAPP_NOTIFY = "is_first_start_app_notify";//是否第一次启动设置通知（MainActivity中第一次启动时提示用户去设置开启通知声音悬浮权限）

    public static final String SK = "sk"; //签名


    public static String getStringValue(String key) {
        return getStringValue(SP_NAME, key);
    }


    public static String getStringValue(String name, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(name);
        if (null != sharedPreferences) {
            try {
                return sharedPreferences.getString(key, "");
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putString(key, "").commit();
            }
        }
        return "";
    }

    public static boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    public static boolean getBooleanValue(String key, boolean def) {
        return getBooleanValue(SP_NAME, key, def);
    }

    public static boolean getBooleanValue(String name, String key, boolean def) {
        SharedPreferences sharedPreferences = getSharedPreferences(name);
        if (null != sharedPreferences) {
            try {
                return sharedPreferences.getBoolean(key, def);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putBoolean(key, def).commit();
            }
        }
        return false;
    }

    public static void setBooleanValue(String key, boolean value) {
        setBooleanValue(SP_NAME, key, value);
    }

    public static void setBooleanValue(String name, String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences(name);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public static void setStringValue(String key, String value) {
        setStringValue(SP_NAME, key, value);
    }

    public static void setStringValue(String name, String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(name);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, value).commit();
        }
    }

    public static void setSetValue(String key, Set<String> set) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            sharedPreferences.edit().putStringSet(key, set).commit();
        }
    }

    public static Set<String> getSetValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return sharedPreferences.getStringSet(key, new HashSet<>());
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putStringSet(key, new HashSet<>()).commit();
            }
        }
        return null;
    }

    public static void setObject(String key, Object valueObject) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (valueObject != null) {
            sharedPreferences.edit().putString(key, new Gson().toJson(valueObject)).commit();
        }
    }

    public static Object getObject(String key) {
        String stringObject = getSharedPreferences().getString(key, "");
        return TextUtils.isEmpty(stringObject) ? null : new Gson().fromJson(stringObject, Object.class);
    }

    public static int getIntValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return getSharedPreferences().getInt(key, 0);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putInt(key, 0).commit();
            }
        }
        return -1;
    }

    public static int getIntValue(String key, int defValue) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return sharedPreferences.getInt(key, defValue);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putInt(key, defValue).commit();
            }
        }
        return -1;
    }

    public static void setIntValue(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(key, value).commit();
        }
    }

    public static long getLongValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return sharedPreferences.getLong(key, 0);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putLong(key, 0).commit();
            }
        }
        return -1;
    }

    public static void setLongValue(String key, long value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong(key, value).commit();
        }
    }

    public static float getFloatValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return getSharedPreferences().getFloat(key, 0);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putFloat(key, 0).commit();
            }
        }
        return -1;
    }

    public static float getFloatValue(String key, Float defValue) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (null != sharedPreferences) {
            try {
                return getSharedPreferences().getFloat(key, defValue);
            } catch (ClassCastException e) {
                e.printStackTrace();
                sharedPreferences.edit().putFloat(key, defValue).commit();
            }
        }
        return -1;
    }

    public static void setFloatValue(String key, Float value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().putFloat(key, value).commit();
        }
    }


    public static void setToken(String token) {
        setStringValue(TOKEN, token);
    }

    public static String getToken() {
        return getSharedPreferences().getString(TOKEN, "");
    }


    public static void setNickName(String nickName) {
        setStringValue(NICK_NAME, nickName);
    }

    public static String getNickName() {
        return getSharedPreferences().getString(NICK_NAME, "");
    }

    public static void setRealName(String realname) {
        setStringValue(REAL_NAME, realname);
    }

    public static String getRealName() {
        return getSharedPreferences().getString(REAL_NAME, "");
    }

    public static void setUserAvatar(String nickName) {
        setStringValue(USER_AVATAR, nickName);
    }

    public static String getUserAvatar() {
        return getSharedPreferences().getString(USER_AVATAR, "");
    }

    public static void setUserWalletAddress(String userwalletaddress) {
        setStringValue(USER_WALLET_ADDRESS, userwalletaddress);
    }

    public static String getUserWalletAddress() {
        return getSharedPreferences().getString(USER_WALLET_ADDRESS, "");
    }

    public static void setVerifyIdentitystatus(int verify) {
        setIntValue(VERIFY_IDENTITYSTATUS, verify);
    }

    public static int getVerifyIdentitystatus() {
        return getSharedPreferences().getInt(VERIFY_IDENTITYSTATUS, 0);
    }


    /**
     * 设置弹框公告今日不再提示
     *
     * @param dialogTag
     */
    public static void setRememberNoTip(NoticeDialogData.DialogTag dialogTag) {
        setStringValue(REMEMBER_NO_TIP, JSONUtils.toJson(dialogTag));
    }

    /**
     * 获取弹框公告今日不再提示
     *
     * @return
     */
    public static NoticeDialogData.DialogTag getRememberNoTip() {
        String string = getSharedPreferences().getString(REMEMBER_NO_TIP, "");
        if (CommonUtils.StringNotNull(string)) {
            return JSONUtils.fromJson(string, NoticeDialogData.DialogTag.class);
        }
        return null;
    }

    /**
     * 注册须知
     */
    public static String getRegistNotice() {
        return getSharedPreferences().getString(REGIST_NOTICE, "");
    }

    public static void setRegistNotice(String registNotice) {
        setStringValue(REGIST_NOTICE, registNotice);
    }

    /**
     * 卖单须知
     */
    public static String getSellOrderNotice() {
        return getSharedPreferences().getString(SELL_ORDER_NOTICE, "");
    }

    public static void setSellOrderNotic(String sellOrderNotic) {
        setStringValue(SELL_ORDER_NOTICE, sellOrderNotic);
    }

    /**
     * 买单须知
     */
    public static String getBuyOrderNotice() {
        return getSharedPreferences().getString(BUY_ORDER_NOTICE, "");
    }

    public static void setBuyOrderNotic(String buyOrderNotic) {
        setStringValue(BUY_ORDER_NOTICE, buyOrderNotic);
    }

    /**
     * 帮助url
     */
    public static String getHelpUrl() {
        return getSharedPreferences().getString(HELP_URL, "");
    }

    public static void setHelpUrl(String helpUrl) {
        setStringValue(HELP_URL, helpUrl);
    }

    public static void setSk(String token) {
        setStringValue(SK, token);
    }

    public static String getSk() {
        return getSharedPreferences().getString(SK, "");
    }

    public static void setDeviceId(String deviceId) {
        setStringValue(DEVICE_ID, deviceId);
    }

    public static String getDeviceId() {
        return getSharedPreferences().getString(DEVICE_ID, "");
    }

    public static void setUserPhone(String userPhone) {
        setStringValue(USER_PHONE, userPhone);
    }

    public static String getUserPhone() {
        return getSharedPreferences().getString(USER_PHONE, "");
    }

    public static void setUserId(String userId) {
        setStringValue(USER_ID, userId);
    }

    public static String getUserId() {
        return getSharedPreferences().getString(USER_ID, "");
    }

    public static void setLoginSuccess(UserData response) {
        if (response != null) {
            SharedPreferences sharedPreferences = getSharedPreferences();
            Log.e("answer", "--:" + response.getPhone());
            if (sharedPreferences != null) {
                sharedPreferences.edit()
                        .putString(TOKEN, response.getToken())
                        .putString(USER_PHONE, response.getPhone())
                        .putString(NICK_NAME, response.getUsername())
                        .putString(USER_AVATAR, response.getAvatarFile())
                        .putString(SK, response.getSk()).apply();
            }
        }
    }

    /**
     * information接口成功后更新信息
     *
     * @param response
     */
    public static void setUserInfo(UserInfoBean response) {
        if (response != null) {
            SharedPreferences sharedPreferences = getSharedPreferences();
            if (sharedPreferences != null) {
                sharedPreferences.edit()
                        .putString(USER_ID, response.getMemberId())
                        .putString(NICK_NAME, response.getUsername())
                        .putString(REAL_NAME, response.getRealName())
                        .putString(USER_PHONE, response.getPhone())
                        .putString(USER_AVATAR, response.getAvatar())
                        .putString(VERIFY_IDENTITYSTATUS, response.getIdentityStatus())
                        .putString(USER_WALLET_ADDRESS, response.getWalletAddress()).apply();
            }
        }
    }

    public static void setLoginOut() {
        setToken("");
        setSk("");
        setStringValue(PAYMENT_MODE_LIST, "");
        new Thread(NetWorkCaCheUtils::clearUserInfo).start();
    }

    /**
     * 判断是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        Context context = MyApp.getContext();
        return context != null && CommonUtils.StringNotNull(getToken());
    }

    public static SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SP_NAME);
    }

    public static SharedPreferences getSharedPreferences(String fileName) {
        Context context = MyApp.getContext();
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 删除指定key的缓存数据
     *
     * @param key
     */
    public static void removeKeyValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(key);
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().commit();
        }
    }


    public static void setSoftInputHeight(int value) {
        setIntValue(SOFT_INPUT_HEIGHT, value);
    }

    public static int getSoftInputHeight() {
        return getIntValue(SOFT_INPUT_HEIGHT, 0);
    }

    /**
     * 设置app启动时间
     */
    public static void setSystemStartTime() {
        setLongValue(SYSTEM_START_TIME, System.currentTimeMillis());
    }

    /**
     * 获取app启动时间
     */
    public static long getSystemStartTime() {
        return getLongValue(SYSTEM_START_TIME);
    }


    /**
     * 清除广告数据
     */
    public static void cleanAdvertData() {
        SharedPreferences sharedPreferences = getSharedPreferences(AD_STRING);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(AD_STRING, "").apply();
        }
    }

    /**
     * 设置广告数据
     *
     * @param advertData
     */
    public static void setAdvertData(String advertData) {
        if (CommonUtils.StringNotNull(advertData)) {
            cleanAdvertData();
            SharedPreferences sharedPreferences = getSharedPreferences(AD_STRING);
            if (sharedPreferences != null) {
                sharedPreferences.edit().putString(AD_STRING, CommonUtils.StringNotNull(advertData) ? advertData : "").apply();
            }
        }
    }


    /**
     * 获取广告整体
     *
     * @return
     */
    private static String getAdvertString() {
        SharedPreferences sharedPreferences = getSharedPreferences(AD_STRING);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(AD_STRING, "");
        }
        return "";
    }


    /**
     * 设置更新apk的md5
     *
     * @param md5
     */
    public static void setUpdateMd5(String md5) {
        if (CommonUtils.StringNotNull(md5)) {
            setStringValue(UPDATE_MD5, md5);
        }
    }

    /**
     * 获取更新apk的md5
     *
     * @return
     */
    public static String getUpdateMd5() {
        return getSharedPreferences().getString(UPDATE_MD5, "");
    }

    /**
     * 设置更新浏览器链接
     *
     * @param browserUrl
     */
    public static void setDownBrowserUrl(String browserUrl) {
        if (CommonUtils.StringNotNull(browserUrl)) {
            setStringValue(DOWN_BROWSER_URL, browserUrl);
        }
    }

    /**
     * 获取更新浏览器链接
     *
     * @return
     */
    public static String getDownBrowserUrl() {
        return getSharedPreferences().getString(DOWN_BROWSER_URL, "");
    }


    /**
     * 设置更新apk下载任务id
     *
     * @param downloadId
     */
    public static void setDownloadId(long downloadId) {
        setLongValue(APK_DOWNLOAD_ID, downloadId);
    }

    /**
     * 获取更新apk的下载任务id
     *
     * @return
     */
    public static long getDownloadId() {
        return getSharedPreferences().getLong(APK_DOWNLOAD_ID, -1);
    }

    /**
     * 重置apk更新相关数据
     */
    public static void reSetUpdate() {
        setUpdateMd5("");
        setDownBrowserUrl("");
        setDownloadId(-1);
    }


    /**
     * 设置默认域名
     *
     * @param urls
     */
    public static void setDefaultDomainUrl(List<String> urls) {
        setStringValue(DEFAULT_DOMAIN_URL, JSONUtils.toJson(new ArrayList<>(new LinkedHashSet<>(urls))));
    }

    /**
     * 获取默认域名
     *
     * @return
     */
    public static List<String> getDefaultDomainUrl() {
        List<String> urls = new ArrayList<>();
        String s = getStringValue(DEFAULT_DOMAIN_URL);
        if (CommonUtils.StringNotNull(s)) {
            urls = JSONUtils.fromJson(s,
                    new TypeToken<ArrayList<String>>() {
                    });
        }
        return urls;
    }

    /**
     * 设置默认Dns域名
     *
     * @param urls
     */
    public static void setDefaultDomainDnsUrl(List<String> urls) {
        setStringValue(DEFAULT_DOMAIN_DNS_URL, JSONUtils.toJson(new ArrayList<>(new LinkedHashSet<>(urls))));
    }

    /**
     * 获取默认Dns域名
     *
     * @return
     */
    public static List<String> getDefaultDomainDnsUrl() {
        List<String> urls = new ArrayList<>();
        String s = getStringValue(DEFAULT_DOMAIN_DNS_URL);
        if (CommonUtils.StringNotNull(s)) {
            urls = JSONUtils.fromJson(s,
                    new TypeToken<ArrayList<String>>() {
                    });
        }
        return urls;
    }

    /**
     * 设置页面点击清除缓存
     * 重置部分数据
     */
    public static void clearCache() {
        SPUtil.setBooleanValue(SPUtil.IS_FIRSTSTARTAPP_NOTIFY, false);
        SPUtil.setRememberNoTip(new NoticeDialogData.DialogTag());
    }

    public static List<WalletManageBean> getPaymentList() {
        List<WalletManageBean> myWalletList = (List<WalletManageBean>) BaseNetUtil.parseFromJsonArray(SPUtil.getStringValue(SPUtil.PAYMENT_MODE_LIST), WalletManageBean.class);
        if (myWalletList == null) {
            myWalletList = new ArrayList<>();
        }
        return myWalletList;
    }

    public static boolean[] getPaymentEnable() {
        boolean[] result = new boolean[3];
        List<WalletManageBean> myWalletList = SPUtil.getPaymentList();
        for (WalletManageBean w : myWalletList) {
            switch (w.getType()) {
                case 0:
                    result[0] = true;
                    break;
                case 1:
                    result[1] = true;
                    break;
                case 2:
                    result[2] = true;
                    break;
            }
        }
        return result;
    }
}
