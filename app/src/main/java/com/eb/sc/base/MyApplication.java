package com.eb.sc.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.scanner.ClientConfig;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.ClientGlobal;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.FileUtil;

import org.aisen.android.common.context.GlobalContext;

/**
 * Created by wangdan on 17/2/11.
 */

public class MyApplication extends GlobalContext{
    public static MyApplication instance;
    private ActivityManagerd activityManager = null;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        activityManager = ActivityManagerd.getScreenManager();
        OfflLineDataDb.setup(instance);
        ClientConfig.init(getApplicationContext());
        initSDcard();
        initDefaultValue();
    }

    private void initDefaultValue() {
        if(!ClientConfig.hasValue(ClientConfig.OPEN_SCAN)){
            ClientConfig.setValue(ClientConfig.OPEN_SCAN, true);
        }

        if(!ClientConfig.hasValue(ClientConfig.DATA_APPEND_ENTER)){
            ClientConfig.setValue(ClientConfig.DATA_APPEND_ENTER, true);
        }

        if(!ClientConfig.hasValue(ClientConfig.APPEND_RINGTONE)){
            ClientConfig.setValue(ClientConfig.APPEND_RINGTONE, true);
        }

        if(!ClientConfig.hasValue(ClientConfig.APPEND_VIBRATE)){
            ClientConfig.setValue(ClientConfig.APPEND_VIBRATE, true);
        }

        if(!ClientConfig.hasValue(ClientConfig.CONTINUE_SCAN)){
            ClientConfig.setValue(ClientConfig.CONTINUE_SCAN, false);
        }

        if(!ClientConfig.hasValue(ClientConfig.SCAN_REPEAT)){
            ClientConfig.setValue(ClientConfig.SCAN_REPEAT, false);
        }

        if(!ClientConfig.hasValue(ClientConfig.SCAN_REPEAT)){
            ClientConfig.setValue(ClientConfig.SCAN_REPEAT, false);
        }

    }


    public static MyApplication getInstance(){
        return instance;
    }

    public ActivityManagerd getActivityManager(){
        return activityManager;
    }

    public boolean isPublic(){

        return true;
    }

    private void initSDcard() {
        FileUtil.createDirIfNotExist(ClientGlobal.Path.ClientDir);
    }
    /**
     * d
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }


    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }
}