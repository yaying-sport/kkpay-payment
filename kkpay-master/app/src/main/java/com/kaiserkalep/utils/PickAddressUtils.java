package com.kaiserkalep.utils;

import android.view.View;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.AddressData;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.widgets.pickerview.pickerview.builder.OptionsPickerBuilder;
import com.kaiserkalep.widgets.pickerview.pickerview.view.OptionsPickerView;

import java.util.ArrayList;

/**
 * 选择省市区
 *
 * @Auther: Jack
 * @Date: 2021/2/17 15:03
 * @Description:
 */
public class PickAddressUtils {


    private ArrayList<AddressData> options1Items;
    private ArrayList<ArrayList<AddressData.SubBeanX>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<AddressData.SubBeanX.SubBean>>> options3Items = new ArrayList<>();
    private OptionsPickerView pvCustomOptions;
    private ActivityIntentInterface anInterface;
    private SucceedCallBackListener<String[]> listener;
    /**
     * 回显索引
     */
    private int o1 = 0, o2 = 0, o3 = 0;

    public PickAddressUtils(ActivityIntentInterface anInterface, String t1, String t2, String t3,
                            SucceedCallBackListener<String[]> listener) {
        this.anInterface = anInterface;
        this.listener = listener;
        options1Items = AddressUtils.getInstance().getAddressList();
        if (CommonUtils.ListNotNull(options1Items)) {
            for (int i = 0; i < options1Items.size(); i++) {//遍历省份
                ArrayList<AddressData.SubBeanX> cityList = new ArrayList<>();//该省的城市列表（第二级）
                ArrayList<ArrayList<AddressData.SubBeanX.SubBean>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
                AddressData addressData = options1Items.get(i);
                if (CommonUtils.StringNotNull(t1) && addressData.getPickerViewText().equals(t1)) {
                    o1 = i;
                }
                for (int c = 0; c < addressData.getSub().size(); c++) {//遍历该省份的所有城市
                    AddressData.SubBeanX subBeanX = addressData.getSub().get(c);
                    if (CommonUtils.StringNotNull(t2) && subBeanX.getPickerViewText().equals(t2)) {
                        o2 = c;
                    }
                    cityList.add(subBeanX);//添加城市
                    ArrayList<AddressData.SubBeanX.SubBean> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                    city_AreaList.addAll(addressData.getSub().get(c).getSub());
                    province_AreaList.add(city_AreaList);//添加该省所有地区数据
                    if (CommonUtils.StringNotNull(t3)) {
                        for (int k = 0; k < city_AreaList.size(); k++) {
                            AddressData.SubBeanX.SubBean subBean = city_AreaList.get(k);
                            if (subBean.getPickerViewText().equals(t3)) {
                                o3 = k;
                            }
                        }
                    }
                }
                //添加城市数据
                options2Items.add(cityList);
                //添加地区数据
                options3Items.add(province_AreaList);
            }
        }
    }


    public void showPick() {
        pvCustomOptions = new OptionsPickerBuilder(anInterface.getContext(),
                (options1, options2, options3, v) -> selectData(options1, options2, options3)).setSelectOptions(o1, o2, o3)
                .setLayoutRes(R.layout.select_address_pickerview, this::setViewEvent)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(MyApp.getMyColor(R.color.msg_edit_bg))
                .setBgColor(MyApp.getMyColor(R.color.select_birthday_bg))
                .setOutSideColor(MyApp.getMyColor(R.color.select_record_out))
                .setTextColorCenter(MyApp.getMyColor(R.color.select_record_center))
                .setOutSideCancelable(false)
                .isRestoreItem(true)
                .isDialog(true).build();
        pvCustomOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvCustomOptions.show();
    }

    private void selectData(int options1, int options2, int options3) {
        if (CommonUtils.ListNotNull(options1Items) &&
                CommonUtils.ListNotNull(options2Items) &&
                CommonUtils.ListNotNull(options3Items)) {
            o1 = options1;
            o2 = options2;
            o3 = options3;
            String t1 = options1Items.get(options1).getPickerViewText();
            String t2 = options2Items.get(options1).get(options2).getPickerViewText();
            String t3 = options3Items.get(options1).get(options2).get(options3).getPickerViewText();
            if (listener != null) {
                listener.succeedCallBack(new String[]{t1, t2, t3});
            }
        }
        if (pvCustomOptions != null) {
            pvCustomOptions.dismiss();
        }
    }


    private void setViewEvent(View v) {
        v.findViewById(R.id.ll_pick_content).setOnClickListener(v1 -> {
        });
        v.findViewById(R.id.tv_cancel).setOnClickListener(v1 -> {
            pvCustomOptions.dismiss();
        });
        v.findViewById(R.id.tv_finish).setOnClickListener(v12 -> {
            pvCustomOptions.returnData();
        });
    }


}

