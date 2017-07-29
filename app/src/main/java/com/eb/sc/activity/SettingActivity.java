package com.eb.sc.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.widget.CommomDialog;
import com.eb.sc.widget.InputDialog;

import org.aisen.android.support.inject.OnClick;
import org.aisen.android.support.inject.ViewInject;

public class SettingActivity extends BaseActivity {
    @ViewInject(id = R.id.top_left)
    LinearLayout top_left;
    @ViewInject(id = R.id.top_title)
    TextView top_title;
    @ViewInject(id = R.id.top_right_text)
    TextView top_right_text;
    @ViewInject(id = R.id.ip_tcp)
    EditText ip_tcp;
    @ViewInject(id = R.id.ip_port)
    EditText ip_port;
    @ViewInject(id = R.id.state)
    EditText state;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("设置");
        top_right_text.setText("保存");
    }

    @Override
    public void initData() {
        super.initData();
      final   BaseConfig bg=BaseConfig.getInstance(this);

        new InputDialog(this, R.style.dialog, "请输入管理员密码？", new InputDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm, String text) {
                if (confirm) {
                    String psd=bg.getStringValue("","");



                } else {
                    SettingActivity.this.finish();
                }

            }
        }).setTitle("提示").show();
    }

    @OnClick({R.id.top_left, R.id.top_right_text})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                SettingActivity.this.finish();
                break;
            case R.id.top_right_text:


                SettingActivity.this.finish();
                break;


        }


    }


}
