package com.kaiserkalep.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.kaiserkalep.BuildConfig;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.BaseNetUtil;
import com.kaiserkalep.base.PageBaseFragment;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.bean.ClientInfo;
import com.kaiserkalep.bean.NoticeDialogData;
import com.kaiserkalep.bean.UserData;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.ui.activity.StartActivity;
import com.kaiserkalep.utils.toast.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:02
 * @Description:
 */
public class CommonUtils {

//    /**
//     * 验证手机格式
//     */
//    public static boolean isMobileNO(String mobiles) {
//        /*
//         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
//         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
//         * 总结起来就是第一位必定为1，第二位必定为3或5或7或8，其他位置的可以为0-9
//         */
//        String telRegex = "[1][0-9]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobiles))
//            return false;
//        else
//            return mobiles.matches(telRegex);
//    }

//    /**
//     * 验证手机号码
//     *
//     * @param mobiles
//     * @return [0-9]{5,9}
//     */
//    public static boolean isMobileNO(String mobiles) {
//        boolean flag = false;
//        try {
////            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//            Pattern p = Pattern.compile("/^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$/");
//            Matcher m = p.matcher(mobiles);
//            flag = m.matches();
//        } catch (Exception e) {
//            flag = false;
//        }
//        return flag;
//    }


    /**
     * <br>　　　　　2019年1月16日已知
     * 中国电信号段
     * 133,149,153,173,174,177,180,181,189,199
     * 中国联通号段
     * 130,131,132,145,146,155,156,166,175,176,185,186
     * 中国移动号段
     * 134(0-8),135,136,137,138,139,147,148,150,151,152,157,158,159,165,178,182,183,184,187,188,198
     * 上网卡专属号段（用于上网和收发短信，不能打电话）
     * 如中国联通的是145
     * 虚拟运营商
     * 电信：1700,1701,1702
     * 移动：1703,1705,1706
     * 联通：1704,1707,1708,1709,171
     * 卫星通信： 1349 <br>　　　　　未知号段：141、142、143、144、154
     * <p>
     * <p>
     * <p>
     * 验证手机号
     *
     * @param phone
     * @return
     */
    public static boolean isMobile(String phone) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        String s2 = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";// 验证手机号
        if (StringNotNull(phone)) {
            p = Pattern.compile(s2);
            m = p.matcher(phone);
            b = m.matches();
        }
        return b;
    }

    /**
     * 手机号简单校验,有后台做完整校验,后期有新增号段时,后台添加即可
     *
     * @return
     */
    public static boolean simpleMobile(String phone) {
        return CommonUtils.StringNotNull(phone) && phone.length() > 9;
    }


    /**
     * 验证输入金额
     */
    public static boolean isMoneyNO(String var) {  //^(([1-9]\d{0,9})|0)(\.\d{1,2})?$

        String telRegex = "^(([0-9]+\\.[0-9]{1,2})|([0-9]*[0-9][0-9]*\\.[0-9]{1,2})|([1-9]*[1-9][0-9]*))$";
        if (TextUtils.isEmpty(var))
            return false;
        else {
            return var.matches(telRegex);
        }
    }


    public static int dp2px(int dp, Context mContext) {
        assert mContext != null;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mContext.getResources().getDisplayMetrics());
    }


    /**
     * 保留2位小数
     *
     * @param val
     * @return
     */
    public static String rahToStr(double val) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(val);
        }
        return "";
    }

    /**
     * 保留2位小数
     *
     * @param val
     * @return
     */
    public static String rahToStr(double val, boolean isCut) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            if (isCut) {
                val = bd.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            return decimalFormat.format(val);
        }
        return "";
    }


    /**
     * 登录成功设置缓存
     *
     * @param response
     */
    public static void setLoginSuccessData(UserData response) {
        if (response != null) {
            SPUtil.setLoginSuccess(response);
            ClientInfo.initHeadParams();
        }
    }

    /**
     * 保留1位小数
     *
     * @param val
     * @return
     */
    public static String radixToStr(double val) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String format = decimalFormat.format(val);
            return MoneyUtils.getIntMoneyText(format);
        }
        return "";
    }

    /**
     * 金额2位小数
     *
     * @param val
     * @return
     */
    public static String radixMoney2(double val) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(val);
        }
        return "";
    }

    /**
     * 水位显示规则
     * 水位展示规则：赔率在10以内（不包含10）的水位小数点后展示两位数字（.00）；
     * 10-20之间（包含10，不包含20）的水位小数点后展示一位数字（.0），
     * 20（包含20）及以上的水位小数点后不展示，全局统一使用此规则展示水位。（赔率值包含本金的1.00）
     * 超过亿以后带小数位,计算不省略
     *
     * @param val
     * @param isBig 是否超出亿
     * @return
     */
    public static String radixOdds2(double val, boolean isBig) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            if (val < 10 || isBig) {
                val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                return decimalFormat.format(val);
            } else if (val >= 10 && val < 20) {
                val = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                return decimalFormat.format(val);
            } else {
                val = bd.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                DecimalFormat decimalFormat = new DecimalFormat("0");
                return decimalFormat.format(val);
            }
        }
        return "";
    }


    /**
     * 水位显示
     *
     * @param valS
     * @return
     */
    public static String radixOdds2(String valS) {
        if (CommonUtils.StringNotNull(valS)) {
            return radixOdds2(NumberUtils.stringToDouble(valS), false);
        }
        return "";
    }

    /**
     * 移除key或value为空的
     *
     * @param map
     * @return
     */
    public static Map<String, String> moveNullValue(Map<String, String> map) {
        if (MapNotNull(map)) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> stringEntry = iterator.next();
                if (stringEntry != null) {
                    String key = stringEntry.getKey();
                    String value = stringEntry.getValue();
                    if (!StringNotNull(key, value)) {
                        iterator.remove();
                    }
                }
            }
        }
        return map;
    }

    /**
     * 保留整数
     *
     * @param val
     * @return
     */
    public static String radixStr(double val) {

        double vals = Math.round(val);
        if (!Double.isNaN(vals) && vals != Double.NEGATIVE_INFINITY && vals != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(vals);
            vals = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat decimalFormat = new DecimalFormat("0");
            return decimalFormat.format(vals);
        }
        return "";
    }


    /**
     * List to 自定义分割的字符串
     *
     * @param list
     * @return
     */
    public static String listToString(List list, String fix) {
        StringBuilder sb = new StringBuilder();
        if (CommonUtils.ListNotNull(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i)).append(fix);
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * List to 逗号分割的字符串
     *
     * @param list
     * @return
     */
    public static String listToString(List list) {
        return listToString(list, StringConstants.comma);
    }

    /**
     * hashSet to 自定义分割的字符串
     *
     * @param list
     * @return
     */
    public static String hashSetToString(HashSet list, String fix) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            Iterator iterator = list.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (i < list.size() - 1) {
                    sb.append(next).append(fix);
                } else {
                    sb.append(next);
                }
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * hashSet to 逗号分割的字符串
     *
     * @param list
     * @return
     */
    public static String hashSetToString(HashSet list) {
        return hashSetToString(list, StringConstants.comma);
    }

    /**
     * map to 顿号分割的下弧线连接字符串
     *
     * @param map
     * @return
     */
    public static String mapToString(Map<Long, Long> map) {
        if (MapNotNull(map)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Long, Long> entry : map.entrySet()) {
                Long key = entry.getKey();
                Long value = entry.getValue();
                sb.append(key + "_" + value +StringConstants.comma);
            }
            String s = sb.toString();
            if (StringNotNull(s)) {
                try {
                    s = s.substring(0, s.length() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }
        }
        return "";
    }

    /**
     * 将每三个数字加上逗号处理（通常使用金额方面的编辑）
     *
     * @return 加上逗号的数字
     */

    public static String addComma(double price) {
        return MoneyUtils.rahToStr(price);
    }

    public static void hidInputKeyBord(View v, Context context) {
        assert context != null;
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public final static String numberToShowString(int number) {
        int numberW = 10000;
        int numberK = 1000;
        //百万以下
        int value_W = number / numberW;
        int value_mode_W = number % numberW;

        if (value_W <= 0) {
            //少于 1 W
            return String.valueOf(value_mode_W);
        } else {
            int value_K = value_mode_W / numberK;
            if (value_K > 0) {
                return value_W + "." + value_K + "万";
            } else {
                return value_W + "万";
            }
        }
    }

    public static String getImgURL(String url) {
        return BaseNetUtil.jointUrl(url);

    }

    public static final long INTERVAL = 500L; // 防止连续点击的时间间隔
    private static long lastClickTime = 0L; // 上一次点击的时间

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime) > INTERVAL) {
            lastClickTime = time;
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 处理double位数显示异常问题
     */
    public static double handleDouble(double value) {
        BigDecimal bg = new BigDecimal(value);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 删除所有的空格
     */
    public static String trimAllblank(String value) {
        return value.replaceAll(" ", "");
    }

    /**
     * 删除所有的空格
     */
    public static boolean isEmpty(String value) {
        if (value == null) return false;
        return TextUtils.isEmpty(trimAllblank(value));
    }

    /**
     * 判断是不是正确的身份证号码
     */
    public static boolean isIdcard(String idcardNo) {
        return !CommonUtils.StringNotNull(IDCard.isEnble(idcardNo));
    }

    public static String transformArrayToString(List<String> array) {

        String arrayString = "";
        if (array == null || array.isEmpty()) {

            return arrayString;
        }
        for (int index = 0; index < array.size(); index++) {

            if (index != array.size() - 1) {

                arrayString = arrayString + array.get(index) + ",";
            } else {

                arrayString = arrayString + array.get(index);
            }
        }
        return arrayString;
    }

    /**
     * app未启动
     *
     * @param context
     * @return
     */
    public static boolean isAppRunBackground(Context context) {
        assert context != null;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (tasks != null && !tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            boolean b = MyActivityManager.getActivityManager() == null
//                    || MyActivityManager.getActivityManager().getActivityStackSize() <= 0;
//            if ((!topActivity.getPackageName().equals(context.getPackageName())) && b) {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * 判断本应用是否在后台运行
     *
     * @param context
     * @return 本应用是否在后台运行，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            boolean b = MyActivityManager.getActivityManager() == null
                    || MyActivityManager.getActivityManager().getActivityStackSize() > 0;
            if ((!topActivity.getPackageName().equals(context.getPackageName())) && b) {
                return true;
            }
        }
        return false;
    }


    /**
     * 动态计算字符长度
     */
    public static int calculateStringLength(String etstring) {
        char[] ch = etstring.toCharArray();
        int varlength = 0;
        for (int i = 0; i < ch.length; i++) {
            //加入中文标点范围
            if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) { // 中文字符范围0x4e00 0x9fbb
                varlength = varlength + 2;
            } else {
                varlength++;
            }
        }
        return varlength;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        assert context != null;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        assert context != null;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String replaceNull(String str) {
        return (TextUtils.isEmpty(str) || "null".equals(str)) ? "" : str;
    }

    public static boolean StringNotNull(String ss) {
        return !TextUtils.isEmpty(ss) && !"null".equals(ss);
    }

    public static boolean StringNotNull(String... ss) {
        boolean bb = true;
        if (ss != null && ss.length > 0) {
            for (String k : ss) {
                bb = bb && StringNotNull(k);
            }
        } else {
            return false;
        }
        return bb;
    }

    public static boolean ListNotNull(List list) {
        return (list != null && list.size() > 0);
    }

    public static boolean MapNotNull(Map map) {
        return (map != null && map.size() > 0);
    }

    /**
     * 邮箱校验
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email != null && !TextUtils.isEmpty(email)) {
            String str = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
            Pattern p = Pattern.compile(str);
            Matcher m = p.matcher(email);
            return m.matches();
        }
        return false;
    }


    /**
     * 测量view高度
     *
     * @param child
     * @return
     */
    public static View measureViwe(View child) {
        if (child != null) {
            int intw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int inth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            child.measure(intw, inth);
        }
        return child;
    }

//
//    /**
//     * 获取照片URL字符串
//     *
//     * @param selImageList
//     * @return
//     */
//    public static String getCameraUrl(ArrayList<ImageItem> selImageList) {
//        StringBuilder sb = new StringBuilder();
//        if (CommonUtils.ListNotNull(selImageList)) {
//            for (int i = 0; i < selImageList.size(); i++) {
//                ImageItem imageItem = null;
//                try {
//                    imageItem = selImageList.get(i);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (imageItem != null) {
//                    String url = imageItem.getPath();
//                    if (CommonUtils.StringNotNull(url)) {
//                        if (i == selImageList.size() - 1) {
//                            sb.append(url);
//                        } else {
//                            sb.append(url + ",");
//                        }
//                    }
//                }
//            }
//        }
//        return sb.toString();
//    }

//    /**
//     * 获取照片URL集合
//     *
//     * @param url
//     * @return
//     */
//    public static ArrayList<ImageItem> getCameraList(String url) {
//        ArrayList<ImageItem> selImageList = new ArrayList<>();
//        if (CommonUtils.StringNotNull(url)) {
//            String[] split = url.split(",");
//            if (split.length > 0) {
//                for (String s : split) {
//                    ImageItem imageItem = new ImageItem();
//                    imageItem.setPath(s);
//                    selImageList.add(imageItem);
//                }
//            }
//        }
//        return selImageList;
//    }

    public static String hideMobileNum(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return "";
        }
        return mobile.replaceAll("(\\d{3})\\d{6}(\\d{1,2})", "$1*****$2");
    }

    /**
     * 是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        assert context != null;
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串转unicode
     *
     * @param str
     * @return
     */
    public static String stringToUnicode(String str) {
        StringBuffer sb = new StringBuffer();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            sb.append("\\u" + Integer.toHexString(c[i]));
        }
        return sb.toString();
    }


    /**
     * 字符串转URLEncode
     *
     * @param str
     * @return
     */
    public static String stringToURLEncode(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            if (!CommonUtils.StringNotNull(str)) {
                return "";
            }
            sb.append(URLEncoder.encode(str, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * URLDecode解码
     *
     * @param str
     * @return
     */
    public static String stringToURLDecoderToString(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(URLDecoder.decode(str, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * unicode转字符串
     *
     * @param unicode
     * @return
     */
    public static String unicodeToString(String unicode) {
        StringBuffer sb = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int index = Integer.parseInt(hex[i], 16);
            sb.append((char) index);
        }
        return sb.toString();
    }


    /**
     * URL 解码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:09:51
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private final static String ENCODE = "GBK";

    /**
     * URL 转码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:10:28
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 是否安装QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        assert context != null;
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 减去空格
     *
     * @param str
     * @return
     */
    public static String removeSpace(String str) {
        if (!TextUtils.isEmpty(str)) {
            str = str.replaceAll("\\" +
                    "s*", "");
        }
        return str;
    }


    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p/>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p/>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p/>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p/>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity);
        } else {
            convertActivityToTranslucentBeforeL(activity);
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    public static void convertActivityToTranslucentBeforeL(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    private static void convertActivityToTranslucentAfterL(Activity activity) {
        try {
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object options = getActivityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, options);
        } catch (Throwable t) {
        }
    }


    /**
     * 千换算
     *
     * @param number
     * @return
     */
    public static String getThousandNumber(int number) {
        String s = "";
        if (number > 0) {
            if (number >= 1000000) {
                s = deleteZero(MoneyUtils.mDecimalFormat(number / 1000000f, "0.0")) + "M";
            } else if (number >= 1000) {
                s = deleteZero(MoneyUtils.mDecimalFormat(number / 1000f, "0.0")) + "K";
            } else {
                s = String.valueOf(number);
            }
        }
        return s;
    }

    private static String deleteZero(String s) {
        if (s.indexOf(".") > 0) {
            // 正则表达
            s = s.replaceAll("0+?$", "");// 去掉后面无用的零;
            s = s.replaceAll("[.]$", "");// 如小数点后面全是零则去
        }
        return s;
    }


    public static String fixThree(String number) {
        if (CommonUtils.StringNotNull(number)) {
            if (number.length() == 7) {
                number = number.substring(4, number.length());
            }
            if (number.length() == 1) {
                number = "00" + number;
            } else if (number.length() == 2) {
                number = "0" + number;
            }
        }
        return number;
    }

    public static String fixImageUrl(String url) {
        if (StringNotNull(url)) {
            return new StringBuilder("[IMG]").append(url).append("[/IMG]").toString();
        }
        return url;
    }

//    public static String getRunningActivityName(Context context) {
//        if (context == null)
//            return "";
//        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
//        return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
//    }

    public static Animation setRotateAnimation(boolean isRotate) {
        return setRotateAnimation(isRotate ? 45 : 0, isRotate ? 0 : 45, 300);
    }


    public static Animation setRotateAnimation(float fromDegrees, float toDegrees, long time) {
        Animation rotate = new RotateAnimation(fromDegrees, toDegrees
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator()); //设置插值器
        rotate.setDuration(time);//设置动画持续周期
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        return rotate;
    }


    public static ValueAnimator getValueShow(View view, int height) {
        return getValueShow(view, height, 1000);
    }

    public static ValueAnimator getValueShow(View view, int height, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                view.setAlpha((animatedValue / 100f));
                view.getLayoutParams().height = (int) ((animatedValue / 100f) * height);
                view.requestLayout();
            }
        });
        return animator;
    }

    public static ValueAnimator getValueHidden(View view, int height) {
        return getValueHidden(view, height, 1000);
    }

    public static ValueAnimator getValueHiddenOrShow(boolean isHidden, View view, int height, long duration) {
        ValueAnimator animator;
        if (isHidden) {
            animator = getValueHidden(view, height, duration);
        } else {
            animator = getValueShow(view, height, duration);
        }
        return animator;
    }

    public static ValueAnimator getValueHidden(View view, int height, long duration) {

        ValueAnimator animator = ValueAnimator.ofInt(100, 0);
        animator.setDuration(duration);
        animator.addUpdateListener(valueAnimator -> {
            int animatedValue = (int) valueAnimator.getAnimatedValue();
//                view.setAlpha((animatedValue / 100f));
            view.getLayoutParams().height = (int) ((animatedValue / 100f) * height);
            view.requestLayout();
        });
        return animator;
    }

    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public final static float raid = 0.1f;

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, (int) (canvas.getWidth() * raid), (int) (canvas.getHeight() * raid));
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap decodeResource(Context context, int resId) {
        assert context != null;
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }


    public static ArrayList<String> getGalleryUrls(String url) {
        ArrayList<String> list = new ArrayList<>();
        if (CommonUtils.StringNotNull(url) && isContainsHttp(url)) {
            list.add(url);
        }
        return list;
    }

    public static boolean isContainsHttp(String s) {
        if (CommonUtils.StringNotNull(s)) {
            String ss = s.toLowerCase();
            if (ss.startsWith("http") || ss.startsWith("https")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 字符长度限制
     *
     * @param str
     * @param maxLen
     * @return
     */
    public static String handleCommText(String str, int maxLen) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = str.length();
        if (count <= maxLen) {
            return str;
        } else {
            return str.substring(0, maxLen) + "...";
        }
    }

    /**
     * 字符长度限制，中文2个字符 ，英文1个字符
     *
     * @param str
     * @param maxLen
     * @return
     */
    public static String handleText(String str, int maxLen) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return str;
        } else {
            return str.substring(0, endIndex) + "...";
        }
    }

    /**
     * 获取字符长度,中文2位,英文、数字1位
     *
     * @param s
     * @return
     */
    public static int getStringLength(String s) {
        int length = 0;
        if (CommonUtils.StringNotNull(s)) {
            for (int i = 0; i < s.length(); i++) {
                char item = s.charAt(i);
                if (item < 128) {
                    length = length + 1;
                } else {
                    length = length + 2;
                }
            }
        }
        return length;
    }


    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    public static boolean canVerticalScroll(EditText editText) {
        try {
            return canVerticalScroll2(editText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    private static boolean canVerticalScroll2(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }


    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text) {
        copyText(context, text, true);
    }


    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text, boolean isToast) {
        if (CommonUtils.StringNotNull(text) && context != null) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", text);//text是内容
            try {
                cm.setPrimaryClip(clipData);
                if (isToast) {
                    ToastUtils.show(UIUtils.getString(R.string.System_copytext_Success));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text, String toast) {
        if (CommonUtils.StringNotNull(text) && context != null) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", text);//text是内容
            try {
                cm.setPrimaryClip(clipData);
                ToastUtils.show(toast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除剪贴板
     *
     * @param context
     */
    public static void cleanClipboardText(Context context) {
        if (context != null) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", "");//text是内容
            cm.setPrimaryClip(clipData);
        }
    }

    /**
     * 获取系统剪贴板内容
     *
     * @param context
     */
    public static String getClipContent(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            if (manager.hasPrimaryClip()) {
                ClipData primaryClip = manager.getPrimaryClip();
                if (primaryClip != null) {
                    if (primaryClip.getItemCount() > 0) {
                        ClipData.Item itemAt = primaryClip.getItemAt(0);
                        if (itemAt != null) {
                            CharSequence addedText = itemAt.getText();
                            String addedTextString = String.valueOf(addedText);
                            if (StringNotNull(addedTextString)) {
                                return addedTextString;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 清空剪贴板内容
     */
    public static void clearClipboard(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setText(null);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }


    /**
     * 获取最后可见position
     *
     * @param lastVisiblePositions
     * @return
     */
    public static int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 设置缩放动画
     *
     * @param view
     */
    public static void setScaleAnimator(boolean isShow, View view, long delayMillis) {
        if (view != null) {
            if (isShow && view.getVisibility() == View.VISIBLE) {
                return;
            }
            if (!isShow && view.getVisibility() == View.GONE) {
                return;
            }
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(isShow ? View.VISIBLE : View.GONE);//先设置显示
                    AnimatorSet animatorSet = new AnimatorSet();//组合动画
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", isShow ? 0 : 1, isShow ? 1f : 0);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", isShow ? 0 : 1, isShow ? 1f : 0);
                    animatorSet.setDuration(300);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
                    animatorSet.start();
                }
            }, delayMillis);
        }
    }


    /**
     * 获取Stringlist
     *
     * @param s
     * @return
     */
    public static List<String> getStringList(String s) {
        ArrayList<String> list = new ArrayList<>();
        if (CommonUtils.StringNotNull(s)) {
            try {
                list = JSONUtils.fromJson(s, new TypeToken<ArrayList<String>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取StringMap
     *
     * @param s
     * @return
     */
    public static HashMap<String, String> getStringMap(String s) {
        HashMap<String, String> map = new HashMap<>();
        if (CommonUtils.StringNotNull(s)) {
            try {
                map = JSONUtils.fromJson(s, new TypeToken<HashMap<String, String>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    /**
     * 数值转大写
     *
     * @param number
     * @return
     */
    public static String numberConvert(int number) {
        return Num2CN.cvt(number);
    }


    /**
     * 处理帖子分享内容过长 50截取
     *
     * @return
     */
    public static String handlerContent(String content) {
        if (StringNotNull(content) && content.length() >= 50) {
            content = content.substring(0, 50);
        }
        return content.trim();
    }


    /**
     * 从剪贴板获取分享短码
     *
     * @param context
     * @return
     */
    public static String getClipShareCode(Context context) {
        String s = CommonUtils.getClipContent(context.getApplicationContext());
        if (CommonUtils.StringNotNull(s) && s.contains("后") && s.contains("打")) {
            try {
                s = s.replace("\n", "").trim();
                CharSequence charSequence = s.subSequence(s.indexOf("后") + 1, s.indexOf("打"));
                String addedTextString = String.valueOf(charSequence);
                if (StringNotNull(addedTextString)) {
                    return addedTextString.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 重新启动App ->杀进程,会短暂黑屏,启动慢
     */
    public static void restartApp(Context context) {
        //启动页
        Intent intent = new Intent(context, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 处理跳转网址
     *
     * @return
     */
    public static String getHandleUrl(String remark) {
        if (CommonUtils.StringNotNull(remark)) {//Remark=网址:www.53460.com
            if (remark.contains("网址:")) {
                remark = remark.replace("网址:", "");
            }
            if (remark.startsWith("www") || remark.startsWith("WWW")) {
                remark = "http://" + remark + "/";
            }
        }
        return remark;
    }


    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     *
     * @param source
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    /**
     * 将一个list均分成多个,均分大小n
     *
     * @param list
     * @param groupSize
     * @return
     */
    public static <T> List<List<T>> averageList(List<T> list, int groupSize) {
        List<List<T>> result = new ArrayList<List<T>>();
        int length = list.size();
        // 计算可以分成多少组  (11 +3 -1)/3
        int num = (length + groupSize - 1) / groupSize;
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            try {
                result.add(list.subList(fromIndex, toIndex));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 按长度切割字符串
     *
     * @param inputString
     * @param length
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        List<String> list = new ArrayList<>();
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f);
        } else {
            return str.substring(f, t);
        }
    }

    /**
     * 去除前缀后缀
     *
     * @param host
     * @return
     */
    public static String getDomain(String host) {
        String s = "";
        if (host.contains("//")) {
            String[] split = host.split("//");
            if (split.length > 1) {
                return getDomain(split[1]);
            }
        } else if (host.contains("/")) {
            String[] split = host.split("/");
            if (split.length > 0) {
                return getDomain(split[0]);
            }
        } else {
            s = host;
        }
        return s;
    }

    /**
     * 生成随机数字和字母
     *
     * @param length
     * @return
     */
    public static String getStringRandom(int length, boolean inNum) {
        StringBuilder val = new StringBuilder();
        if (length <= 0) {
            return "";
        }
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            boolean isChar = !inNum && random.nextInt(2) % 2 == 0;
            //输出字母还是数字
            if (isChar) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }

    /**
     * 请求基础随机数
     *
     * @return
     */
    public static String getNonceParams() {
        return getStringRandom(20, false).toLowerCase();
    }


    /**
     * 通用点击背景设置
     *
     * @param textView
     * @param isClickable
     * @param clickedColor
     * @param unClickColor
     */
    public static void setCommClickable(TextView textView, boolean isClickable, int clickedColor, int unClickColor) {
        if (textView == null) {
            return;
        }
        GradientDrawable myGrad = (GradientDrawable) textView.getBackground();
        Resources resources = textView.getContext().getResources();
        if (resources == null) {
            return;
        }
        int color;
        if (isClickable) {
            color = resources.getColor(clickedColor);
        } else {
            color = resources.getColor(unClickColor);
        }
        myGrad.setColor(color);
        textView.setClickable(isClickable);
        textView.setEnabled(isClickable);
    }

    /**
     * 获取dimen总值
     *
     * @param res
     * @return
     */
    public static float getTotalDimen(int... res) {
        float v = 0;
        if (res != null && res.length > 0) {
            for (int re : res) {
                v += MyApp.getMyDimension(re);
            }
        }
        return v;
    }

    /**
     * 是否需要公告弹框(调用接口)
     *
     * @return
     */
    public static boolean needShowNotice() {
        NoticeDialogData.DialogTag rememberNoTip = SPUtil.getRememberNoTip();
        if (rememberNoTip != null) {
            if (!DateUtils.covertDateToString(rememberNoTip.time)//非当前天,新的一天
                    .equals(DateUtils.covertDateToString(System.currentTimeMillis()))) {
                return true;
            } else {//是当前天
                return !rememberNoTip.rememberNoTip;//今日不提示,不需要调用接口
            }
        }
        return true;
    }

    /**
     * 消息中心类型图标
     *
     * @param type /1,通知 2，公告
     * @return
     */
    public static int getMsgTypeStatus(int type) {
        int res = R.drawable.icon_msg_notice;
        if (type == 1) {
            res = R.drawable.icon_msg_order;
        } else if (type == 2) {
            res = R.drawable.icon_msg_notice;
        }
        return res;
    }

    /**
     * 校验姓名中的点 {@link RealNameInputFilter#dot}
     *
     * @param name
     * @return
     */
    public static boolean checkDot(String name) {
        if (name.contains(RealNameInputFilter.dot)) {
            if (name.endsWith(RealNameInputFilter.dot)) {
                return true;
            }
            String[] split = name.split(RealNameInputFilter.dot);
            if (split.length > 0) {
                for (String s : split) {
                    if (!CommonUtils.StringNotNull(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取环境配置
     *
     * @return 应用环境  0,测试  1,预发布  2,线上
     */
    public static int getEnvMode() {
        return MyApp.isDebug ? NumberUtils.stringToInt(MyApp.getMyString(R.string.env_mode)) : 2;
    }

    /**
     * 根据环境配置获取默认域名
     *
     * @return 应用环境  0,测试  1,预发布  2,线上
     */
    public static String getDefDomainByEnvMode() {
        int envMode = getEnvMode();
        String domain = "";
        List<String> stringList = CommonUtils.getStringList(DeflaterUtils.unzipString(JSONUtils.getStringByAss(
                "json/b.json", MyApp.getContext())));
        if (CommonUtils.ListNotNull(stringList)) {
            domain = stringList.get(envMode);
        }
        return domain;
    }

    /**
     * 根据context 获取 activity
     *
     * @param context
     * @return
     */
    public static @Nullable
    Activity getActivityFromContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof Application || context instanceof Service) {
            return null;
        }
        Context c = context;
        while (c != null) {
            if (c instanceof ContextWrapper) {
                c = ((ContextWrapper) c).getBaseContext();
                if (c instanceof Activity) {
                    return (Activity) c;
                }
            } else {
                return null;
            }
        }
        return null;
    }


    /**
     * 获取基类
     *
     * @param context
     * @return
     */
    public static ZZActivity getZZActivity(@Nullable Context context) {
        Activity activity = getActivityFromContext(context);
        if (activity instanceof ZZActivity) {
            return (ZZActivity) activity;
        }
        return null;
    }

    /**
     * 元素为对象的集合传参因后台框架需转换处理(去除换行等号补位)
     *
     * @param t
     * @return
     */
    public static <T> String getListParam(List<T> t) {
        String encode = BASE64Utils.encode(JSONUtils.toJson(t), Base64.NO_PADDING);
        if (!CommonUtils.StringNotNull(encode)) {
            return "";
        }
        return encode.replaceAll("[\r\n]", "");
    }

    /**
     * 是否json格式
     *
     * @param str
     * @return
     */
    public static boolean getJSONType(String str) {
        boolean result = false;
        if (StringNotNull(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 跳转浏览器
     *
     * @param context
     * @param url
     */
    public static void startBrowser(Context context, String url) {
        if (null != context) {
            if (!CommonUtils.isContainsHttp(url)) {
                url = Config.HTTP_TITLE + url;
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        }
    }

    // 应用可见时（在前台 且 亮屏） 则返回true
    public static boolean isAppShow(Context context) {
        if (context == null) {
            return false;
        }
        //在前台 且 亮屏 返回true
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!isRunningBackground(context) && pm.isScreenOn()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断本应用是否在后台运行
     *
     * @param context
     * @return 本应用是否在后台运行，返回 true；否则返回 false
     */
    //应用在前台返回false
    //应用切后台返回true
    //应用在前台锁屏 返回false
    public static boolean isRunningBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            boolean b = MyActivityManager.getActivityManager() != null
                    && MyActivityManager.getActivityManager().getActivityStackSize() > 0;
            return (!topActivity.getPackageName().equals(context.getPackageName())) && b;
        }
        return false;
    }

    //收起通知栏
    public static void collapseNotificationBar(Context context) {
        @SuppressLint("WrongConstant")
        Object service = context.getSystemService("statusbar");
        if (null == service)
            return;
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method collapse;
            if (sdkVersion <= 16) {
                collapse = clazz.getMethod("collapse");
            } else {
                collapse = clazz.getMethod("collapsePanels");
            }
            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showPhoneWakeUp(Context context) {
        //屏锁管理器
//        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
//        //解锁
//        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_DIM_WAKE_LOCK
                , BuildConfig.APPLICATION_ID + ":bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }


}






