package com.eb.sc.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.utils.SupportMultipleScreensUtil;

/**
 * Created by Administrator on 2017/8/10.
 */

public class RestartDialog extends Dialog implements View.OnClickListener  {
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;
    private EditText in_put;
    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public RestartDialog(Context context) {
        super(context);
        this.mContext = context;
    }
    public RestartDialog(Context context, String content) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.content = content;
    }

    public RestartDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public RestartDialog(Context context, int themeResId, String content, OnCloseListener
            listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected RestartDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public RestartDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public RestartDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public RestartDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_restart);
        View rootView=findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(mContext);
        SupportMultipleScreensUtil.scale(rootView);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView(){
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        submitTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);
        in_put= (EditText) findViewById(R.id.in_put);
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                listener.onClick(this, true,"");
                break;
            case R.id.cancel:
                listener.onClick(this, false,"");
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm,String text);
    }
}
