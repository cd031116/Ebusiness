package com.eb.sc.business;

/*
*
* @author lyj
* @describe  查询适配器
* @data 2017/9/8
* */


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eb.sc.R;
import com.eb.sc.bean.ChileInfo;
import com.eb.sc.bean.GroupInfo;
import com.eb.sc.utils.SupportMultipleScreensUtil;

import java.util.ArrayList;
import java.util.List;

public class QueryAdapter extends BaseExpandableListAdapter {
    private Context mcontext;
    private List<GroupInfo>  mGroup=new ArrayList<>();
    public QueryAdapter(Context context,List<GroupInfo>  mGroup){
        this.mcontext=context;
        this.mGroup=mGroup;
    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroup.get(groupPosition).getTickets().size()+1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroup.get(groupPosition).getTickets();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view=null;
        GroupHolder holder = null;
        if(convertView!=null){
            view = convertView;
           holder = (GroupHolder) view.getTag();
        }else{
            view = View.inflate(mcontext,R.layout.group_item, null);
            SupportMultipleScreensUtil.scale(view);
            holder =new GroupHolder();
            holder.order_id= (TextView) view.findViewById(R.id.order_id);
            holder.phone= (TextView) view.findViewById(R.id.phone);
            holder.image= (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        }
        if(isExpanded){
            holder.image.setImageResource(R.drawable.zhedie);
        }else {
            holder.image.setImageResource(R.drawable.zhankai);
        }



        holder.order_id.setText(mGroup.get(groupPosition).getOrderSn());
        holder.phone.setText(mGroup.get(groupPosition).getPhone());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view=null;
        ChildHolder childholder = null;
        if(convertView!=null){
            view = convertView;
            childholder = (ChildHolder) view.getTag();
        }else{
            view = View.inflate(mcontext,R.layout.child_item, null);
            SupportMultipleScreensUtil.scale(view);
            childholder = new ChildHolder();
            childholder.name_t = (TextView) view.findViewById(R.id.name_t);
            childholder.num_t = (TextView) view.findViewById(R.id.num_t);
            childholder.state = (TextView) view.findViewById(R.id.state);
            view.setTag(childholder);
        }
        if(childPosition==0){
            childholder.name_t.setText("景点");
            childholder.num_t.setText("核销人数");
            childholder.state.setText("核销状态");
            childholder.name_t.setTextColor(Color.parseColor("#7BB4E8"));
            childholder.num_t.setTextColor(Color.parseColor("#7BB4E8"));
            childholder.state.setTextColor(Color.parseColor("#7BB4E8"));
        }else{
            childholder.name_t.setTextColor(Color.parseColor("#333333"));
        childholder.num_t.setTextColor(Color.parseColor("#333333"));
        childholder.state.setTextColor(Color.parseColor("#333333"));
        childholder.name_t.setText(mGroup.get(groupPosition).getTickets().get(childPosition-1).getName());
        childholder.num_t.setText(mGroup.get(groupPosition).getTickets().get(childPosition-1).getNum());

        if("1".equals(mGroup.get(groupPosition).getTickets().get(childPosition-1).getCheckNum())){
            childholder.state.setText("已核销");
            childholder.state.setTextColor(Color.parseColor("#3DAF5D"));
        }else {
            childholder.state.setText("未核销");
            childholder.state.setTextColor(Color.parseColor("#EF403B"));
        }
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupHolder{
        TextView order_id;
        TextView phone;
        ImageView image;
    }

    static class ChildHolder{
        TextView name_t;
        TextView num_t;
        TextView state;
    }
}
