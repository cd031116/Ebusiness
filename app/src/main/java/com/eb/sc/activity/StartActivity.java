package com.eb.sc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.offline.OfflLineDataDb;
import com.eb.sc.tcprequest.PushManager;
import com.eb.sc.tcprequest.PushService;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;
import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.NetWorkUtils;

import org.aisen.android.network.task.TaskException;
import org.aisen.android.network.task.WorkTask;
import org.aisen.android.support.inject.ViewInject;

import butterknife.Bind;


/*
*开始页面 lyj
* */
public class StartActivity extends BaseActivity {
    @Bind(R.id.start_bg)
    ImageView start_bg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public void initView() {
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(500);
        contentView.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });
    }


    @Override
    public void initData() {
        super.initData();
        BaseConfig bg = new BaseConfig(this);
        if (NetWorkUtils.isNetworkConnected(this)) {
            bg.setStringValue(Constants.havenet, "1");
        } else {
            bg.setStringValue(Constants.havenet, "-1");
        }
        PushManager.getInstance(getApplicationContext()).add();
    }

    private void redirectTo() {
        startActivity(new Intent(StartActivity.this, CheckActivity.class));
        this.finish();
    }



}
