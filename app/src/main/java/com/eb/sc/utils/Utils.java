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


}
