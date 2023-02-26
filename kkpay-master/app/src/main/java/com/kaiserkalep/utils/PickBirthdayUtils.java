package com.kaiserkalep.utils;

import android.content.Context;
import android.view.View;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZNetCallBack;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.net.impl.UserImpl;
import com.kaiserkalep.widgets.pickerview.pickerview.builder.TimePickerBuilder;
import com.kaiserkalep.widgets.pickerview.pickerview.view.TimePickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.kaiserkalep.base.BaseApp.toast;

/**
 * 选择出生日期
 *
 * @Auther: Jack
 * @Date: 2020/12/21 22:19
 * @Description:
 */
public class PickBirthdayUtils {

    private ActivityIntentInterface anInterface;
    private TimePickerView pvCustomOptions;
    private SucceedCallBackListener<String> listener;

    public PickBirthdayUtils(ActivityIntentInterface anInterface, SucceedCallBackListener<String> listener) {
        this.anInterface = anInterface;
        this.listener = listener;
    }

    public void showPick(Context context) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar startDate = Calendar.getInstance(timeZone);
        startDate.set(1900, 0, 1);
        Calendar selectedDate = Calendar.getInstance(timeZone);//系统当前时间
        selectedDate.set(1990, 0, 1);
        Calendar endDate = Calendar.getInstance(timeZone);
        int i = endDate.get(Calendar.YEAR);
        if (i < 2020) {
            endDate.set(2020, 11, 31);
        }
        pvCustomOptions = new TimePickerBuilder(anInterface.getContext()
                , (date, v) -> selectData(context,date))
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.select_birthday_pickerview, this::setViewEvent)
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(MyApp.getMyColor(R.color.msg_edit_bg))
                .setBgColor(MyApp.getMyColor(R.color.select_birthday_bg))
                .setOutSideColor(MyApp.getMyColor(R.color.select_record_out))
                .setTextColorCenter(MyApp.getMyColor(R.color.select_record_center))
                .setOutSideCancelable(false)
                .isDialog(true)
                .build();
        pvCustomOptions.show();
    }

    private void selectData(Context context,Date date) {
        if (date != null) {
            String s = DateUtils.pickBirthday(date.getTime());
            if (CommonUtils.StringNotNull(s)) {
                update(context,s);
            }
        }
    }

    private void update(Context context,String birthday) {

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
