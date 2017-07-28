package com.eb.sc.offline;/**
 * Created by Administrator on 2017/7/28.
 */

import android.content.Context;

import org.aisen.android.component.orm.SqliteUtility;
import org.aisen.android.component.orm.SqliteUtilityBuilder;

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
    static SqliteUtility getDB() {
        return SqliteUtility.getInstance("OffLineDB");
    }
}
