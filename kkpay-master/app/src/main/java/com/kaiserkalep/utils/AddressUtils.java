package com.kaiserkalep.utils;

import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.bean.AddressData;

import java.util.ArrayList;

/**
 * 获取省市区地址
 *
 * @Auther: Jack
 * @Date: 2021/2/17 14:19
 * @Description:
 */
public class AddressUtils {

    private static ArrayList<AddressData> addressList = new ArrayList<>();

    private AddressUtils() {
        initAddress();
    }

    private void initAddress() {
        LogUtils.d("解压地址开始__");
        String json = DeflaterUtils.unzipString(JSONUtils.getStringByAss("json/a.json", MyApp.getContext()));
        if (CommonUtils.StringNotNull(json)) {
            try {
                addressList = JSONUtils.fromJson(json, new TypeToken<ArrayList<AddressData>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogUtils.d("解压地址结束__");
    }

    public static AddressUtils getInstance() {
        return AddressUtilsHolder.INSTANCE;
    }
    private static class AddressUtilsHolder {
        private static final AddressUtils INSTANCE = new AddressUtils();
    }
    ArrayList<AddressData> getAddressList() {
        return addressList;
    }
}
