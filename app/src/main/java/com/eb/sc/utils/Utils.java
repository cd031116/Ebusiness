package com.eb.sc.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
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


    public static  String getscan(Context context,String msg){
        BaseConfig bg=new BaseConfig(context);
         String she=  bg.getStringValue(Constants.shebeihao,"");//后台给的
        String nr_16=HexStr.str2HexStr(msg);
        String leng_16=HexStr.shiTo16(nr_16.length());
        if (leng_16.length()<=1){
            leng_16="000"+leng_16;
        }else if(leng_16.length()<=2){
            leng_16="00"+leng_16;
        }else if(leng_16.length()<=3){
            leng_16="0"+leng_16;
        }
        String data= "4001"+she+leng_16+nr_16;
        return data;
    }

    public static  String getIdcard(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.shiTo16(nr_16.length());
        if (leng_16.length()<=1){
            leng_16="000"+leng_16;
        }else if(leng_16.length()<=2){
            leng_16="00"+leng_16;
        }else if(leng_16.length()<=3){
            leng_16="0"+leng_16;
        }
        String data= "4002"+ she+leng_16+nr_16;
        return data;
    }

    public static  String getscan_t(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.shiTo16(nr_16.length());
        if (leng_16.length()<=1){
            leng_16="000"+leng_16;
        }else if(leng_16.length()<=2){
            leng_16="00"+leng_16;
        }else if(leng_16.length()<=3){
            leng_16="0"+leng_16;
        }
        String data= "4010"+she+leng_16+nr_16;
        return data;
    }

    public static  String getIdcard_t(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String nr_16=HexStr.str2HexStr(str);
        String leng_16=HexStr.shiTo16(nr_16.length());
        if (leng_16.length()<=1){
            leng_16="000"+leng_16;
        }else if(leng_16.length()<=2){
            leng_16="00"+leng_16;
        }else if(leng_16.length()<=3){
            leng_16="0"+leng_16;
        }
        String data= "4010"+ she+leng_16+nr_16;
        return data;
    }

    public static String getShebeipul(Context context,String msg){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.address,"");
        String str=msg+"&"+she;
        String nr_16=HexStr.str2Hex16(str);
        String leng_16=HexStr.shiTo16(nr_16.length());
        if (leng_16.length()<=1){
            leng_16="000"+leng_16;
        }else if(leng_16.length()<=2){
            leng_16="00"+leng_16;
        }else if(leng_16.length()<=3){
            leng_16="0"+leng_16;
        }
        Log.i("tttt","getShebeipul="+nr_16.length());
        Log.i("tttt","leng_16="+leng_16);
        String data= "40100001"+leng_16+nr_16;
        return data;
    }

//检测是项目list
    public static boolean pullItem(String sty){
        if(TextUtils.isEmpty(sty)){
            return false;
        }
        String  sgs=sty.substring(2,4);
        Log.i("tttt","sgs="+sgs);
            if("11".equals(sgs)){
                return  true;
            }
        return  false;
    }

    //检测是身份证
    public static boolean pullIdCard(String sty){
        if(TextUtils.isEmpty(sty)){
            return false;
        }
        if(sty.length()<12){
            return false;
        }
        String  sgs=sty.substring(2,4);
        if("02".equals(sgs)){
            return  true;
        }
        return  false;
    }
    //检测是二维码
    public static boolean pullScan(String sty){
        if(TextUtils.isEmpty(sty)){
            return false;
        }
        if(sty.length()<12){
            return false;
        }
        String  sgs=sty.substring(2,4);
        if("01".equals(sgs)){
            return  true;
        }
        return  false;
    }

    //检测是同步
    public static boolean pullSync(String sty){
        if(TextUtils.isEmpty(sty)){
            return false;
        }
        if(sty.length()<12){
            return false;
        }
        String  sgs=sty.substring(2,4);
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
