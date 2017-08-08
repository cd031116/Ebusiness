package com.eb.sc.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eb.sc.R;

/*
*
* @author lyj
* @describe  弹出框
* @data 2017/7/29
* */


public class CommomDialog extends Dialog implements View.OnClickListener{
    private TextView tName;//票型
    private TextView idcard;//号码(二维码 身份证)
    private TextView tCode;//项目
    private TextView submit;
    private LinearLayout xiangmu;
    private Context mContext;
    private String mName;//票型
    private String mNum;
    private String mCode;//项目
    private OnCloseListener listener;
    private String title;

    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommomDialog(Context context, int themeResId, String names,String num,String code, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.mName = names;
        this.mNum = num;
        this.mCode = code;
        this.listener = listener;
    }

    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommomDialog setTitle(String title){
        this.title = title;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_commom);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView(){
        tName = (TextView)findViewById(R.id.name);
        idcard = (TextView)findViewById(R.id.idcard);
        submit = (TextView)findViewById(R.id.submit);
        tCode = (TextView)findViewById(R.id.tCode);
        xiangmu= (LinearLayout) findViewById(R.id.xiangmu);
        submit.setOnClickListener(this);

        if(!TextUtils.isEmpty(mName)){
            tName.setText(mName);
            xiangmu.setVisibility(View.VISIBLE);
        }else {
            xiangmu.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(mCode)){
            tCode.setText(mCode);
        }
        if(!TextUtils.isEmpty(mNum)){
            idcard.setText(mNum);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
