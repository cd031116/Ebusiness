package com.eb.sc.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
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
      String data= "4001"+she+(str.length()+"")+str;

        return data;
    }

    public static  String getIdcard(Context context,String str){
        BaseConfig bg=new BaseConfig(context);
        String she=  bg.getStringValue(Constants.shebeihao,"");
        String data= "4002"+ she+(str.length()+"")+str;
        return data;
    }

    public static int byte2int(byte[] res) {
// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }
}
