package com.eb.sc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.utils.ChangeData;
import com.eb.sc.widget.CommomDialog;

import org.aisen.android.support.inject.OnClick;
import org.aisen.android.support.inject.ViewInject;
/*
*
* @author lyj
* @describe  功能入口界面
* @data 2017/7/29
* */


public class CheckActivity extends BaseActivity {
    @ViewInject(id=R.id.top_left)
    LinearLayout top_left;
    @ViewInject(id=R.id.top_title)
    TextView top_title;
    @ViewInject(id=R.id.top_right_text)
    TextView top_right_text;
    @ViewInject(id = R.id.scan)
    LinearLayout scan;
    @ViewInject(id = R.id.idcard)
    LinearLayout idScan;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check;
    }

    @Override
    public void initView() {
        super.initView();
        top_left.setVisibility(View.GONE);
        top_title.setText("选项");
        top_right_text.setText("设置");
    }

    @Override
    public void initData() {
        super.initData();

    }


    @OnClick({R.id.scan, R.id.idcard, R.id.top_right_text})
    void onBuy(View v) {
        switch (v.getId()) {
            case R.id.scan:
                startActivity(new Intent(CheckActivity.this, CaptureActivity.class));
                break;
            case R.id.idcard:
                startActivity(new Intent(CheckActivity.this, MainActivity.class));
                break;
            case R.id.top_right_text:
                startActivity(new Intent(CheckActivity.this, SettingActivity.class));
                break;
        }
    }

}
