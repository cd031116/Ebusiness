package com.eb.sc.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by lyj on 2017/7/29.
 */

public class Utils {
    //获取设备码
    public static String getImui(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }


    public static  String getscan(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
         String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.str2HexStr(nr_16.length()+"");
        String data= "4001"+she+leng_16+nr_16;
        return data;
    }

    public static  String getIdcard(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.str2HexStr(nr_16.length()+"");
        String data= "4002"+ she+leng_16+nr_16;
        return data;
    }

    public static  String getscan_t(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.str2HexStr(nr_16.length()+"");
        String data= "4010"+she+leng_16+nr_16;
        return data;
    }

    public static  String getIdcard_t(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.str2HexStr(nr_16.length()+"");
        String data= "4010"+ she+leng_16+nr_16;
        return data;
    }


    public static boolean pullItem(String sty){
        if(TextUtils.isEmpty(sty)){
            return false;
        }
        String  sgs=sty.substring(3,5);
            if("11".equals(sgs)){
                return  true;
            }

        return  false;
    }

    //
    public static  String pullString(String strs){
        if(TextUtils.isEmpty(strs)){
           return "";
        }
        if(strs.length()<12){
            return "";
        }
        String arr=strs.substring(12,strs.length());
        return  arr;
    }









}
