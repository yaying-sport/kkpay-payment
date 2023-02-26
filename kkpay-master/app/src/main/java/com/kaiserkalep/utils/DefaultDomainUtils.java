package com.kaiserkalep.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.bean.ConfigBean;
import com.kaiserkalep.bean.PrivateStringData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.url.HomeUrl;
import com.kaiserkalep.utils.toast.ToastUtils;
import com.kaiserkalep.utils.wustrive.AESCipher;
import com.tencent.msdk.dns.MSDKDnsResolver;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kaiserkalep.constants.Config.EIGHT;
import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.SEVEN;
import static com.kaiserkalep.constants.Config.THREE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

/**
 * 默认域名切换
 *
 * @Auther: Jack
 * @Date: 2020/5/12 15:21
 * @Description:
 */
public class DefaultDomainUtils {

    /**
     * 读取超时时间
     */
    private int readTimeout = 7 * 1000;
    /**
     * 链接超时时间
     */
    private int connTimeout = 3 * 1000;
    /**
     * 效验接口是否可用
     */
    private int connSuccess = 3 * 1000;

    private Context context;
    private int pollTime = 0;
    private SucceedCallBackListener<List<String>> listener;
    private List<String> isErrorList = new ArrayList<>();
    private String baseDomain;

    /**
     * 获取请求的链接
     *
     * @param context
     * @param listener
     */
    public void init(Context context, SucceedCallBackListener<List<String>> listener) {
        this.context = context;
        this.listener = listener;
        // 使用 Deflater  加密
//        String deFlaterStrokeJson = DeflaterUtils.zipString(JSONUtils.getStringByAss("json/url.json", context));
//        DeflaterUtils.writeFile(deFlaterStrokeJson, "u.json");

        /**
         *初始设置默认域名
         *
         */
        baseDomain = CommonUtils.getDefDomainByEnvMode();

        if (null != isErrorList) {
            isErrorList.clear();
        }
        pollTime = 3;
        setUrl();
    }

    private void setUrl() {
        if (null == context || null == listener) {
            return;
        }
        //        使用 Inflater 解密
        LogUtils.d("默认域名_获取开始");
        String json = DeflaterUtils.unzipString(JSONUtils.getStringByAss("json/u.json", context));
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        if (CommonUtils.StringNotNull(json)) {
            try {
                list = JSONUtils.fromJson(json, new TypeToken<ArrayList<ArrayList<String>>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList<ArrayList<String>> finalList = list;
        new Thread(() -> {
            if (finalList != null && finalList.size() > 0) {
                ArrayList<String> strings = finalList.get(CommonUtils.getEnvMode());
                if (CommonUtils.ListNotNull(strings)) {
                    LogUtils.d("默认域名_请求地址_" + JSONUtils.toJson(strings));
                    for (int i = 0; i < strings.size(); i++) {
                        String s = strings.get(i);
                        if (CommonUtils.StringNotNull(s)) {
                            boolean last = i == strings.size() - 1;
                            ConfigBean configbean = getDomainUrl(s, context, false);
                            if (null != configbean && CommonUtils.ListNotNull(configbean.getHostArr())) {
                                boolean androidDnsSwitch = configbean.getAndroidDnsSwitch();
                                if (androidDnsSwitch) {
                                    String dnsId = configbean.getDnsId();
                                    String dnsKey = configbean.getDnsKey();
                                    String dnsAppkey = configbean.getDnsAppkey();
                                    if (CommonUtils.StringNotNull(dnsAppkey, dnsId, dnsKey)) {//数据不空时,使用默认初始化
                                        MSDKDnsResolver.getInstance().init(context, dnsAppkey, dnsId, dnsKey, "119.29.29.98", true, 2000);
                                    } else {//为空时，手动置关闭
                                        androidDnsSwitch = false;
                                    }
                                }
                                List<String> domainUrls = configbean.getHostArr();
                                LogUtils.d("默认域名_获取成功,地址_" + JSONUtils.toJson(domainUrls));
                                handlerDomain(androidDnsSwitch, domainUrls);
                                return;
                            } else {
                                if (last) {
                                    pollTime--;
                                    if (pollTime > 0) {
                                        setUrl();
                                    } else {
                                        LogUtils.d("默认域名_获取失败,地址--null");
                                        handlerDomain(false, new ArrayList<>());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void handlerDomain(boolean isDNS, List<String> defaultDomains) {
        List<String> domainUrl;
        if (CommonUtils.ListNotNull(defaultDomains)) {
            domainUrl = new ArrayList<>(defaultDomains);
            if (CommonUtils.StringNotNull(baseDomain)) {
                domainUrl.add(baseDomain);
            }
        } else {
            domainUrl = SPUtil.getDefaultDomainUrl();
            if (domainUrl.size() <= ZERO && CommonUtils.StringNotNull(baseDomain)) {
                domainUrl.add(baseDomain);
            }
        }

        if (isDNS) {
            initTxDns(ZERO, domainUrl);
        } else {
            SPUtil.setDefaultDomainUrl(domainUrl);
            SPUtil.setDefaultDomainDnsUrl(new ArrayList<>());
            BaseNetUtil.setOpenDns(false);
            judgePollUrl(false, domainUrl, null, ZERO);
        }
    }

    private void initTxDns(int index, List<String> listDomain) {
        if (index < THREE) {
            int finalIndex = ++index;
            StringBuilder strUrl = new StringBuilder();
            Map<String, String> map = new HashMap<>();
            for (int x = ZERO; x < listDomain.size(); x++) {
                String url = listDomain.get(x);
                String host = "";
                int httpType = Config.judgeHttp(url);
                if (httpType == ONE) {
                    host = url.substring(EIGHT, url.length());
                } else if (httpType == TWO) {
                    host = url.substring(SEVEN, url.length());
                } else {
                    host = url;
                }
                map.put(host, Config.getHttpHead(url));
                if (x == listDomain.size() - 1) {
                    strUrl.append(host);
                } else {
                    strUrl.append(host).append(StringConstants.comma);
                }
            }
            MSDKDnsResolver.getInstance().getAddrsByNameAsync(strUrl.toString(), "" + System.currentTimeMillis());
            MSDKDnsResolver.getInstance().setHttpDnsResponseObserver((s, s1, o) -> {
                try {
                    LogUtils.e("answer", "2---:" +  o);
                    Map<String, Object> objectToMap = Config.getObjectToMap(o, Object.class);
                    Object vFourIps = objectToMap.get(Config.STRING_DNS);
                    String vfourStr = new Gson().toJson(vFourIps);
                    ArrayList<String> strings = JSONUtils.fromJson(vfourStr, new TypeToken<ArrayList<String>>() {
                    });
                    List<String> listhostYm = new ArrayList<>();
                    List<String> listhostIp = new ArrayList<>();
                    if (strings.size() >= 1) {
                        for (int i = 0; i < strings.size(); i++) {
                            String[] split = strings.get(i).split(":");
                            if (split.length == 2) {
                                String ym = split[0];
                                String ip = split[1];
                                if (CommonUtils.StringNotNull(ip) && !Config.ZERO_STRING.equals(ip) && CommonUtils.StringNotNull(ym) && !Config.ZERO_STRING.equals(ym)) {
                                    String yHead = map.get(ym);//有可能无效域名，HTTPDNS直接不返回
                                    listhostYm.add(yHead + ym);
                                    listhostIp.add(ip);
                                }
                            }
                        }
                        if (listhostYm.size() <= 0 || listhostIp.size() <= 0) {
                            initTxDns(finalIndex, listDomain);
                        } else {
                            SPUtil.setDefaultDomainDnsUrl(listhostIp);
                            SPUtil.setDefaultDomainUrl(listhostYm);
                            BaseNetUtil.setOpenDns(true);
                            judgePollUrl(true, listhostYm, listhostIp, 0);
                        }
                    } else {
                        initTxDns(finalIndex, listDomain);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    initTxDns(finalIndex, listDomain);
                }
            });
        } else {
            SPUtil.setDefaultDomainDnsUrl(new ArrayList<>());
            SPUtil.setDefaultDomainUrl(listDomain);
            BaseNetUtil.setOpenDns(false);
            judgePollUrl(false, listDomain, null, 0);
        }
    }

    private void judgePollUrl(boolean openDns, List<String> listHostYm, List<String> listHostIp, int index) {
        if (null != listHostYm && listHostYm.size() > 0 && index < listHostYm.size()) {
            String url;
            String HostYm = listHostYm.get(index);
            String HostIp;

            if (openDns) {
                HostIp = listHostIp.get(index);
                url = Config.getHttpHead(HostYm) + HostIp + HomeUrl.GET_SERVICE_DATE;
            } else {
                HostIp = "";
                url = HostYm + HomeUrl.GET_SERVICE_DATE;
            }

            OkHttpClient.Builder okhttpclientbuilder = new OkHttpClient().newBuilder();
            HTTPSCerUtils.setTrustAllCertificate(okhttpclientbuilder);
            OkHttpClient okHttpClient = okhttpclientbuilder
                    .connectTimeout(connSuccess, TimeUnit.MILLISECONDS)
                    .readTimeout(connSuccess, TimeUnit.MILLISECONDS)
                    .writeTimeout(connSuccess, TimeUnit.MILLISECONDS)
                    .build();

            if (!MyApp.isDebug) {//线上包禁止代理抓包
                okhttpclientbuilder.proxy(Proxy.NO_PROXY);
            } else {
                okhttpclientbuilder.addInterceptor(new LoggerInterceptor(BaseNetUtil.OK_HTTP_LOG_TAG, true));
            }
            Request.Builder Builder = new Request.Builder().url(url);
            if (openDns) {
                String httpHost = Config.getHttpHost(HostYm);
                Builder.addHeader(Config.HOST, httpHost);
            }
            Request build = Builder.get().build();
            int num = ++index;
            okHttpClient.newCall(build).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    isErrorList.add(HostYm);
                    judgePollUrl(openDns, listHostYm, listHostIp, num);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if ((200 == response.code() || 0 == response.code())) {
                        effectSuccess(openDns, HostYm, HostIp, listHostYm);
                    } else {
                        isErrorList.add(HostYm);
                        judgePollUrl(openDns, listHostYm, listHostIp, num);
                    }
                }
            });
        } else {
            DomainErrorLogUtils.instance().setAllUrlError(true);
            if (CommonUtils.StringNotNull(baseDomain)) {
                effectSuccess(openDns, baseDomain, "", listHostYm);
            } else {
                MyApp.toast(MyApp.getApplication().getLanguageString(context, R.string.Share_Error));
            }
        }
    }

    /**
     * @param hostIp    新解析后I域名（解析失败，使用默认域名，BaseNetUtil中再次解析）
     * @param hostYm    旧 域名的host
     * @param urlYmList 效验url集合
     */
    private void effectSuccess(boolean openDns, String hostYm, String hostIp, List<String> urlYmList) {
        BaseNetUtil.setStartViewDomainUrl(hostIp, hostYm);
        listener.succeedCallBack(urlYmList);
        if (null != isErrorList && isErrorList.size() > ZERO) {
            DomainErrorLogUtils.instance().updataLog(true, urlYmList, isErrorList, openDns);
        }
    }

    /**
     * 从链接获取默认域名
     *
     * @param u
     * @param context
     * @param needToast 获取域名最后一个请求失败提示异常
     * @return
     */
    private ConfigBean getDomainUrl(String u, Context context, boolean needToast) {
        ConfigBean configBean = null;
        File file = null;
        if (CommonUtils.StringNotNull(u)) {
            file = new File(context.getCacheDir().getPath() + File.separator + "1");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(u);
                TrustManager[] trustManagers = new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            }

                            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            }

                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                        }
                };
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, trustManagers, null);

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(ctx.getSocketFactory());

                conn.setConnectTimeout(connTimeout);
                conn.setReadTimeout(readTimeout);
                setRequestProperty(conn);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                    fos = new FileOutputStream(file);//会自动创建文件
                    int len = 0;
                    byte[] buf = new byte[8 * 1024 * 1024];
                    while ((len = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, len);//写入流中
                    }
                } else {
                    if (needToast) {
                        ToastUtils.showLong(context.getString(R.string.toast_common_system_net_error));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (needToast) {
                    ToastUtils.showLong(context.getString(R.string.toast_common_system_net_error));
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (file != null && file.exists()) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    sb.append(tempStr);
                }
                reader.close();
                String s = sb.toString();
                if (CommonUtils.StringNotNull(s)) {
                    PrivateStringData.getK(context);//获取加密域名的密钥
                    String decrypt = AESCipher.getDecrypt(s, false);
                    LogUtils.e("answer", "1---:" +  decrypt);
                    if (CommonUtils.StringNotNull(decrypt)) {
                        configBean = JSONUtils.fromJson(decrypt, ConfigBean.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (file != null && file.exists()) {
            file.delete();
        }
//        PrivateStringData.getK(context, "json/p1.json");//解密接口密钥

        return configBean;
    }

    /**
     * 设置随机ua头模拟浏览器访问,防止码云/github拦截请求
     *
     * @param conn
     */
    private void setRequestProperty(HttpsURLConnection conn) {
        if (conn != null) {
            String[] uaList = new String[]{"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
                    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
                    "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
                    "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
                    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
                    "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
                    "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
                    "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
                    "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
                    "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
                    "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52"};
//                conn.setRequestProperty("User-agent","Mozilla/5.0 (Linux; Android 4.0.3; U9200 Build/HuaweiU9200)");
//                conn.setRequestProperty("User-agent",String.format("%s/%s (Linux; Android %s; %s Build/%s)", UIUtils.getString(R.string.app_name), SPUtil.getStringValue(SPUtil.VERSION_CODE), Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
//                conn.setRequestProperty("User-agent",String.format("Mozilla/5.0 (Linux; Android %s; %s Build/%s)", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
            conn.setRequestProperty("User-agent", uaList[new Random().nextInt(16)]);
        }
    }
}
