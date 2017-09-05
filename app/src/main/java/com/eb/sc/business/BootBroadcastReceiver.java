package com.eb.sc.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eb.sc.activity.StartActivity;

/**
 * Created by Administrator on 2017/9/5.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("kkkk","kkkkkkkkkkkkkkkkk");
        if (intent.getAction().equals(action_boot)){
            Intent ootStartIntent=new Intent(context,StartActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }

    }
}
