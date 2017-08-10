package com.eb.sc.widget;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.eb.sc.R;

/**
 * Created by lyj on 2017/7/31.
 */

public class ScanDialog extends Dialog implements View.OnClickListener{
    private TextView idcard;//号码
    private TextView tCode;//类型
    private TextView submit;
    private LinearLayout xiangmu;
    private Context mContext;
    private String renshu;
    private String mNum;
    private String mCode;
    private OnCloseListener listener;
    private String title;
    private LinearLayout renshu_l;
    private TextView   renshu_t;
    public ScanDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ScanDialog(Context context, int themeResId,String time,String code,String renshu, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.renshu=renshu;
        this.mNum = time;
        this.mCode = code;
        this.listener = listener;
    }

    protected ScanDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ScanDialog setTitle(String title){
        this.title = title;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_dialog);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView(){
        idcard = (TextView)findViewById(R.id.idcard);
        submit = (TextView)findViewById(R.id.submit);
        tCode = (TextView)findViewById(R.id.tCode);//票型
        xiangmu=(LinearLayout) findViewById(R.id.xiangmu);
        renshu_l=(LinearLayout) findViewById(R.id.renshu_l);
        renshu_t=(TextView)findViewById(R.id.renshu);


        submit.setOnClickListener(this);

        if(!TextUtils.isEmpty(mNum)){
            tCode.setText(mNum);
        }

        if(!TextUtils.isEmpty(renshu)){
            renshu_l.setVisibility(View.VISIBLE);
            renshu_t.setText(renshu);
        }else {
            renshu_l.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mCode)){
            xiangmu.setVisibility(View.VISIBLE);
            idcard.setText(mCode);
        }else{
            xiangmu.setVisibility(View.GONE);
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
