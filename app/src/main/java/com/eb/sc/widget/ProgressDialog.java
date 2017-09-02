package com.eb.sc.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.eb.sc.R;

/**
 * Created by Administrator on 2017/9/2.
 */

public class ProgressDialog extends Dialog {

    private TextView tInfo;

    public ProgressDialog(Context context, boolean isCancel) {
        super(context, R.style.progress_dialog);
        setCanceledOnTouchOutside(false);
        if(!isCancel){
            setCancelable(false);
        }
        View v = View.inflate(context, R.layout.dialog_custom, null);
        tInfo = (TextView) v.findViewById(R.id.info);
        setContentView(v);
    }

    public void showText(String txt) {
        if (txt == null || "".equals(txt)) tInfo.setVisibility(View.GONE);
        else {
            tInfo.setText(txt);
            tInfo.setVisibility(View.VISIBLE);
        }
    }
}
