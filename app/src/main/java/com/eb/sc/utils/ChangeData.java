package com.eb.sc.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lyj on 2017/7/29.
 * 日期转换
 */

public class ChangeData {
    //字符串转时间蹉
    public static long StringTolong(String timed) {
        long time = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(timed);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long HaveTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
//        Date now = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
//        String time = dateFormat.format(now);
        String nowtime = yesterday + " 23:59:59";
        long currenttime = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(nowtime);
            currenttime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currenttime;

    }


    public static long getNowtime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        long currenttime = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(str+" 00:00:00");
            currenttime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currenttime;

    }


    public static String cuotoString(String fs){
        if(TextUtils.isEmpty(fs)){
            return "";
        }
        ;
        SimpleDateFormat format =  new SimpleDateFormat("MM-dd HH:mm");
        String d = format.format(Long.parseLong(fs));
        return  d;
    }
}
