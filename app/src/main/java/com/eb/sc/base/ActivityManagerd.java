package com.eb.sc.base;

import android.app.Activity;

import java.util.Stack;



/**
 * Created by lyj on 2016/11/29 0029.
 */

public class ActivityManagerd {

    private static Stack<Activity> activityStack;
    private static ActivityManagerd instance;

    private ActivityManagerd() {
    }

    public static ActivityManagerd getScreenManager() {
        if (instance == null) {
            instance = new ActivityManagerd();
        }
        return instance;
    }

    // 退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null && !activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    // 退出栈中所有Activity
    public void popAllActivityExceptOne() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
        finishProgram();
    }


    // 退出栈中所有Activity
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

    // 将指定Activity置顶
    public void pustTopActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        if (!activityStack.contains(activity)){
            activityStack.add(activity);
        }
    }

    public void finishProgram() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
