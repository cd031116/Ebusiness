package com.eb.sc.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.ip_tcp)
    EditText ip_tcp;
    @Bind(R.id.ip_port)
    EditText ip_port;
    @Bind(R.id.state)
    EditText state;
    @Bind(R.id.amend)
    RelativeLayout amend;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("设置");
    }

    @Override
    public void initData() {
        super.initData();

    }

    @OnClick({R.id.top_left, R.id.top_right_text,R.id.amend})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SettingActivity.this.finish();
                break;
            case R.id.top_right_text:

                SettingActivity.this.finish();
                break;
            case R.id.amend:
               startActivity(new Intent(SettingActivity.this,AmendActivity.class));
                break;

        }
    }


}
