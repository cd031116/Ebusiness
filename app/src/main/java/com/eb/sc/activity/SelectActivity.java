package com.eb.sc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.MainActivity;
import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;


import butterknife.Bind;
import butterknife.OnClick;

public class SelectActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.idcard)
    RelativeLayout idcard;
    @Bind(R.id.scan)
    RelativeLayout scan;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("扫描检票");
    }

    @Override
    public void initData() {
        super.initData();
    }

    @OnClick({R.id.idcard,R.id.scan,R.id.top_left})
    void onclick(View v){
        switch (v.getId()){
            case R.id.idcard:
                startActivity(new Intent(SelectActivity.this,CaptureActivity.class));
                break;
            case R.id.scan:
                startActivity(new Intent(SelectActivity.this,MainActivity.class));
                break;
            case R.id.top_left:
                SelectActivity.this.finish();
                break;
        }
    }
}
