package com.kaiserkalep.utils;

import com.kaiserkalep.R;
import com.kaiserkalep.base.ActivityBase;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * activity栈管理
 *
 * @Auther: Administrator
 * @Date: 2019/3/19 0019 21:18
 * @Description:
 */
public class MyActivityManager {
    private static final String TAG = "MyActivityManager";
    private static ArrayList<ActivityBase> activityStack;
    private static MyActivityManager instance;

    private MyActivityManager() {
    }

    public static MyActivityManager getActivityManager() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    //退出栈顶Activity
    public void popActivity(ActivityBase activity) {
        if (activity == null || activityStack == null) {
            return;
        }
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        //activity.finish;
    }

    /**
     * 迭代不可用
     *
     * @param activity
     * @param b
     */
    public void destoryActivity(ActivityBase activity, boolean b) {
        if (activity == null || activityStack == null) {
            return;
        }
        if (b) {//最上层页面友盟pv退出
            activity.onPause();
        }
        activity.stopUmPage = true;
        activity.finish();
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        activity = null;
    }

    //获得当前栈顶Activity
    public ActivityBase getCurrentActivity() {
        if (activityStack == null || activityStack.size() <= 0) {
            return null;
        }
        return activityStack.get(activityStack.size() - 1);
    }

    //将当前Activity推入栈中
    public void pushActivity(ActivityBase activity) {
        if (activityStack == null) {
            activityStack = new ArrayList<>();
        }
        activityStack.add(activity);
    }

    //退出栈中除指定的Activity外所有
    public void popAllActivityExceptOne(Class cls) {
        popAllActivityExceptMore(cls);
//        int i = 0;
//        while (true) {
//            i++;
//            ActivityBase activity = getCurrentActivity();
//            if (activity == null) {
//                break;
//            }
//            if (activity.getClass().equals(cls)) {
//                break;
//            }
//            destoryActivity(activity, i == 1);
//        }
    }

    /**
     * 退出指定Activity
     *
     * @param cls
     */
    public void exceptActivity(Class... cls) {
        if (activityStack != null && activityStack.size() > 0) {
            Iterator<ActivityBase> iterator = activityStack.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    ActivityBase activity = iterator.next();
                    if (activity != null) {
                        if (cls != null && cls.length > 0) {
                            if (hasClass(activity, cls)) {
                                activity.superFinish();
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 退出指定Activity ,无退出动画
     *
     * @param cls
     */
    public void exceptActivityNotAnimation(Class... cls) {
        if (activityStack != null && activityStack.size() > 0) {
            Iterator<ActivityBase> iterator = activityStack.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    ActivityBase activity = iterator.next();
                    if (activity != null) {
                        if (cls != null && cls.length > 0) {
                            if (hasClass(activity, cls)) {
                                activity.superFinish();
                                activity.overridePendingTransition(R.anim.anim_none, R.anim.anim_none);
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    //退出栈中除指定的Activity外所有
    public void popAllActivityExceptMore(Class... cls) {
        if (activityStack != null && activityStack.size() > 0) {
            Iterator<ActivityBase> iterator = activityStack.iterator();
            if (iterator != null) {
                int i = 0;
                int size = activityStack.size();
                while (iterator.hasNext()) {
                    i++;
                    ActivityBase activity = iterator.next();
                    if (activity != null) {
                        if (cls != null && cls.length > 0) {
                            if (hasClass(activity, cls)) {
                                continue;
                            }
                        }
                        try {
                            if (i == size) {//最上层页面友盟pv退出
                                activity.onPause();
                            }
                            activity.stopUmPage = true;
                            activity.superFinish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void cleanAll() {
        if (activityStack != null && activityStack.size() > 0) {
            Iterator<ActivityBase> iterator = activityStack.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    ActivityBase activity = iterator.next();
                    if (activity != null) {
                        try {
                            activity.superFinish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * 是否包含Activity
     *
     * @param activity
     * @param cls
     * @return
     */
    private boolean hasClass(ActivityBase activity, Class... cls) {
        for (Class cl : cls) {
            if (activity.getClass().equals(cl)) {
                return true;
            }
        }
        return false;
    }

    public int getActivityStackSize() {
        int size = 0;
        if (activityStack != null) {
            size = activityStack.size();
        }
        return size;
    }

    /**
     * 是否包含这个页面
     *
     * @param cls
     * @return
     */
    public boolean isContains(Class cls) {
        if (cls != null && activityStack != null && activityStack.size() > 0) {
            for (ActivityBase activityBase : activityStack) {
                if (activityBase != null && activityBase.getClass().equals(cls)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 由activity类名获取activity
     *
     * @param name
     * @return
     */
    public ActivityBase getActivityByName(String name) {
        if (activityStack != null && activityStack.size() > 0) {
            for (ActivityBase activityBase : activityStack) {
                if (activityBase != null && activityBase.getClass().getSimpleName().equals(name)) {
                    return activityBase;
                }
            }
        }
        return null;
    }
}
