package com.eb.sc.offline;/**
 * Created by Administrator on 2017/7/28.
 */

import android.content.Context;

import com.eb.sc.bean.DataInfo;

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
    static void setup(Context context) {
        new SqliteUtilityBuilder().configDBName("OffLineDB").configVersion(1).build(context);
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
    public void delete(DataInfo info){
        getDB().deleteById(null, DataInfo.class,info.getId());
    }
    //查询所有
    public static List<DataInfo> queryAll() {
        return getDB().select(null, DataInfo.class);
    }

}
