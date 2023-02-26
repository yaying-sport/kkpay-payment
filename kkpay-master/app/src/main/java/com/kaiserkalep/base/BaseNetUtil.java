package com.kaiserkalep.base;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.HTTPSCerUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.SPUtil;;
import com.kaiserkalep.utils.StringUtils;
import com.kaiserkalep.utils.XDns;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import ikidou.reflect.TypeBuilder;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.kaiserkalep.constants.Config.ZERO;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:00
 * @Description:
 */
public class BaseNetUtil {

    private static final String TAG = "BaseNetUtil";
    public static final String OK_HTTP_LOG_TAG = "yaying_okhttp";
    public static final TimeUnit DEF_TIME_UNIT = TimeUnit.MILLISECONDS;//毫秒
    public static final int TIME_OUT_MILLIS = 30 * 1000;//超时时间,毫秒
    public static final int TIME_OUT_MILLIS_ORDER = 60 * 1000;//下单接口超时时间
    public static final int TIME_OUT_MILLIS_LONG = 20 * 1000;//超时时间,毫秒(一键回收等...耗时较长)
    private static boolean OPEN_DNS = false;//默认关闭
    public final static boolean TURNOFF_ENCRYPTION = true;//是否加密

    /**
     * 上传图片接口接口时间 90秒超时
     */
    public static final int singleTime = 120 * 1000;
    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    //设置缓存目录
    public static File cacheFile = new File(MyApp.getApplication().getCacheDir(),
            MyApp.getApplication().getString(R.string.ZZNetWorkCache));
    static Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

    public static boolean getOpenDns() {
        return OPEN_DNS;
    }

    public static void setOpenDns(boolean openDns) {
        OPEN_DNS = openDns;
    }

    /**
     * 全局host,api/图片
     */
    public static String HOST = "";//当前使用域名（解析后）
    public static String HOST_IP = "";//IP （12.21.22.2）
    public static String HOST_YM = "";//YM （www.text.com）

    private BaseNetUtil() {
    }

    public static void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //信任所有证书
        HTTPSCerUtils.setTrustAllCertificate(builder);
        /**
         * 线上包禁止代理抓包   三处有设置
         *
         * DefaultDomainUtils  域名拉取后效验接口
         *
         * DomainErrorLogUtils 单独异常上报接口
         */
        if (!MyApp.isDebug) {
            builder.proxy(Proxy.NO_PROXY);
        } else {
            builder.addInterceptor(new LoggerInterceptor(OK_HTTP_LOG_TAG, true));
        }
        OkHttpClient okHttpClient = builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                try {
                    LogUtils.d(OK_HTTP_LOG_TAG, "hostname=" + hostname + "session=" +
                            session.getPeerHost() + "PeerCertificates" + session.getPeerCertificates().toString());
                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
                }
                return true;
            }
        })

                .connectTimeout(TIME_OUT_MILLIS, DEF_TIME_UNIT)
                .readTimeout(TIME_OUT_MILLIS, DEF_TIME_UNIT)
                .writeTimeout(TIME_OUT_MILLIS, DEF_TIME_UNIT)
                .dns(new XDns(TIME_OUT_MILLIS, DEF_TIME_UNIT))
                .addInterceptor(new MyInterceptor())//添加获取token的拦截器
//                .addNetworkInterceptor(new HttpCacheInterceptor())
                .cache(cache)
                .retryOnConnectionFailure(true).
                        cookieJar(new CookieJar() {
                            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                            //Tip：這裡key必須是String
                            @Override
                            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                cookieStore.put(url.host(), cookies);
                            }

                            @Override
                            public List<Cookie> loadForRequest(HttpUrl url) {
                                List<Cookie> cookies = cookieStore.get(url.host());
                                return cookies != null ? cookies : new ArrayList<Cookie>();
                            }
                        })
                .build();
        okHttpClient.dispatcher().setMaxRequestsPerHost(10);
        okHttpClient.dispatcher().setMaxRequests(30);
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 通用的异步get请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 回调接口
     */
    public static void get(String url, Map<String, String> params, final Callback callback) {
        url = jointUrl(url);
        if (!CommonUtils.isContainsHttp(url)) {//拦截非正常请求url
            return;
        }
        LogUtils.e("httprequest  " + url + "\n    params = " + params + "\n");
        GetBuilder builder = OkHttpUtils.get().url(url);
        addHeadParams(builder);
        if (null != params && params.size() > ZERO) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry != null) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (CommonUtils.StringNotNull(key, value)) {
                        builder.addParams(key, value);
                    }
                }
            }
        }
        RequestCall build = null;
        if (callback != null && callback instanceof CallbackBase) {
            CallbackBase callbackBase = (CallbackBase) callback;
            if (CommonUtils.StringNotNull(callbackBase.getCancelTag())) {
                builder = builder.tag(callbackBase.getCancelTag());
            }
            long timeOut = callbackBase.getTimeOut();
            build = builder.build();
            build.connTimeOut(timeOut).readTimeOut(timeOut).writeTimeOut(timeOut);
            OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
            if (okHttpClient != null) {
                if (okHttpClient.dns() instanceof XDns) {
                    ((XDns) okHttpClient.dns()).setTimeout(timeOut);
                }
            }
        }
        if (build == null) {
            build = builder.build();
        }
        build.execute(callback);
    }

    /**
     * 通用的异步post请求
     *
     * @param url        请求地址
     * @param postParams post请求参数
     * @param callback   回调接口
     */
    public static void post(String url, Map<String, String> postParams, final Callback callback) {
        String json = "";
        url = jointUrl(url);
        if (!CommonUtils.isContainsHttp(url)) {//拦截非正常请求url
            return;
        }
        if (null != postParams && postParams.size() > ZERO) {
            Gson gson = new Gson();
            json = gson.toJson(postParams);
//            Log.e("answer", "post:" + url + "--------" + json);
            LogUtils.e("httprequest  " + url + "\n    params= " + json + "\n");
            if (TURNOFF_ENCRYPTION) {
                Map<String, String> map = new HashMap<>();
                map.put("encryptData", EncryptRsa.encryptByPubKey(json));
                json = gson.toJson(map);
            }
        }
        url = jointUrl(url);
        if (!CommonUtils.isContainsHttp(url)) {//拦截非正常请求url
            return;
        }
        PostStringBuilder builder = OkHttpUtils.postString().content(json).mediaType(mediaType).url(url);
        addHeadParams(builder);
        RequestCall build = null;
        if (callback != null && callback instanceof CallbackBase) {
            CallbackBase callbackBase = (CallbackBase) callback;
            if (CommonUtils.StringNotNull(callbackBase.getCancelTag())) {
                builder = builder.tag(callbackBase.getCancelTag());
            }
            long timeOut = callbackBase.getTimeOut();
            build = builder.build();
            build.connTimeOut(timeOut).readTimeOut(timeOut).writeTimeOut(timeOut);
            OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
            if (okHttpClient != null && okHttpClient.dns() instanceof XDns) {
                ((XDns) okHttpClient.dns()).setTimeout(timeOut);
            }
        }
        if (build == null) {
            build = builder.build();
        }
        build.execute(callback);
    }


    /**
     * 通用的异步post请求
     *
     * @param url      请求地址
     * @param callback 回调接口
     */
    public static void postUpLoadFile(String url, HashMap<String, String> postParams, final Callback callback) {
        url = jointUrl(url);
        if (!CommonUtils.isContainsHttp(url)) {//拦截非正常请求url
            return;
        }
        PostFormBuilder builder = OkHttpUtils.post().url(url);
        if (null != postParams && postParams.size() > ZERO) {
            Set<String> keySet = postParams.keySet();
            for (String key : keySet) {
                if (StringConstants.QRCODE_FILE.equals(key)) {
                    String str = postParams.get(key);
                    builder.addFile(StringConstants.QRCODE_FILE, str, new File(str));
                } else {
                    builder.params(postParams);
                }
            }
        }
        builder.addHeader(StringConstants.FILE_TYPE, StringConstants.QRCODE_FILE);
        addHeadParams(builder);
        if (callback != null && callback instanceof CallbackBase) {
            if (CommonUtils.StringNotNull(((CallbackBase) callback).getCancelTag())) {
                CallbackBase callbackBase = (CallbackBase) callback;
                builder = builder.tag(callbackBase.getCancelTag());
            }
        }

        builder.build().execute(callback);
    }

    /**
     * 功能描述:    添加head参数
     *
     * @auther: Administrator
     * @date: 2019/3/19 0019 下午 9:55
     */
    public static void addHeadParams(OkHttpRequestBuilder builder) {
        Map<String, String> commParams = generateCommonParams();
        Set<Map.Entry<String, String>> entries = commParams.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.addHeader(entry.getKey(), StringUtils.chinaToUnicode(entry.getValue()));
        }
        builder.addHeader("Connection", "close");
    }

    /**
     * 生成通用的请求参数
     *
     * @return 参数map
     */
    public static Map<String, String> generateCommonParams() {
        HashMap map = new HashMap();
        map.putAll(ClientInfo.getHeadMap());
        return map;
    }

    public static <T> T parseFromJson(String json, Type type) {
        try {
            return !TextUtils.isEmpty(json) && type != null ? (T) new Gson().fromJson(json, type) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static <T> T parseFromJsonArray(String json,  Class<T> tClass) {
        try {
            return !TextUtils.isEmpty(json) ? (T) new Gson().fromJson(json, TypeBuilder.newInstance(ArrayList.class).addTypeParam(tClass).build()) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 自动填参，加入空判断
     *
     * @param map
     * @param key
     * @param value
     */
    public static void putStringParams(Map map, String key, String value) {
        if (map == null) {
            map = new HashMap<>();
        }
        if (CommonUtils.StringNotNull(key, value)) {
            map.put(key, value);
        }
    }

    /**
     * @param hostip 新解析后I域名
     * @param hostym 旧 域名的host
     */
    public static void setStartViewDomainUrl(String hostip, String hostym) {
        HOST_YM = hostym;
        if (getOpenDns()) {
            if (CommonUtils.StringNotNull(hostip) && !Config.ZERO_STRING.equals(HOST_IP)) {
                HOST_IP = hostip;
                HOST = getRequestUrl(HOST_YM, HOST_IP);
            } else {
                HOST_IP = "";
                HOST = HOST_YM;
            }
        } else {
            HOST_IP = "";
            HOST = HOST_YM;
        }
    }

    public static String getRequestUrl(String sYm, String sIp) {
        String httpText = Config.getHttpHost(sYm);
        return sYm.replace(httpText, sIp);
    }

    public static boolean notEmtryHostIp() {
        if (TextUtils.isEmpty(HOST_IP) && "0".equals(HOST_IP)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 拼接host
     *
     * @param url
     * @return
     */
    public static String jointUrl(String url) {
        if (!CommonUtils.StringNotNull(url)) {
            return "";
        }
        if (!CommonUtils.isContainsHttp(url)) {
            url = BaseNetUtil.HOST + url;
        }
//        if (url.contains("/s3/")) {//包含/s3/资源服务器时，去掉域名前缀app.
//            url = url.replace("app.", "");
//        }
        return url;
    }

    /**
     * 下载时，使用原域名下载
     *
     * @param url
     * @return
     */
    public static String jointUpdateUrl(String url) {
        if (!CommonUtils.StringNotNull(url)) {
            return "";
        }
        if (!CommonUtils.isContainsHttp(url)) {
            url = BaseNetUtil.HOST_YM + url;
        }
        return url;
    }

    /**
     * 拼接webview访问host
     *
     * @param url
     * @return
     */
    public static String jointWebViewUrl(String url) {
        if (!CommonUtils.StringNotNull(url)) {
            return "";
        }
        if (!CommonUtils.isContainsHttp(url)) {
            url = HOST_YM + url;
        }
        return url;
    }
}

