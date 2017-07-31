package com.eb.sc.sdk.permission;

import android.Manifest;

import org.aisen.android.support.action.IAction;
import org.aisen.android.support.permissions.APermissionsAction;
import org.aisen.android.ui.activity.basic.BaseActivity;

/**
 * Created by lyj on 17/7/29.
 */

public class CallPhonePermissionAction extends APermissionsAction {
    public CallPhonePermissionAction(BaseActivity context, IAction parent) {
        super(context, parent, context.getActivityHelper(), Manifest.permission.CALL_PHONE);
    }

    @Override
    protected void onPermissionDenied(boolean alwaysDenied) {
        if (alwaysDenied) {
            ((BaseActivity) getContext()).showMessage("拨打电话被禁用了，请去设置界面打开此权限");
        }
        else {
            ((BaseActivity) getContext()).showMessage("取消拨打电话授权");
        }
    }
}
