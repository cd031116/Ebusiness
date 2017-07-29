package com.eb.sc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;

import org.aisen.android.support.inject.OnClick;
import org.aisen.android.support.inject.ViewInject;

public class CheckActivity extends BaseActivity {
    @ViewInject(id=R.id.scan)
    LinearLayout scan;
    @ViewInject(id=R.id.idcard)
    LinearLayout idScan;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check;
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initData() {
        super.initData();
    }


    @OnClick({R.id.scan,R.id.idcard})
    void onBuy(View v) {
        switch (v.getId()){
            case R.id.scan:
                startActivity(new Intent(CheckActivity.this,CaptureActivity.class));
                break;
            case R.id.idcard:
                startActivity(new Intent(CheckActivity.this,MainActivity.class));
                break;
        }


    }

}
