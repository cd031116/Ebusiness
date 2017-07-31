package com.eb.sc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class AmendActivity extends BaseActivity {
    @Bind(R.id.top_left)
    LinearLayout top_left;
    @Bind(R.id.top_title)
    TextView top_title;
    @Bind(R.id.top_right_text)
    TextView top_right_text;
    @Bind(R.id.old_psd)
    EditText old_psd;
    @Bind(R.id.new_psd)
    EditText new_psd;
    @Bind(R.id.sure_psd)
    EditText sure_psd;
    @Bind(R.id.submit)
    TextView submit;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_amend;
    }

    @Override
    public void initView() {
        super.initView();
        top_title.setText("修改密码");
    }

    @Override
    public void initData() {
        super.initData();
    }


    @OnClick({R.id.top_left,R.id.submit})
    void onclick(View v){
        switch (v.getId()){
            case R.id.top_left:
                AmendActivity.this.finish();
                break;
            case R.id.submit:

                break;
        }
    }
}
