package com.eb.sc.offline;/**
 * Created by Administrator on 2017/7/28.
 */

import android.content.Context;

import com.eb.sc.bean.DataInfo;
import com.eb.sc.business.BusinessManager;

import org.aisen.android.component.orm.SqliteUtility;
import org.aisen.android.component.orm.SqliteUtilityBuilder;

import java.util.List;

/**
*
*@author lyj
*@description
*@date 2017/7/28
*/
public class OfflLineDataDb {
    public  static void setup(Context context) {
        new SqliteUtilityBuilder().configDBName("OffLineDB").configVersion(2).build(context);
    }
    public static SqliteUtility getDB() {
        return SqliteUtility.getInstance("OffLineDB");
    }

    //2.存入数据库
    public static void insert(DataInfo bean){
        getDB().insert(null,bean);
    }


    //3.上传刷新本地
    public  static  void updata(DataInfo bean){
        getDB().update(null, bean);
    }

    //删除
    public static void delete(DataInfo info){
        getDB().deleteById(null, DataInfo.class,info.getId());
    }
    //查询所有
    public static List<DataInfo> queryAll() {
        return getDB().select(null, DataInfo.class);
    }
    //shanc 所有
    public static void deleteueryAll() {
         getDB().deleteAll(null, DataInfo.class);
    }

    public static void sysn(String id,String isUp){
        DataInfo a= getDB().selectById(null,DataInfo.class,id);
        if("1".equals(isUp)){
            a.setUp(true);
        }else {
            a.setUp(true);
            a.setpName("无效票");
        }
        updata(a);
    }


}
