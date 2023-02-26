package com.kaiserkalep.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Locale;

import static com.kaiserkalep.constants.Config.ONE;
import static com.kaiserkalep.constants.Config.TWO;
import static com.kaiserkalep.constants.Config.ZERO;

public class LanguageUtil {

    //语言类型：
    private static final String CHINESE = "zh";
    private static final String ENGLISH = "en";
    private static final String VIETNAMESE = "vi";

    private static HashMap<String, Locale> languagesList = new HashMap<String, Locale>(3) {{
        put(CHINESE, Locale.CHINESE);
        put(ENGLISH, Locale.ENGLISH);
        put(VIETNAMESE, new Locale(VIETNAMESE, "VN"));
    }};
    private static Locale locale;

    /**
     * sp中存储的类型获取语言标识
     *
     * @param languageType
     * @return
     */
    public static String getLanguageMark(int languageType) {
        switch (languageType) {
            case ONE:
                return ENGLISH;
            case TWO:
                return VIETNAMESE;
            default:
                return CHINESE;
        }
    }

    /**
     * sp中存储的类型获取语言对应下标
     *
     * @param language
     * @return
     */
    public static int getLanguageIndex(String language) {
        switch (language) {
            case ENGLISH:
                return ONE;
            case VIETNAMESE:
                return TWO;
            default:
                return ZERO;
        }
    }

    /**
     * SettingActivity修改语言
     *
     * @param context  上下文
     * @param language 例如修改为 英文传“en”，参考上文字符串常量
     * @param cls      要跳转的类（一般为入口类）
     */
    public static void changeAppLanguage(Context context, String language, Class<?> cls) {
        // app locale 默认简体中文
        locale = getLocaleByLanguage(TextUtils.isEmpty(language) ? CHINESE : language);

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        //Android 7.0以上的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            context.getApplicationContext().createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, metrics);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Android 4.1 以上方法
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, metrics);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, metrics);
        }
        // 重启app
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动页更新语言
     *
     * @param context  上下文
     * @param language 例如修改为 英文传“en”，参考上文字符串常量
     */
    public static void upDataAppLanguage(Context context, String language) {
        if (null == locale) {
            // app locale 默认简体中文
            locale = getLocaleByLanguage(TextUtils.isEmpty(language) ? CHINESE : language);
        }

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        //Android 7.0以上的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            context.getApplicationContext().createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, metrics);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Android 4.1 以上方法
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, metrics);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, metrics);
        }
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在
     * 返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     */
    private static Locale getLocaleByLanguage(String language) {
        if (isContainsKeyLanguage(language)) {
            return languagesList.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : languagesList.keySet()) {
                if (TextUtils.equals(languagesList.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.CHINESE;
    }

    /**
     * 如果此映射包含指定键的映射关系，则返回 true
     */
    private static boolean isContainsKeyLanguage(String language) {
        return languagesList.containsKey(language);
    }
}
