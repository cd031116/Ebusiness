package com.eb.sc.offline;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.SaleBean;
import com.eb.sc.bean.TicketInfo;

import org.aisen.android.component.orm.SqliteUtility;
import org.aisen.android.component.orm.SqliteUtilityBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyj on 2017/9/6.
 */

public class SaleDataDb{
    public  static void setup(Context context) {
        new SqliteUtilityBuilder().configDBName("SaleDB").configVersion(2).build(context);
    }
    public static SqliteUtility getDB() {
        return SqliteUtility.getInstance("SaleDB");
    }


    //2.存入数据库
    public static void insert(SaleBean bean){
        getDB().insert(null,bean);
    }


    //查询所有
    public static List<SaleBean> queryAll() {
        return getDB().select(null, SaleBean.class);
    }

    //shanc 所有
    public static void deleteAll() {
        getDB().deleteAll(null, SaleBean.class);
    }

    //删除
    public static void delete(SaleBean info){
        getDB().deleteById(null, SaleBean.class,info.getOrderId());
    }

    //1.检查是否存在这张票
    public static boolean isHave(String orderId){
        SaleBean a=getDB().selectById(null,SaleBean.class,orderId);
        if(a==null){
            return false;
        }else{
            return true;//存在
        }
    }

    public static List<SaleBean> querCashAll(){
        List<SaleBean> mList = new ArrayList<>();
        if(mList!=null){
            mList.clear();
        }
        List<SaleBean> data=getDB().select(null, SaleBean.class);
        for(int i=0;i<data.size();i++){
            SaleBean bean = data.get(i);
            if(bean.getState()==0){
                mList.add(bean);
            }
        }
        return  mList;
    }

    public static List<SaleBean> querWAll(){
        List<SaleBean> mList = new ArrayList<>();
        if(mList!=null){
            mList.clear();
        }
        List<SaleBean> data=getDB().select(null, SaleBean.class);
        for(int i=0;i<data.size();i++){
            SaleBean bean = data.get(i);
            if(bean.getState()==3){
                mList.add(bean);
            }
        }
        return  mList;
    }


    public static List<SaleBean> querAAll(){
        List<SaleBean> mList = new ArrayList<>();
        if(mList!=null){
            mList.clear();
        }
        List<SaleBean> data=getDB().select(null, SaleBean.class);
        for(int i=0;i<data.size();i++){
            SaleBean bean = data.get(i);
            if(bean.getState()==2){
                mList.add(bean);
            }
        }
        return  mList;
    }

}
