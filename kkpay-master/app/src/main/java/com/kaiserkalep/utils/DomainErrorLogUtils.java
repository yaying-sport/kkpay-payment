package com.kaiserkalep.utils;

import com.google.gson.JsonObject;
import com.kaiserkalep.constants.StringConstants;
import com.umeng.analytics.MobclickAgent;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.widgets.bigimageViewpage.tool.utility.ui.PhoneUtil;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DomainErrorLogUtils {

    private static DomainErrorLogUtils domainerrorlogutils;

    private boolean allUrlError = false;

    public boolean getAllUrlError() {
        return allUrlError;
    }

    public void setAllUrlError(boolean allurlerror) {
        allUrlError = allurlerror;
    }

    private DomainErrorLogUtils() {
    }

    /**
     * @return 返回工具类实例
     */
    public static DomainErrorLogUtils instance() {
        if (null == domainerrorlogutils) {
            synchronized (DomainErrorLogUtils.class) {
                if (null == domainerrorlogutils) {
                    domainerrorlogutils = new DomainErrorLogUtils();
                }
            }
        }
        return domainerrorlogutils;
    }

    /**
     * @param switchOn  是否是启动App时，true时，false运行中
     * @param list      域名集合
     * @param errorList 异常域名集合
     */
    public void updataLog(boolean switchOn, List<String> list, List<String> errorList, boolean isHttpDns) {
        if (JudgeDoubleUtils.updataDomianLogClick()) {
            return;
        }
        String s1 = JSONUtils.toJson(list);
        String s2 = JSONUtils.toJson(errorList);
        String userAccount = SPUtil.getUserPhone();
        String ipAddress = PhoneUtil.getIpAddress(MyApp.getContext());
        String deviceModel = PhoneUtil.getDeviceModel();
        String httpDns = isHttpDns ? "1" : "0";
        //
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("allDomains", s1);
        jsonObject.addProperty("errorDomains", switchOn ? s2 : s1);
        jsonObject.addProperty("userName", userAccount);
        jsonObject.addProperty("networkIP", ipAddress);
        jsonObject.addProperty("unitType", deviceModel);
        jsonObject.addProperty("HttpDns", httpDns);

        try {

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(BaseNetUtil.TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(BaseNetUtil.TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(BaseNetUtil.TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
                    .build();
            OkHttpClient.Builder builder = okHttpClient.newBuilder();
            if (!MyApp.isDebug) {//线上包禁止代理抓包
                builder.proxy(Proxy.NO_PROXY);
            } else {
                builder.addInterceptor(new LoggerInterceptor(BaseNetUtil.OK_HTTP_LOG_TAG, true));
            }
            FormBody.Builder formbody = new FormBody.Builder();
            formbody.add(StringConstants.DATA, jsonObject.toString());
            RequestBody requestbody = formbody.build();
            Request build = new Request.Builder()
                    .url("http://tzbwapi.tw00.cc/yy/log")
                    .post(requestbody)
                    .build();
            okHttpClient.newCall(build).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> fristApp = new HashMap<String, Object>();
        if (switchOn) {//启动时，上传异常域名到友盟。
            fristApp.put("data", userAccount + ";" + ipAddress + ";" + deviceModel + ";" + s2 + ";httpDns" + httpDns);
            MobclickAgent.onEventObject(MyApp.getContext(), "StartAppReporting", fristApp);
        } else {//运行时，所有域名无法访问，上传至友盟。
            fristApp.put("data", userAccount + ";" + ipAddress + ";" + deviceModel + ";" + s1 + ";httpDns" + httpDns);
            MobclickAgent.onEventObject(MyApp.getContext(), "UseAppAllDomainError", fristApp);
        }
    }
}
