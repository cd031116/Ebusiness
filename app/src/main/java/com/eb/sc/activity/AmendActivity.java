package com.eb.sc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.base.BaseActivity;
import com.eb.sc.utils.BaseConfig;
import com.eb.sc.utils.Constants;

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


    @OnClick({R.id.top_left, R.id.submit,R.id.close_bg})
    void onclick(View v) {
        BaseConfig bg = new BaseConfig(this);
        switch (v.getId()) {
            case R.id.top_left:
                AmendActivity.this.finish();
                break;
            case R.id.submit:
                String opsd = old_psd.getText().toString();
                String psd = bg.getStringValue(Constants.admin_word, "");
                if (TextUtils.isEmpty(opsd)) {
                    Toast.makeText(AmendActivity.this, "请输入原密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!opsd.equals(psd)) {
                    Toast.makeText(AmendActivity.this, "您输入原密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                String npsd = new_psd.getText().toString();
                String tnpsd = sure_psd.getText().toString();
                if (TextUtils.isEmpty(npsd)) {
                    Toast.makeText(AmendActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tnpsd)) {
                    Toast.makeText(AmendActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!npsd.equals(tnpsd)) {
                    Toast.makeText(AmendActivity.this, "您两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                bg.setStringValue(Constants.admin_word, tnpsd);
                Toast.makeText(AmendActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                AmendActivity.this.finish();
                break;

            case R.id.close_bg:
                ExitDialog();
                break;
        }
    }
}
