package com.eb.sc.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.utils.AESCipher;
import com.eb.sc.utils.Base64;


import butterknife.Bind;
import butterknife.OnClick;
/*
*
* @author lyj
* @describe  功能入口界面
* @data 2017/7/29
* */


public class CheckActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind( R.id.scan)
    RelativeLayout scan;//检票
    @Bind( R.id.idcard)
    RelativeLayout idScan;//明细
    @Bind(R.id.setting)
    RelativeLayout setting;//设置

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check;
    }

    @Override
    public void initView() {
        super.initView();
        top_left.setVisibility(View.GONE);
        top_title.setText("石燕湖大门核销点");
    }

    @Override
    public void initData() {
        super.initData();
        String abc="";
        try {
             abc= AESCipher.encrypt("1234567891234567","4010000100017A");
            Log.i("tttt","abc="+abc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String abc1= Base64.encode(abc);
        Log.i("tttt","abc1="+abc1);

        String yuan=Base64.decode(abc1);
        Log.i("tttt","yuan="+yuan);

        String yuan1="";
        try {
            yuan1= AESCipher.decrypt("1234567891234567",yuan);
            Log.i("tttt","yuan1="+yuan1);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    @OnClick({R.id.scan, R.id.idcard, R.id.top_right_text,R.id.setting})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.scan:
                startActivity(new Intent(CheckActivity.this, SelectActivity.class));
                break;
            case R.id.idcard:
                startActivity(new Intent(CheckActivity.this, MainActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(CheckActivity.this, SettingActivity.class));
                break;
        }
    }

}
