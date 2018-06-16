package com.eb.sc.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.offline.SaleDataDb;
import com.eb.sc.scanner.ClientConfig;
import com.eb.sc.utils.AidlUtil;
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
        SaleDataDb.setup(instance);
        ClientConfig.init(getApplicationContext());
        initSDcard();
        AidlUtil.getInstance().connectPrinterService(this);
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