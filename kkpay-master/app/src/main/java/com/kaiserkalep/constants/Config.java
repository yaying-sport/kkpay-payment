package com.kaiserkalep.constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.ui.activity.MainActivity;
import com.king.zxing.util.CodeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static int width = -1;

    public static final int NotificationID = 101010;
    public static final int EGATIVE_ONE = -1;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int ELEVEN = 11;
    public static final int TWELVE = 12;
    public static final int THIRTEEN = 13;
    public static final int FOURTEEN = 14;
    public static final int FIFTEEN = 15;
    public static final int SIXTEEN = 16;
    public static final int TWENTY = 20;
    public static final int TWOHUNDRED = 200;


    public static final String SCHEME = "kkpay";
    public static final String SCHEME_HOST = "pay";
    public static final String SCHEME_PARAM = "orderNo";
    public static final String SCHEME_SH_HOST = "agent";
    public static final String SCHEME_SH_PARAM = "address";
    public static final String NORMAL_PAGE_SIZE = "20";
    public static final String NORMAL_BUYCOIN_PAGE_SIZE = "50";
    public static final String MAX_PAGE_SIZE = "200";
    public static String HTTPS_TITLE = "https://";
    public static String HTTP_TITLE = "http://";
    public static String STRING_DNS = "v4Ips";
    public static String HOST = "Host";

    public static final String ZERO_STRING = "0";
    public static final String ONE_STRING = "1";
    public static final String TWO_STRING = "2";
    public static final String THREE_STRING = "3";
    public static final String FOUR_STRING = "4";
    public static final String FIVE_STRING = "5";
    public static final String SIX_STRING = "6";
    public static final String EIGHT_STRING = "8";
    public static final String TEN_STRING = "10";
    public static final String TWENTY_STRING = "20";

    public static final String OWN_ONE_STRING = "-1";
    public static final String OWN_TWO_STRING = "-2";


    /**
     * 强制隐藏软键盘
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (null == activity) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (null != imm && null != currentFocus) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), ZERO); //强制隐藏键盘
        }
    }

    public static int getScreenWidth() {
        if (width < ZERO) {
            WindowManager wm = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
            if (null != wm) {
                DisplayMetrics dm = new DisplayMetrics();
                Display defaultDisplay = wm.getDefaultDisplay();
                defaultDisplay.getMetrics(dm);
                width = dm.widthPixels;
            } else {
                width = EGATIVE_ONE;
            }
            return width;
        } else {
            return width;
        }
    }

    //Object转Map
    public static <T> Map<String, T> getObjectToMap(Object obj, Class<T> clazz) throws IllegalAccessException {
        Map<String, T> map = new HashMap<String, T>();
        Class<?> cla = obj.getClass();
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String keyName = field.getName();
            T value = (T) field.get(obj);
            map.put(keyName, value);
        }
        return map;
    }

    /**
     * @param str 目标文本
     * @return 返回https://  或  http：//
     */
    public static String getHttpHead(String str) {
        if (!TextUtils.isEmpty(str) && str.length() > EIGHT) {
            String ss = str.toLowerCase();
            if (ss.startsWith(Config.HTTPS_TITLE)) {
                return Config.HTTPS_TITLE;
            }
            if (ss.startsWith(Config.HTTP_TITLE)) {
                return Config.HTTP_TITLE;
            }
        }
        return Config.HTTPS_TITLE;
    }

    /**
     * @param str 目标文本
     * @return 返回域名中host
     */
    public static String getHttpHost(String str) {
        if (!TextUtils.isEmpty(str) && str.length() > EIGHT) {
            String ss = str.toLowerCase();
            if (ss.startsWith(Config.HTTPS_TITLE)) {
                return str.substring(EIGHT, str.length());
            }
            if (ss.startsWith(Config.HTTP_TITLE)) {
                return str.substring(SEVEN, str.length());
            }
        }
        return str;
    }

    /**
     * @param str 目标文本
     * @return ONE:https://  ; TWO:http://  ; ZERO:无https://和http：//
     */
    public static int judgeHttp(String str) {
        if (!TextUtils.isEmpty(str) && str.length() > EIGHT) {
            String ss = str.toLowerCase();
            if (ss.startsWith(Config.HTTPS_TITLE)) {
                return ONE;
            }
            if (ss.startsWith(Config.HTTP_TITLE)) {
                return TWO;
            }
        }
        return ZERO;
    }

    /**
     * 减法运算。
     *
     * @param amount1 被减数
     * @param amount2 减数
     * @return 两个参数的差
     */
    public static double subtraction(double amount1, double amount2) {
        BigDecimal mDecimal1 = new BigDecimal(Double.toString(amount1));
        BigDecimal mDecimal2 = new BigDecimal(Double.toString(amount2));
        return mDecimal1.subtract(mDecimal2).doubleValue();
    }

    /**
     * 减法运算。
     *
     * @param amount1 被减数
     * @param amount2 减数
     * @return 两个参数的差
     */
    public static double subtraction(double amount1, double... amount2) {
        BigDecimal mDecimal1 = new BigDecimal(Double.toString(amount1));
        for (int i = 0; i < amount2.length; i++) {
            BigDecimal mDecimal = new BigDecimal(Double.toString(amount2[i]));
            mDecimal1 = mDecimal1.subtract(mDecimal);
        }
        return mDecimal1.doubleValue();
    }

    /**
     * 加法运算。
     *
     * @param amount1 被加数
     * @param amount2 加数
     * @return 两个参数的和
     */
    public static double add(double amount1, double amount2) {
        BigDecimal mDecimal1 = new BigDecimal(Double.toString(amount1));
        BigDecimal mDecimal2 = new BigDecimal(Double.toString(amount2));
        return mDecimal1.add(mDecimal2).doubleValue();
    }

    /**
     * 乘法运算。
     *
     * @param amount1 被乘数
     * @param amount2 乘数
     * @return 两个参数的积
     */
    public static double multiplication(double amount1, double amount2) {
        BigDecimal mDecimal1 = new BigDecimal(Double.toString(amount1));
        BigDecimal mDecimal2 = new BigDecimal(Double.toString(amount2));
        return mDecimal1.multiply(mDecimal2).doubleValue();
    }

    /**
     * 除法运算。
     * MathContext.DECIMAL128其精度设置与Decimal128格式，34位数字和 [HALF_EVEN]的舍入模式一样。
     * divide()方法默认为MathContext.UNLIMITED无限取值，不做限制陷入死循环会抛出ArithmeticException(计算溢出)异常。
     *
     * @param amount1 被除数
     * @param amount2 除数
     * @return 两个参数的商
     */
    public static double divisionOperation(double amount1, double amount2) {
        BigDecimal mDecimal1 = new BigDecimal(Double.toString(amount1));
        BigDecimal mDecimal2 = new BigDecimal(Double.toString(amount2));
        return mDecimal1.divide(mDecimal2, MathContext.DECIMAL128).doubleValue();
    }

    /**
     * um
     *
     * @param num   需要跳转的double值
     * @param Value 保留几位小数
     * @return
     */
    public static String getNumToString(double num, int Value) {
        String str = "";
        try {
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            numberInstance.setMaximumFractionDigits(Value);
            numberInstance.setRoundingMode(RoundingMode.DOWN);
            str = numberInstance.format(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static String getInteger(double d) {
        String str = "";
        try {
            if (d < 0) {
                return str;
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                str = decimalFormat.format(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 去掉String所有.
     *
     * @param s
     * @return
     */
    public static String subNotAndDot(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return "0";
            }
            int i = s.indexOf(".");
            if (i > 0) {
                s = s.substring(0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 保留String中.后面一位
     *
     * @param s
     * @return
     */
    public static String subOneAndDot(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return "0.0";
            }
            int i = s.indexOf(".");
            if (i > 0 && s.length() - i > 2) {
                s = s.substring(0, (i + 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 添加String中.后面一位
     *
     * @param s
     * @return
     */
    public static String addOneAndDot(String s) {
        try {
            if (TextUtils.isEmpty(s)) {
                return "0.0";
            }
            int i = s.indexOf(".");
            if (i < 0) {
                s = s + ".0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getUrlValue(String str) {
        String value = "";
        if (null != str) {
            int i = str.indexOf("=");
            if (i > Config.EGATIVE_ONE) {
                value = str.substring(i + ONE, str.length());
            }
        }
        return value;
    }

    public static void toSettingNotify(Activity activity) {
        try {
            if (null != activity) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", activity.getPackageName());
                    intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                    activity.startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                }
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), Config.SCHEME);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, MyApp.getLanguageString(context, R.string.Share_Save_Error), Toast.LENGTH_SHORT).show();
            return;
        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        // 通知图库更新
        String[] paths = new String[]{file.getAbsolutePath()};
        MediaScannerConnection.scanFile(context, paths, null, null);
        Toast.makeText(context, MyApp.getLanguageString(context, R.string.Share_Save_Success), Toast.LENGTH_SHORT).show();

    }
}
