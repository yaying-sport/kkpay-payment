package com.kaiserkalep.bean;


import android.content.Context;

import com.kaiserkalep.utils.BASE64Utils;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.DeflaterUtils;
import com.kaiserkalep.utils.JSONUtils;
import com.kaiserkalep.utils.LogUtils;

import java.util.ArrayList;

/**
 * 加密解密密钥生成
 */
public class PrivateStringData {


    /**
     * 最终密钥
     */
    public static String k = "";

    /**
     * 解密获取密钥
     *
     * @param context
     * @return
     */
    public static void getK(Context context) {
        getK(context, "json/p.json");
    }

    /**
     * 解密获取密钥
     *
     * @param context
     * @return
     */
    public static void getK(Context context, String keyName) {
        long time = System.currentTimeMillis();
        LogUtils.d("密__开始");
        String json = JSONUtils.getStringByAss(keyName, context);
        String decode = BASE64Utils.decode(json);
        String s = DeflaterUtils.unzipString(decode);
        Ks ks = JSONUtils.fromJson(s, Ks.class);
        if (ks != null && CommonUtils.ListNotNull(ks.ks)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ks.ks.size(); i++) {
                sb.append(getS(ks.ks.get(i), i + 1));
            }
            k = sb.toString();
            long l = System.currentTimeMillis() - time;
            LogUtils.d("密__结束__" + k + "__耗时__" + l);
        }
    }

    /**
     * 分段解密
     *
     * @param ss
     * @param count
     * @return
     */
    private static String getS(String ss, int count) {
        for (int i = 0; i < count; i++) {
            ss = BASE64Utils.decode(ss);
        }
        return ss;
    }

    static class Ks {
        private ArrayList<String> ks;

        public ArrayList<String> getKs() {
            return ks;
        }

        public void setKs(ArrayList<String> ks) {
            this.ks = ks;
        }

        public Ks(ArrayList<String> ks) {
            this.ks = ks;
        }

        @Override
        public String toString() {
            return "Ks{" +
                    "ks=" + ks +
                    '}';
        }
    }

}
