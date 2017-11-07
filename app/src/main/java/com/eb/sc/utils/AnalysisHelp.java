package com.eb.sc.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.eb.sc.activity.SettingActivity;

/**
 * Created by lyj on 2017/7/29.
 */

public class AnalysisHelp {
    //1------可用
    //2--------过期
    //3-------------票型不符合
    //4-----------梅江
    //判断是否可用
    public static int StringScan(Context context, String str) {
        int a = -1;
        if (TextUtils.isEmpty(str)) {
            return -1;//字符为空
        }

        if (str.length() == 6) {
            return 4;
        }
        String[] strs = str.split("\\&");
        if (strs.length <= 3) {
            return 0;//未检测到票
        }
        if (!TextUtils.isEmpty(strs[1])) {
            Log.i("tttt", "ChangeData.StringTolong(strs[1])=" + ChangeData.StringTolong(strs[1]));
            Log.i("tttt", "ChangeData.HaveTime()=" + ChangeData.HaveTime());
            if (ChangeData.StringTolong(strs[1]) > ChangeData.HaveTime()) {
                boolean gt = false;
                BaseConfig bg = new BaseConfig(context);
                for (int i = 2; i < strs.length - 1; i++) {
                    if (strs[i].equals(Utils.getItemId(context))) {
                        Log.i("tttt", "strs[i]=" + strs[i]);
                        gt = true;
                    }
                }
                if (gt) {
                    return 1;
                } else {
                    return 3;
                }
            } else {
                return 2;
            }

        } else {

            return -1;
        }
    }

    //1------可用
    //2--------过期
    //3-------------票型不符合
    //4-----------梅江
    //判断是否可用
    public static int useScan(Context context, String str) {
        int a = -1;
        if (TextUtils.isEmpty(str)) {
            return -1;//字符为空
        }

        if (str.length() == 6) {
            return 4;
        }
        if (str.length() < 29) {
            return -1;
        }
        //在使用时间内
        if (pastDue(str)) {
                if(havexm(context,str)){//包含项目
                    return  1;
                }else {
                    return 3;
                }
        } else {
            return 2;
        }
    }

    //检测是否有项目
    private static boolean havexm(Context context, String str) {
        String t16 = str.substring(8, 24);
        String t2 = HexStr.t16tot2(t16);
        Log.i("","");
        int xm_id = Integer.parseInt(Utils.getItemCode(context));
        if(t2.substring(xm_id-1,xm_id).equals("1")){
            return  true;
        }else {
            return false;
        }
    }


    //获得人数
    public static int renshu(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        if (str.length() < 27) {
            return 0;
        }
        String renshu = str.substring(24, 28);
        if (TextUtils.isEmpty(renshu)) {
            return 0;
        }
        return Integer.parseInt(renshu, 16);
    }


    // 时间是否没过期
    public static boolean pastDue(String str) {
        String time = "20" + str.substring(2, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
        Log.i("mmmm","time="+time);
        if (ChangeData.StringTolong(time) > ChangeData.HaveTime()) {
            return true;
        }
        return false;
    }


    public static String[] arrayScan(String str) {
        int a = -1;
        if (TextUtils.isEmpty(str)) {
            return null;//字符为空
        }
        String[] strs = str.split("\\&");
        return strs;
    }


    public static String getjieguo(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";//字符为空
        }
        String[] strs = str.split("\\&");
        if (strs.length > 1) {
            return strs[0];
        }
        return "";
    }

    public static String getresylt(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";//字符为空
        }
        String[] strs = str.split("\\&");
        if (strs.length >= 1) {
            return strs[1];
        }
        return "";
    }

    public static String getScanjieguo(String str){
        if (TextUtils.isEmpty(str)) {
            return "";//字符为空
        }

        String result = str.substring(0, str.length() - 2);
        return result;
    }


    public static String getScanresylt(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";//字符为空
        }
        String[] strs = str.split("\\&");
        if (strs.length >= 1) {
            return strs[strs.length - 1];
        }
        return "";
    }


}
