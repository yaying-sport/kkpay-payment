package com.kaiserkalep.base;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.bean.ResultData;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import ikidou.reflect.TypeBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:24
 * @Description:
 */
public abstract class ZZNetCallBack<T> extends CallbackBase<T> {

    /**
     * {@link ResultData}
     */
    public static String[] Key = {"data", "rows"};
    Class tType;
    boolean isLogResult = MyApp.isDebug;
    boolean isNeedDecrypt = true;//是否需要解密

    public ZZNetCallBack(IBaseView ui, Class tType) {
        super(ui);
        this.tType = tType;
    }

    protected boolean isLogResult() {
        return isLogResult;
    }

    protected boolean isNeedDecrypt() {
        return isNeedDecrypt;
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     * @param id
     */

    @Override
    public ResultData<T> parseNetworkResponse(Response response, int id) throws Exception {
        if (response != null && response.body() != null) {
            Request request = response.request();

            HttpUrl httpUrl = request.url();
            String url = "";
            if (response.request().url() != null) {
                url = httpUrl.url().toString();
            }

            String resultString = response.body().string();

            if (isLogResult()) {
                LogUtils.d(url + "___ " + resultString);
            }
            ResultData<T> resultData = getResultData(url, resultString);
            animationTime();
            return resultData;
        }

        return null;

    }

    /**
     * @param body 返回参数
     * @return
     */
    private ResultData<T> getResultData(String url, String body) {
        JsonObject asJsonObject = null;
        /**
         * 是否开启加密
         */
        if (BaseNetUtil.TURNOFF_ENCRYPTION) {
            try {
                String code = "";
                String msg = "";
                String newbody = "";
                JSONObject object = new JSONObject(body);
                if (object.has(StringConstants.CODE)) {
                    code = object.optString(StringConstants.CODE);
                }
                if (object.has(StringConstants.MSG)) {
                    msg = object.optString(StringConstants.MSG);
                }
                if (StringConstants.REQUEST_SUCCEED.equals(code) && object.has(StringConstants.DATA)) {
                    String data = object.optString(StringConstants.DATA);
                    if (CommonUtils.StringNotNull(data)) {
                        String responseData = EncryptRsa.decryptByPubKey(data);//解密
                        if (CommonUtils.StringNotNull(responseData)) {
                            newbody = StringEscapeUtils.unescapeJava(responseData);
                        }
                    }
                }
                LogUtils.e("httprequest  " + url + "\n    result= " + newbody + "\n");
                if (CommonUtils.StringNotNull(newbody)) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(StringConstants.CODE, code);
                    jsonObject.addProperty(StringConstants.MSG, msg);
                    jsonObject.add(StringConstants.DATA, new JsonParser().parse(newbody));
                    asJsonObject = new JsonParser().parse(jsonObject.toString()).getAsJsonObject();
                } else {
                    object.put(StringConstants.DATA, null);
                    asJsonObject = new JsonParser().parse(object.toString()).getAsJsonObject();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return getNullData("9999", "");
            }
        } else {
            asJsonObject = new JsonParser().parse(body).getAsJsonObject();
        }
        ResultData<T> resultData = BaseNetUtil.parseFromJson(asJsonObject.toString(), new TypeToken<ResultData<T>>() {
        }.getType());
        JsonArray jsonArray;
        String dataKey = getRealKey(asJsonObject);
        if (asJsonObject.has(dataKey)) {
            if (asJsonObject.get(dataKey).isJsonArray()) {
                jsonArray = asJsonObject.getAsJsonArray(dataKey);
                asJsonObject.remove(dataKey);
                if (jsonArray != null && resultData != null) {
                    String resultDataString = jsonArray.toString();
                    resultData.setData(BaseNetUtil.parseFromJson(resultDataString,
                            TypeBuilder.newInstance(ArrayList.class).addTypeParam(tType).build()));
                }
            } else {
                JsonElement result = asJsonObject.remove(dataKey);
                if (result != null && resultData != null) {
                    String resultDataString = result.toString();
                    resultData.setData(BaseNetUtil.parseFromJson(resultDataString, TypeBuilder.newInstance(tType).build()));
                }
            }
        }
        return resultData;
    }

    private ResultData<T> getNullData(String code, String msg) {
        ResultData<T> response = new ResultData<T>();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }

    private String getRealKey(JsonObject j) {
        String dataKey = "data";
        if (j != null && Key != null && Key.length > 0) {
            for (String s : Key) {
                if (CommonUtils.StringNotNull(s)) {
                    if (j.has(s)) {
                        dataKey = s;
                        break;
                    }
                }
            }
        }
        return dataKey;
    }
}
