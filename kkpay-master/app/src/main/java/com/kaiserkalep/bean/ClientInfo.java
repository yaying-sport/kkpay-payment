package com.kaiserkalep.bean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.SPUtil;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.widgets.bigimageViewpage.tool.utility.text.MD5Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * by Zane, 2016/8/19.
 * 对外提供Core Module的一些接口
 * sy_ui	用户IP
 * sy_um	用户mac地址
 * sy_mc	设备号
 * sy_lo	经度
 * sy_la	纬度
 * sy_ov	操作系统版本
 * sy_mm	手机型号
 * sy_nw	网络模式WIFI/4G
 * sy_an	APP名称
 * sy_av	APP版本
 * sy_hv	APP补丁版本
 * sy_ba	浏览器 user-agent
 * sy_gs	终端全局标识
 * sy_oi	H5微信入口openId
 */
public class ClientInfo {


    public static HashMap<String, String> headMap = new HashMap();
    private static final String CHARSET_NAME = "UTF-8";
    private static final String INVALID_ANDROID_ID = "9774d56d682e549c";
    public static final String INTERFACE_VERSION = "1.0.0"; //version
    public static final double VERSION_NUMS = 2.01; //更新时的版本比对qian


    /**
     * 屏幕密度
     */
    public static float scale = 1.5f;

    /**
     * 是否有网络
     */
    public static boolean isConnect = true;

    /**
     * 渠道
     */
    public static String SOURCE = "";
    /**
     * 版本号
     */
    public static int VERSION_NUM = 0;
    /**
     * 外部版本
     */
    public static String VER = "";
    /**
     * 热修复补丁版本(打补丁包修改)
     */
    public static String HOTFIX_VERSSION = "";

    /**
     * 客服端设备ID
     */
    public static String IMEI = "";
    /**
     * IP地址
     */
    public static String WAP_IP = "";

    /**
     * 当前手机分辨率
     */
    public static String SCREEN = "0x0";


    /**
     * 设备类型  android   ios   h5
     */
    public static String DEVICE = "android";

    /**
     * 接入点是否是使用wap方式
     */
    public static boolean ISWAP = false;
    /**
     * 网络方式
     */
    public static String NET_TYPE = "";

    /**
     * 机器型号
     */
    public static String MODEL = "0000";

    /**
     * 经度
     */
    public static String LAT = "";
    /**
     * 纬度
     */
    public static String LONG = "";

    /**
     * 渠道(代理商渠道)
     */
    public static String CHANNEL = "";
    /**
     * 系统版本
     */
    public static String OS_VER = "";
    /**
     * 是否发布版
     */
    public static boolean IS_RELEASE = true;


    /**
     * 基础参数 用户id
     */
    public static String USER_ID;


    /**
     * 基础参数 token
     */
    public static String AUTHORIZATION = "";

    private ClientInfo() {
    }

    public static void init(Context context) {
        //更新屏幕密度
        UIUtils.dip2px(context, 0);
        SPUtil.setDeviceId(obtainDeviceId(context));
        getVersionCode(context);
        initHeadParams();

    }

    public static void initHeadParams() {
        USER_ID = SPUtil.getUserId();
        AUTHORIZATION = SPUtil.getToken();
        IMEI = SPUtil.getDeviceId();
    }


    public static void updata(Context context) {
        headMap.put(StringConstants.PHONE_MODEL, String.valueOf(Build.MODEL));
        headMap.put(StringConstants.OS_VER, String.valueOf(android.os.Build.VERSION.RELEASE));
        headMap.put(StringConstants.IMEI, SPUtil.getDeviceId());
        headMap.put(StringConstants.DEVICE, DEVICE);
        headMap.put(StringConstants.X_AUTH_TOKEN, SPUtil.getToken());
        headMap.put(StringConstants.VER, getVersionCode(context));
    }

    public static void main(String[] args) {
        Map<String, String> map = new TreeMap<>(String::compareTo);
        map.put("ts", "1597395858");
        map.put("nonce", "ded482eb4cadc613a9de");
        map.put("pageSize", "10");
        map.put("isAsc", "desc");
        map.put("orderByColumn", "createTime");
        map.put("beginTime", "");
        map.put("endTime", "");
        map.put("projectId", "");
        map.put("sk", "647376aa81d64dfda925124eb08bcc66");
        map.put("pageNum", "1");
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry != null) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&").append(key).append("=").append(value);
            }
        }
        String s = sb.toString();
        System.out.print(s + "\n");
        String ss = s.substring(1);
        System.out.print(ss + "\n");
        System.out.print(MD5Util.md5Encode(ss) + "\n");
    }


    public static Map<String, String> getUrlParams(Map<String, String> params) {
        Map<String, String> map = new TreeMap<>(String::compareTo);
        map.putAll(params);
        return map;
    }


    private static String obtainDeviceId(Context context) {
        String deviceId = SPUtil.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            //step1:正常获取
            if (!TextUtils.equals(INVALID_ANDROID_ID, androidId)) {
                deviceId = UUID.nameUUIDFromBytes(androidId.getBytes(CHARSET_NAME)).toString();
            } else {
                //step2:如果是非法值，再通过TelephonyManager获取
                @SuppressLint("MissingPermission") String id = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).
                        getDeviceId();
                if (!TextUtils.isEmpty(id)) {
                    deviceId = UUID.nameUUIDFromBytes(deviceId.getBytes(CHARSET_NAME)).toString();
                } else {
                    //step3:如果还是未获取到，则通过UUID生成一个
                    deviceId = UUID.randomUUID().toString();
                }
            }
        } catch (Exception e) {
            LogUtils.e("ClientInfo.getDeviceId() error");
        }
//        if (!CommonUtils.StringNotNull(deviceId)) {
//            deviceId = UTDevice.getUtdid(context);
//        }
        SPUtil.setDeviceId(deviceId);
        return deviceId;
    }

    public static HashMap<String, String> getHeadMap() {
        updata(MyApp.getApplication().getApplicationContext());
        return headMap;
    }

    public static String getVersionCode(Context context) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            VER = packInfo.versionName;
            IS_RELEASE = !VER.contains("test");
            VERSION_NUM = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VER;
    }

//    public static void updataNetType(Context context) {
//        obtainMACIP(context);
//        NET_TYPE = GetNetworkType(context);
//        ISWAP = !"WIFI".equals(NET_TYPE);
//        WAP_IP = getHostIP();
//        isConnect= NetWorkUtils.isNetworkConnected(context);
//        BaseNetUtil.putStringParams(headMap,NetUrlHeadline.SY_NW,NET_TYPE);
//        BaseNetUtil.putStringParams(headMap,NetUrlHeadline.SY_UI,WAP_IP);
//    }

//    /**
//     * 获取版本号
//     *
//     * @return
//     */
//    public static String getVersionName(Context context) {
//        // 获取包管理者
//        PackageManager pm = context.getPackageManager();
//        try {
//            // 获取包信息
//            PackageInfo packageInfo = pm.getPackageInfo(
//                    context.getPackageName(), 0);
//            // 获取versionName
//            return packageInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }

//    /**
//     * 获取渠道
//     *
//     * @return
//     */
//    public static void updataSource(Context context) {
//        if(context==null)
//        {
//            return;
//        }
//        try {
//            SOURCE = WalleChannelReader.getChannel(context.getApplicationContext());
//            if(!CommonUtils.StringNotNull(SOURCE))
//            {
//                SOURCE = AnalyticsConfig.getChannel(context);
//            }
//            BaseNetUtil.putStringParams(headMap,NetUrlHeadline.MO_NA,SOURCE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    public static String getDeviceId() {
//        return DEVICE_ID;
//    }

//    private static void obtainMACIP(Context context) {
//        try {
//            //获取wifi服务
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
////            if (!wifi.isWifiEnabled()) {
////                wifi.setWifiEnabled(true);
////            }
//            MAC_IP = wifi.getConnectionInfo().getMacAddress();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            LogUtils.e("getHostIP error");
        }
        return hostIp;

    }

    public static String GetNetworkType(Context context) {
        String strNetworkType = "";
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    LogUtils.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }

                    LogUtils.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtils.e("cocos2d-x", "Network Type : " + strNetworkType);

        return strNetworkType;
    }

    /**
     * 获取宽
     *
     * @return
     */
    public static int getScreenWidth() {
        String screen = ClientInfo.SCREEN;
        int screenWidth = 0;
        try {
            String[] xes = screen.split("x");
            String x = xes[0];
            screenWidth = Integer.parseInt(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWidth;
    }

    /**
     * 获取高
     *
     * @return
     */
    public static int getScreenHeight() {
        String screen = ClientInfo.SCREEN;
        int screenHeight = 0;
        try {
            String[] yes = screen.split("x");
            String y = yes[1];
            screenHeight = Integer.parseInt(y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenHeight;
    }

    /**
     * 获取屏幕信息
     */
    public static void getScreenParam(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ClientInfo.SCREEN = dm.widthPixels + "x" + getRealHeight(context);
    }


    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        int realHeight = dm.heightPixels;
        return realHeight;
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
