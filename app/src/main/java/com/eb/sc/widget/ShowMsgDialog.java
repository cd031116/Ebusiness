package com.eb.sc.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.utils.SupportMultipleScreensUtil;

/**
 * Created by lyj on 2017/8/2.
 */

public class ShowMsgDialog   extends Dialog implements View.OnClickListener {

    private TextView content;//内容
    private TextView submit;

    private Context mContext;
    private String mName;
    private OnCloseListener listener;
    private String title;

    public ShowMsgDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ShowMsgDialog(Context context, int themeResId, String names,OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.mName = names;
        this.listener = listener;
    }

    protected ShowMsgDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ShowMsgDialog setTitle(String title){
        this.title = title;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_dialog);
        View rootView=findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(mContext);
        SupportMultipleScreensUtil.scale(rootView);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submit.performClick();
            }
        },1000);
    }

    private void initView(){
        content = (TextView)findViewById(R.id.content);
        submit = (TextView)findViewById(R.id.submit);
        submit.setOnClickListener(this);

        content.setText(mName);
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
