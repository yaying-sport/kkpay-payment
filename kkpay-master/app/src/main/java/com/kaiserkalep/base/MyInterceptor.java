package com.kaiserkalep.base;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kaiserkalep.bean.ResultData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DomainErrorLogUtils;
import com.kaiserkalep.utils.JSONUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.NetWorkUtils;
import com.kaiserkalep.utils.NumberUtils;
import com.kaiserkalep.utils.SPUtil;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;

import static com.kaiserkalep.net.url.HomeUrl.WALLET_UPLOADFILE;

/**
 * 全局token过期自动刷新/重新绑定/缓存
 *
 * @Auther: Jack
 * @Date: 2019/12/31 11:21
 * @Description:
 */
public class MyInterceptor implements Interceptor {

    private static final String TAG = "MyInterceptor";
    private static final int TIME_OUT_MILLIS = 3 * 1000;//拦截超时时间,毫秒
    private int reConnectCount = 0;//刷新token请求次数  3次
    private int reConnectCount2 = 0;//未绑定设置拦截绑定请求次数  3次


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().url().toString();
        List<String> hostIp = SPUtil.getDefaultDomainDnsUrl();
        List<String> hostYm = SPUtil.getDefaultDomainUrl();

        Response response = getResponse(chain, url, hostIp, hostYm, false);

        if (response == null) {
            response = new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(NumberUtils.stringToInt(StringConstants.REQUEST_OTHER_ERROR))
                    .message("connect fail")
                    .body(Util.EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }
        return response;
    }

    /**
     * 拦截请求异常更换host重新请求
     *
     * @param chain
     * @param url
     * @return
     */
    private Response getResponse(Chain chain, String url, List<String> hostip, List<String> hostym, boolean isInter) {
        Request request = chain.request();
        RequestBody body = request.body();
        Headers headers = request.headers();
        Response response = null;

        String newUrl = null;
        try {
            if (!isInter) {
                if (BaseNetUtil.getOpenDns() && BaseNetUtil.notEmtryHostIp()) {
                    String httpHost = Config.getHttpHost(BaseNetUtil.HOST_YM);
                    request = request.newBuilder().addHeader(Config.HOST, httpHost).build();
                }
                response = chain.proceed(request);
            }
            if (isInter || requestFail(response)) {
                if (response != null && response.code() >= 500) {//超时切换延时1-3秒,减小并发
                    int i = (new Random().nextInt(3) + 1) * 1000;
                    LogUtils.e("请求异常，切换域名随机延时开始__" + i);
                    Thread.sleep(i);
                }
                try {
                    newUrl = handlerUrl(url, hostip, hostym);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != newUrl && CommonUtils.StringNotNull(newUrl)) {
                    //使用新的域名，创建新的请求
                    Request.Builder builder = chain.request().newBuilder();
                    chain.request().newBuilder();
                    if (BaseNetUtil.getOpenDns() && BaseNetUtil.notEmtryHostIp()) {
                        String httpHost = Config.getHttpHost(BaseNetUtil.HOST_YM);
                        builder.addHeader(Config.HOST, httpHost);
                    }
                    Request.Builder aTrue = builder.header(StringConstants.IS_INTERCEPTOR, "true")
                            .headers(headers)
                            .url(newUrl);

                    if (body != null) {
                        aTrue.post(body);
                    }
                    //重新请求
                    Response proceed = chain.withConnectTimeout(TIME_OUT_MILLIS, BaseNetUtil.DEF_TIME_UNIT)
                            .withReadTimeout(TIME_OUT_MILLIS, BaseNetUtil.DEF_TIME_UNIT)
                            .withWriteTimeout(TIME_OUT_MILLIS, BaseNetUtil.DEF_TIME_UNIT).proceed(aTrue.build());
                    if (requestFail(proceed)) {
                        return getResponse(chain, newUrl, hostip, hostym, true);
                    } else {
                        response = proceed;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("请求异常，Exception__接口__" + url + "__异常__" + e.toString());
            if (e.toString().contains("Canceled")) {//主动取消
                return new Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("Canceled")
                        .body(Util.EMPTY_RESPONSE)
                        .sentRequestAtMillis(-1L)
                        .receivedResponseAtMillis(System.currentTimeMillis())
                        .build();
            }
            return getResponse(chain, CommonUtils.StringNotNull(newUrl) ? newUrl : url, hostip, hostym, true);
        }

        if (response == null) {
            response = new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(404)
                    .message("connect fail")
                    .body(Util.EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();

            DomainErrorLogUtils.instance().setAllUrlError(true);
            List<String> defaultDomainUrl = SPUtil.getDefaultDomainUrl();
            DomainErrorLogUtils.instance().updataLog(false, defaultDomainUrl, null, BaseNetUtil.getOpenDns());
        }
        return response;
    }

    /**
     * 请求是否错误
     *
     * @param response
     * @return
     */
    public static boolean requestFail(Response response) {
        if (!NetWorkUtils.isNetworkConnected()) {
            return false;
        }
        if (response == null) {
            return true;
        }
        return response.code() != 200;
    }

    /**
     * 拼接host
     *
     * @param url         发生错误完整url
     * @param domainUrlip IP域名列
     * @param domainUrlym 域名列
     * @return
     */
    public static String handlerUrl(String url, List<String> domainUrlip, List<String> domainUrlym) {
        String[] baseUrl = getBaseUrl(url);
        if (baseUrl != null && baseUrl.length > 1) {
            String oldUrl = handleOldUrl(baseUrl[1]);
            String oldnowHost = baseUrl[0];
            String oldDomainym = BaseNetUtil.HOST_YM;
            //移除异常的域名,只请求一轮
            if (CommonUtils.ListNotNull(domainUrlym) && CommonUtils.StringNotNull(oldDomainym)) {
                int i = domainUrlym.indexOf(oldDomainym);
                if (i <= 0 || i >= domainUrlym.size() - 1) {
                    i = 0;
                }
                if (domainUrlym.size() > 1) {
                    domainUrlym.remove(i);
                }
                if (null != domainUrlip && domainUrlip.size() > 1) {
                    domainUrlip.remove(i);
                }

                String newHostYm = domainUrlym.get(0);
                String newHostIp;
                if (null != domainUrlip && domainUrlip.size() >= 1) {
                    newHostIp = domainUrlip.get(0);
                } else {
                    newHostIp = "";
                }
                LogUtils.e("请求异常，切换域名__error__" + url + "__newDomain__" + newHostYm + "--" + newHostIp);
                if (CommonUtils.ListNotNull(domainUrlym) && domainUrlym.size() == 1) {//当只剩下一个,清空所有集合
                    domainUrlym.clear();
                }
                if (CommonUtils.ListNotNull(domainUrlip) && domainUrlip.size() == 1) {//当只剩下一个,清空所有集合
                    domainUrlip.clear();
                }

                BaseNetUtil.setStartViewDomainUrl(newHostIp, newHostYm);

                return BaseNetUtil.HOST + oldUrl;
            }
        }
        return null;
    }

    /**
     * 当前异常域名,截取异常
     * http://api.rerevod.co/v1/login
     * http://api.rerevod.com/v1/login
     *
     * @param oldUrl
     * @return
     */
    private static String handleOldUrl(String oldUrl) {
        int i = oldUrl.indexOf("/");
        if (i != 0) {
            oldUrl = oldUrl.substring(i);
        }
        return oldUrl;
    }

    /**
     * 获取当前异常接口域名
     *
     * @param url
     * @return
     */
    //http://videotest2.x6s88.com/api +  /v1/login
    private static String[] getBaseUrl(String url) {
        String oldDomain = BaseNetUtil.HOST;
        if (url.contains(oldDomain)) {
            return getBaseUrl(url, oldDomain);
        }
        return null;
    }

    /**
     * 获取当前异常接口域名
     *
     * @param url
     * @param oldDomain
     * @return
     */
    private static String[] getBaseUrl(String url, String oldDomain) {
        if (CommonUtils.StringNotNull(url)) {
            String[] split = url.split(oldDomain);
            if (split.length > 1) {
                String s2 = split[1];
                if (CommonUtils.StringNotNull(s2)) {
                    split[0] = oldDomain;
                    return split;
                }
            }
        }
        return null;
    }
}
