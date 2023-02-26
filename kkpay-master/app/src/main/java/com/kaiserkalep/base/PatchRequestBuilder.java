package com.kaiserkalep.base;


import com.kaiserkalep.utils.JSONUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.FormBody;

/**
 * @Auther: Jack
 * @Date: 2020/3/13 10:23
 * @Description:
 */
public class PatchRequestBuilder extends OtherRequestBuilder {

    public PatchRequestBuilder() {
        super(OkHttpUtils.METHOD.PATCH);
    }

    public OtherRequestBuilder myRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        addParams(builder);
        requestBody(builder.build());
        requestBody(JSONUtils.toJson(params));
        return this;
    }

    public PatchRequestBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public PatchRequestBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    private void addParams(FormBody.Builder builder) {
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
    }
}