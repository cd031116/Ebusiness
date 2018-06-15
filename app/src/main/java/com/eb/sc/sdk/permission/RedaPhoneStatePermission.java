package com.eb.sc.sdk.permission;

import android.Manifest;
import android.util.Log;

import org.aisen.android.support.action.IAction;
import org.aisen.android.support.permissions.APermissionsAction;
import org.aisen.android.ui.activity.basic.BaseActivity;

/**
 * Created by Administrator on 2018/6/15/015.
 */

public class RedaPhoneStatePermission extends APermissionsAction {
    public RedaPhoneStatePermission(BaseActivity context, IAction parent) {
        super(context, parent, context.getActivityHelper(), Manifest.permission.READ_PHONE_STATE);
        Log.i("hhhh","RedaPhoneStatePermission=");
    }

    @Override
    protected void onPermissionDenied(boolean alwaysDenied) {
        Log.i("hhhh","alwaysDenied="+alwaysDenied);
        if (alwaysDenied) {
            ((BaseActivity) getContext()).showMessage("获取手机串码，请去设置界面打开此权限");
        }
        else {
            ((BaseActivity) getContext()).showMessage("取消获取手机串码授权");
        }
    }
}
