package com.eb.sc.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.utils.SupportMultipleScreensUtil;

/*
*
* @author lyj
* @describe 登录弹出框
* @data 2017/11/10
* */


public class LogDialog extends Dialog  implements View.OnClickListener{

    private Context mContext;
    private OnCloseListener listener;
    private String title;
    private String positiveName;

    private TextView title_t;

    private EditText account;

    private EditText pasword;

    private TextView submit;

    private TextView cancel;
    public LogDialog(Context context) {
        super(context);
        this.mContext = context;
    }
    public LogDialog(Context context, String content) {
        super(context, R.style.dialog);
        this.mContext = context;
    }

    public LogDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
    }

    public LogDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected LogDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public LogDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public LogDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        View rootView=findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(mContext);
        SupportMultipleScreensUtil.scale(rootView);
        setCanceledOnTouchOutside(false);
//        setCancelable(false);
        initView();
    }

    private void initView(){
        title_t = (TextView)findViewById(R.id.title);

        account = (EditText) findViewById(R.id.account);
        pasword = (EditText)findViewById(R.id.pasword);
        submit= (TextView)findViewById(R.id.submit);
        cancel= (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if(listener != null){
                    String acco=account.getText().toString();
                    String psd=pasword.getText().toString();
                    if(TextUtils.isEmpty(acco)){
                        Toast.makeText(mContext,"请输入账号",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(TextUtils.isEmpty(psd)){
                        Toast.makeText(mContext,"请输入密码",Toast.LENGTH_SHORT).show();
                        break;
                    }

                    listener.onClick(this, true,acco,psd);
                }
                break;
            case R.id.cancel:
                listener.onClick(this, false,"","");
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm,String text,String pasd);
    }
}
