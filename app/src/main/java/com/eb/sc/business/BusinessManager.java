package com.eb.sc.business;/**
 * Created by Administrator on 2017/7/28.
 */

import android.text.TextUtils;
import android.util.Log;

import com.eb.sc.bean.DataInfo;
import com.eb.sc.offline.OfflLineDataDb;

import java.util.ArrayList;
import java.util.List;

/**
 * created by lyj at 2017-7-28
 */

public class BusinessManager {

    //1.检查是否存在这张票
    public static boolean isHaveScan(String id,int num){
        DataInfo a= OfflLineDataDb.getDB().selectById(null,DataInfo.class,id);
        if(a==null){
            return false;
        }else  if(a.getCanuse()<num){
            Log.i("mmmm","检查是否存在这张票="+a.getCanuse());
            return false;
        }else{
            return true;//存在
        }
    }

    //1.检查是否存在这张票
    public static boolean isHave(String id){
        DataInfo a= OfflLineDataDb.getDB().selectById(null,DataInfo.class,id);
        if(a==null){
            return false;
        }else{
            return true;//存在
        }
    }


    //1.检查是否存在这张票
    public static int isHaveuse(String id,int num){
        DataInfo a= OfflLineDataDb.getDB().selectById(null,DataInfo.class,id);
        if(a==null){
            return 0;//不存在
        }else  if(a.getCanuse()<num){
            Log.i("mmmm","使用过还可以使用=");
            return a.getCanuse();//使用过还可以使用
        }else{
            return -1;//存在 不可以使用
        }
    }



    //未上传的数据
    public static List<DataInfo> querAll(){
        List<DataInfo> mList = new ArrayList<>();
        if(mList!=null){
            mList.clear();
        }
        List<DataInfo> data=OfflLineDataDb.queryAll();
            for(int i=0;i<data.size();i++){
                Log.i("tttt","data="+data.get(i).getpName());
                DataInfo bean = data.get(i);
                if(!bean.isUp()||!TextUtils.isEmpty(data.get(i).getpName())){
                    mList.add(bean);
                }
            }
        return  mList;
    }

    //.上传成功后
    public static void updataUp(DataInfo info){
        info.setUp(true);
        OfflLineDataDb.updata(info);
    }


}
