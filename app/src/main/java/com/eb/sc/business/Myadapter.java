package com.eb.sc.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.bean.SaleInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26/026.
 */

public class Myadapter extends BaseAdapter {

    private List<SaleInfo> mlist;
    private Context mcontext;
    public Myadapter(Context pcontext, List<SaleInfo> plist) {
        this.mcontext = pcontext;
        this.mlist = plist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _layoutinflater=LayoutInflater.from(mcontext);
        convertView=_layoutinflater.inflate(R.layout.spinner_item, null);
        if(convertView!=null)
        {
            TextView _textview=(TextView)convertView.findViewById(R.id.textview);
            _textview.setText(mlist.get(position).getName());
        }
        return convertView;
    }


}
