package com.kaiserkalep.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.kaiserkalep.R;
import com.kaiserkalep.base.IntentUtils;
import com.kaiserkalep.constants.StringConstants;
import com.kaiserkalep.ui.activity.MainActivity;
import com.kaiserkalep.ui.activity.StartActivity;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 消息推送点击跳转
 *
 * @Auther: Jack
 * @Date: 2019/8/1 0001 13:31
 * @Description:
 */
public class PushMessageUtils {

    /**
     * 主页host
     *
     * @param context
     * @return
     */
    private static String getMainIntentPath(Context context) {
        String s = "";
        if (context != null) {
            s = IntentUtils.getScheme(context) + context.getString(R.string.MainActivity);
        }
        return s;
    }

    /**
     * 带http链接url截取处理
     *
     * @param isBackground
     * @param context
     * @param path
     */
    private static void handleUrlIntent(boolean isBackground, Context context, String path) {
        Intent webIntent = getWebIntent(context, path, null);
        if (webIntent != null) {
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            gotoIntent(isBackground, context, webIntent);
        }
    }

    /**
     * 获取webUrl跳转
     *
     * @param context
     * @param path
     * @return
     */
    public static Intent getWebIntent(Context context, String path, HashMap<String, String> params) {
        String host = getHost(path, context);
        String ss = path.substring(path.indexOf("url") + 4);
        String s1 = "&" + StringConstants.TITLE + "=";
        String title = "";
        if (ss.contains(s1)) {
            String[] split = ss.split(s1);
            if (split.length > 0) {
                ss = split[0];
                title = split[1];
            }
        }
        if (params == null) {
            params = new HashMap<>();
        }
        if (CommonUtils.StringNotNull(host)) {
            params.put(StringConstants.URL, ss);
        }
        if (CommonUtils.StringNotNull(title)) {
            params.put(StringConstants.TITLE, title);
        }
        return IntentUtils.getIntent(context, CommonUtils.StringNotNull(host) ? host : path, params);
    }

    /**
     * 区分前台或后台跳转
     *
     * @param context
     * @param activityIntent
     */
    private static void gotoIntent(boolean isBackground, Context context, Intent activityIntent) {
        if (isBackground || CommonUtils.isAppRunBackground(context)) {
            Intent startIntent = new Intent(context, StartActivity.class);
            startIntent.putExtra(StringConstants.IS_PUSH_MESSAGE, true);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (activityIntent != null) {
                gotoActivitys(context, new Intent[]{mainIntent, activityIntent, startIntent});
            } else {
                gotoActivitys(context, new Intent[]{mainIntent, startIntent});
            }
        } else {
            try {
                setTopApp(context); //如果在后台运行,先切换到前台
            } catch (Exception e) {
                e.printStackTrace();
            }
            gotoActivity(context, activityIntent);
        }
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp(Context context) {
        if (CommonUtils.isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            if (CommonUtils.ListNotNull(taskInfoList)) {
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    /**找到本应用的 task，并将它切换到前台*/
                    if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                        activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                        break;
                    }
                }
            }
        }
    }


    private static Intent getActivityIntent(Context context, String path) {
        try {
//            Uri uri = Uri.parse(path);
            Intent in = IntentUtils.getIntent(context, path, null);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return in;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void gotoActivity(Context context, Intent in) {
        if (in != null) {
            try {
                context.startActivity(in);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("传值异常，无法跳转");
            }
        } else {
            LogUtils.e("传值异常，无法跳转");
        }
    }


    private static void gotoActivitys(Context context, Intent[] intents) {
        if (intents != null) {
            try {
                context.startActivities(intents);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("传值异常，无法跳转");
            }
        } else {
            LogUtils.e("传值异常，无法跳转");
        }
    }


    public static String getHost(String path, Context context) {
        String substring = "";
        if (path.startsWith(IntentUtils.getScheme(context))) {
            //获取host
            if (path.contains("?")) {
                substring = path.substring(path.indexOf("://") + 3, path.indexOf("?"));
            } else {
                substring = path.substring(path.indexOf("://") + 3);
            }
        }
        return substring;
    }
}
