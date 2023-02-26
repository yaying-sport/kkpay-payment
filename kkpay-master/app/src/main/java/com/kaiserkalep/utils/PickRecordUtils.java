package com.kaiserkalep.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.interfaces.SucceedCallBackListener;
import com.kaiserkalep.widgets.pickerview.pickerview.builder.TimePickerBuilder;
import com.kaiserkalep.widgets.pickerview.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间选择
 *
 * @Auther: Jack
 * @Date: 2020/12/21 22:19
 * @Description:
 */
public class PickRecordUtils {

    private ActivityIntentInterface anInterface;
    private TimePickerView pvCustomOptions;
    private SucceedCallBackListener<String[]> listener;

    public PickRecordUtils(ActivityIntentInterface anInterface, SucceedCallBackListener<String[]> listener) {
        this.anInterface = anInterface;
        this.listener = listener;
    }

    @SuppressLint("WrongConstant")
    public static void main(String[] args) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");

        //2、获取本月第一天凌晨开始时间00:00:00和最后一天的最后一刻时间23:59:59
        Calendar cal2 = Calendar.getInstance(timeZone);
        //当前日期月份
//        cal2.add(Calendar.MONTH, 0);
        //创建当前时间
//        Date date2 = new Date();
        //设置时间格式为yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long l = System.currentTimeMillis();
        //设置当前时间,
        cal2.setTimeInMillis(l);
        //打印当前时间
        System.out.println("当前时间：" + sdf2.format(l));


        //获取到本月起始日
        int actualMinimum2 = cal2.getActualMinimum(Calendar.DAY_OF_MONTH);
        //获取到本月结束日
        int actualMaximum2 = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal2.add(Calendar.MONTH, 0);

        //设置本月起始日的年月日时分秒格式
        cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONDAY), actualMinimum2, 0, 0, 0);
        //打印本月起始日的年月日时分秒格式
        System.out.println("这个月的第一天是： " + sdf2.format(cal2.getTime()));
        //设置本月结束日的年月日时分秒格式
        cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONDAY), actualMaximum2, 23, 59, 59);
        //打印本月结束日的年月日时分秒格式
        System.out.println("这个月的最后一天是： " + sdf2.format(cal2.getTime()));

    }

    @SuppressLint("WrongConstant")
    public void showPick(ViewGroup decorView, long select) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        Calendar end = Calendar.getInstance(timeZone);
        long now = System.currentTimeMillis();
        end.setTimeInMillis(now);
        Calendar sele = end;
        if (select > 0) {
            sele = Calendar.getInstance(timeZone);
            sele.setTimeInMillis(select);
        }
        Calendar start = Calendar.getInstance(timeZone);
        start.setTimeInMillis(now);
        start.add(Calendar.MONTH, -5);//近6个月,包含当前月

        pvCustomOptions = new TimePickerBuilder(anInterface.getContext()
                , (date, v) -> selectData(date))
                .setDate(sele)
                .setDecorView(decorView)
                .setIsAnim(false)
                .setRangDate(start, end)
                .setLayoutRes(R.layout.select_record_pickerview, this::setViewEvent)
                .setType(new boolean[]{true, true, false, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(MyApp.getMyColor(R.color.comm_line))
                .setBgColor(MyApp.getMyColor(R.color.view_title_bg))
                .setOutSideColor(MyApp.getMyColor(R.color.select_record_out))
                .setTextColorCenter(MyApp.getMyColor(R.color.select_record_center))
                .setOutSideCancelable(true)
                .isDialog(false)
                .build();
        pvCustomOptions.show();
    }

    @SuppressLint("WrongConstant")
    private void selectData(Date date) {
        if (date != null) {
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            //2、获取本月第一天凌晨开始时间00:00:00和最后一天的最后一刻时间23:59:59
            Calendar cal2 = Calendar.getInstance(timeZone);
            //设置当前选中的月份时间
            cal2.setTimeInMillis(date.getTime());
            //获取到本月起始日
            int actualMinimum2 = cal2.getActualMinimum(Calendar.DAY_OF_MONTH);
            //获取到本月结束日
            int actualMaximum2 = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal2.add(Calendar.MONTH, 0);
            //设置本月起始日的年月日时分秒格式
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONDAY), actualMinimum2, 0, 0, 0);
            //打印本月起始日的年月日时分秒格式
//            System.out.println("这个月的第一天是： " + sdf2.format(cal2.getTime()));
            String current = String.valueOf(cal2.getTime().getTime());
            String start = DateUtils.covertDateToString1(cal2.getTime().getTime()) + " 00:00:00";
            //设置本月结束日的年月日时分秒格式
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONDAY), actualMaximum2, 23, 59, 59);
            //打印本月结束日的年月日时分秒格式
//            System.out.println("这个月的最后一天是： " + sdf2.format(cal2.getTime()));
            String end = DateUtils.covertDateToString1(cal2.getTime().getTime()) + " 23:59:59";
            if (listener != null) {
                listener.succeedCallBack(new String[]{start, end, current});
            }
        }
    }

    private void setViewEvent(View v) {
        v.findViewById(R.id.ll_pick_content).setOnClickListener(v1 -> {
        });
        v.findViewById(R.id.tv_cancel).setOnClickListener(v1 -> {
            pvCustomOptions.dismiss();
            if (listener != null) {
                listener.succeedCallBack(null);
            }
        });
        v.findViewById(R.id.tv_finish).setOnClickListener(v12 -> {
            pvCustomOptions.returnData();
        });
    }

    public void dismiss() {
        if (pvCustomOptions != null) {
            pvCustomOptions.dismiss();
        }
    }


}
