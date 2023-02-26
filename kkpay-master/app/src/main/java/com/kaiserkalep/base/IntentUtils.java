package com.kaiserkalep.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.bean.HashMapEntity;
import com.kaiserkalep.constants.Config;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.interfaces.ActivityIntentInterface;
import com.kaiserkalep.ui.activity.DepositShActivity;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.ScanBindActivity;
import com.kaiserkalep.ui.activity.ScanDetailActivity;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.LogUtils;
import com.kaiserkalep.utils.NumberUtils;
import com.kaiserkalep.utils.PushMessageUtils;
import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.toast.ToastUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 隐式意图跳转工具类
 *
 * @Auther: Administrator
 * @Date: 2019/4/4 0004 20:52
 * @Description:
 */
public class IntentUtils {
    private static final String TAG = "IntentUtils";

    public static Intent getIntent(Context context, String activityName, HashMap params) {
        if (context != null) {
            Intent in = new Intent();
            in.setAction(context.getString(R.string.action));
            in.addCategory(context.getString(R.string.category));
            if (TextUtils.isEmpty(activityName)) {
                return null;
            }
            Uri uri = null;
            if (CommonUtils.isContainsHttp(activityName)) {
                return getWebIntent(activityName);
            } else {
                if (activityName.startsWith(getScheme(context))) {
                    //拦截手机浏览器   yaying://browserWebView?url=http://www.wwcp.com
//                    if (activityName.contains(context.getString(R.string.BrowserWebView))) {
//                        String host = PushMessageUtils.getHost(activityName, context);
//                        if (CommonUtils.StringNotNull(host) && context.getString(R.string.BrowserWebView).equals(host)) {
//                            String ss = activityName.substring(activityName.indexOf("url") + 4);
//                            return getWebIntent(ss);
//                        }
//                    } else {
                    if (activityName.contains(context.getString(R.string.MainActivity))) {//首页设置
                        HashMap<String, String> urlParam = getUrlParam(activityName);
                        if (CommonUtils.MapNotNull(urlParam)) {
                            if (urlParam.containsKey(StringConstants.INDEX)) {
                                int j = NumberUtils.stringToInt(urlParam.get(StringConstants.INDEX));
                                if (j >= 0) {
                                    in.putExtra(StringConstants.INDEX, j);
                                }
                            }
                            if (urlParam.containsKey(StringConstants.NAV)) {
                                int j = NumberUtils.stringToInt(urlParam.get(StringConstants.NAV));
                                if (j >= 0) {
                                    in.putExtra(StringConstants.NAV, j);
                                }
                            }
                        }
                    }
                    uri = Uri.parse(addParams(new StringBuffer(activityName), params).toString());
//                    }
                } else {
                    uri = getUri(getUrl(getScheme(context), activityName, params));
                }
                if (uri == null) {
                    return null;
                }
                in.setData(uri);
            }
            return in;
        }
        return null;
    }

    public static Intent getIntent(Context context, Uri uri) {
        if (context != null) {
            Intent in = new Intent();
            in.setAction(context.getString(R.string.action));
            in.addCategory(context.getString(R.string.category));
            if (uri == null) {
                return null;
            }
            in.setData(uri);
            return in;
        }
        return null;
    }


    public static String getActivityLaunchName(Context context, Intent in) {
        ActivityInfo activityInfo = getActivityInfo(context, in);
        if (activityInfo != null) {
            String name = activityInfo.name;
            if (CommonUtils.StringNotNull(name)) {
                return name.substring(name.lastIndexOf(".") + 1);
            }
        }
        return "";
    }

    /**
     * 跳转默认浏览器
     *
     * @param context
     * @param url     需要打开的网址
     */
    public static void gotoDefaultWeb(ActivityIntentInterface context, String url) {
        if (CommonUtils.isContainsHttp(url)) {
            if (context != null && context.getActivity() != null) {
                try {
                    context.getActivity().startActivity(getWebIntent(url));
                } catch (Exception exception) {
                    //  context.getActivity():
                    ToastUtils.show("请安装浏览器");
                }
            }
        }
    }

    /**
     * @param url
     * @return
     */
    public static Intent getWebIntent(String url) {
        if (CommonUtils.isContainsHttp(url)) {
            Uri uri = Uri.parse(url);
            return new Intent(Intent.ACTION_VIEW, uri);
        }
        return null;
    }


    /**
     * 获取对应的 Activity信息，多个返回第一个
     *
     * @param context
     * @param in
     * @return
     */
    public static ActivityInfo getActivityInfo(Context context, Intent in) {
        if (context != null && in != null) {
            try {
                PackageManager pm = context.getPackageManager();
                List<ResolveInfo> resolveInfos = pm
                        .queryIntentActivities(in, PackageManager.GET_META_DATA);
                if (resolveInfos != null && resolveInfos.size() > 0) {
                    return resolveInfos.get(0).activityInfo;
                } else {
                    LogUtils.d("找不到host对应Activity");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bundle getActivityBundle(Context context, Intent in) {
        ActivityInfo activityInfo = getActivityInfo(context, in);
        if (activityInfo != null) {
            return activityInfo.metaData;
        }
        return null;
    }

    /**
     * 当前版本是否有此页面
     *
     * @param context
     * @param host    页面host
     * @return
     */
    public static boolean hasThisHost(Context context, String host) {
        return getActivityInfo(context, getIntent(context, host, null)) != null;
    }

    public static Uri getUri(String url) {
        try {
            return Uri.parse(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getUrl(String scheme, String activityName, HashMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(scheme) && !TextUtils.isEmpty(activityName)) {
            sb.append(scheme).append(activityName);
            addParams(sb, params);
        }
        return sb.toString();
    }

    public static StringBuffer addParams(StringBuffer sb, HashMap<String, String> params) {
        if (sb == null) {
            return new StringBuffer();
        }
        if (params != null && params.size() > 0) {
            for (HashMap.Entry<String, String> entry : params.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    sb.append("&").append(entry.getKey()).append("=");
                    if (!TextUtils.isEmpty(entry.getValue())) {
                        String value = entry.getValue();
                        try {
                            value = URLEncoder.encode(value, "UTF-8");
//                            value = BASE64Utils.encode(value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sb.append(value);
                    }

                }

            }
            while (sb.indexOf("?") > 0) {
                int index = sb.indexOf("?");
                sb.replace(index, index + 1, "&");
            }
            int index = sb.indexOf("&");
            sb.replace(index, index + 1, "?");

        }
        return sb;
    }

    public static String getScheme(Context context) {
        if (context != null) {
            return context.getString(R.string.scheme) + "://";
        }
        return null;
    }

    public static HashMap<String, String> getUrlParam(Activity context) {

        if (context != null && context.getIntent() != null) {
            HashMap params = new HashMap();
            Uri uri = context.getIntent().getData();
            if (uri != null && uri.getQueryParameterNames() != null) {
                for (String key : uri.getQueryParameterNames()) {
                    String value = String.valueOf(uri.getQueryParameter(key));
                    try {
                        value = URLDecoder.decode(value, "UTF-8");
//                        value = BASE64Utils.decode(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    params.put(key, value);
                }
            }
            return params;
        }
        return null;
    }

    /**
     * 获取页面的传进来的bundle数据
     *
     * @param context
     * @return
     */
    public static Bundle getBundleParams(Activity context) {
        if (context != null && context.getIntent() != null) {
            return getBundleParams(context, context.getIntent());
        }
        return null;
    }

    /**
     * 获取Intent中的bundle数据
     *
     * @param context
     * @param intent
     * @return
     */
    public static Bundle getBundleParams(Context context, Intent intent) {

        if (context != null && intent != null) {
            return intent.getBundleExtra(context.getString(R.string.jsonBundle));
        }
        return null;
    }


    /**
     * url转map
     *
     * @param url
     * @return
     */
    public static HashMap<String, String> getUrlParam(String url) {
        HashMap<String, String> params = new HashMap();
        if (CommonUtils.StringNotNull(url)) {
            if (url.contains("?")) {
                String[] split = url.split("\\?");
                if (split.length > 1) {
                    url = split[1];
                }
            }
            String[] ss = url.split("&");
            for (int i = 0; i < ss.length; i++) {
                String[] p = ss[i].split("=");
                if (p.length == 2) {
                    params.put(p[0], p[1]);
                }
            }
        }
        return params;
    }

    /**
     * 获取一个保存了Bundle对象的Intent用于setResult 回传bundle对象
     */
    public static Intent getResultBundleIntent(Context context, Bundle bundle) {
        Intent in = new Intent();
        in.putExtra(context.getString(R.string.jsonBundle), bundle);
        return in;
    }

    /**
     * 传入参数为{"key","value"}
     */

    public static HashMap<String, String> getHashObj(String[] params) {
        if (params.length % 2 != 0) {
            LogUtils.d(TAG, "请输入正确的键值对个数");
            return null;
        }
        ArrayList<HashMapEntity> entityList = new ArrayList<>();
        for (int i = 0; i < params.length / 2; i++) {
            HashMapEntity item = new HashMapEntity(params[i * 2],
                    params[i * 2 + 1]);
            entityList.add(item);
        }
        HashMap<String, String> pp = new HashMap<>();
        for (HashMapEntity item : entityList) {
            pp.put(item.getKey(), item.getValue());
        }
        return pp;
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @return
     */
    public static boolean isHaveInstallApp(Context context, String packName) {
        if (TextUtils.isEmpty(packName))
            return false;
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> list = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (packName.equals(list.get(i).packageName)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 跳转web
     *
     * @param url
     * @param title
     */
    public static void startWeb(ActivityIntentInterface context, String url, String title) {
        startWeb(context, url, title, true);
    }

    /**
     * 跳转web
     *
     * @param url
     * @param title
     * @param isInlay 是否跳转内置
     */
    public static void startWeb(ActivityIntentInterface context, String url, String title, boolean isInlay) {
        if (context == null) {
            return;
        }
        String imgURL = CommonUtils.getImgURL(url);

        if (isInlay) {
            context.startClass(MyApp.getMyString(R.string.WebViewActivity), IntentUtils.getHashObj(new String[]{
                    StringConstants.TITLE, title,
                    StringConstants.URL, imgURL}));
        } else {
            IntentUtils.gotoDefaultWeb(context, imgURL);
        }
    }

    /**
     * Banner点击跳转web
     *
     * @param url
     * @param title
     * @param isInlay 是否跳转内置
     */
    public static void startBannerWeb(ActivityIntentInterface context, String url, String title, boolean isInlay) {
        if (context == null || "#".equals(url)) {
            return;
        }
        String imgURL = joinBannerUrl(url);

        if (isInlay) {
            context.startClass(MyApp.getMyString(R.string.WebViewActivity), IntentUtils.getHashObj(new String[]{
                    StringConstants.TITLE, title,
                    StringConstants.URL, imgURL}));
        } else {
            IntentUtils.gotoDefaultWeb(context, imgURL);
        }
    }

    /**
     * Banner中url拼接（单独加上 "/#"）
     *
     * @param url
     */
    private static String joinBannerUrl(String url) {
        if (!CommonUtils.StringNotNull(url)) {
            return "";
        }
        if (!CommonUtils.isContainsHttp(url)) {
            url = BaseNetUtil.HOST + "/#" + url;
        }
        return url;
    }

    public static boolean StartScanResultIntent(Activity activity, String url) {
        boolean success = true;//是否跳转成功
        if (CommonUtils.StringNotNull(url) && url.contains(Config.SCHEME)) {
            String host = PushMessageUtils.getHost(url, activity);
            if (host.equals(Config.SCHEME_HOST) && url.contains(Config.SCHEME_PARAM)) {
                Intent intent = new Intent(activity, ScanDetailActivity.class);
                intent.putExtra(StringConstants.DATA, url);
                activity.startActivity(intent);
            } else if (host.equals(Config.SCHEME_SH_HOST) && url.contains(Config.SCHEME_SH_PARAM)) {
                Intent intent = new Intent(activity, DepositShActivity.class);
                intent.putExtra(StringConstants.DATA, url);
                activity.startActivity(intent);
            } else if (host.equals(UIUtils.getString(R.string.scheme_bind))) {
                Intent intent = new Intent(activity, ScanBindActivity.class);
                intent.putExtra(StringConstants.DATA, url);
                activity.startActivity(intent);
            } else {
//                activity.startActivity(new Intent(activity, MainActivity.class));
                success = false;
            }
//                if (ZERO_STRING.equals(type) && scheme.contains(Config.SCHEME) && scheme.contains(Config.SCHEME_HOST) && scheme.contains(Config.SCHEME_PARAM)) {
//                    Intent intent = new Intent(activity, ScanDetailActivity.class);
//                    intent.putExtra(StringConstants.DATA, scheme);
//                    activity.startActivity(intent);
//                }else if (ONE_STRING.equals(type) && scheme.contains(Config.SCHEME) && scheme.contains(Config.SCHEME_SH_HOST) && scheme.contains(Config.SCHEME_SH_PARAM)) {
//                    Intent intent = new Intent(activity, DepositShActivity.class);
//                    intent.putExtra(StringConstants.DATA, scheme);
//                    activity.startActivity(intent);
//                } else {
//                    activity.startActivity(new Intent(activity, MainActivity.class));
//                }
        } else {
//            activity.startActivity(new Intent(activity, MainActivity.class));
            success = false;
        }
        return success;
    }

}

