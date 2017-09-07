package com.eb.sc.sdk.eventbus;

/**
 * Created by Administrator on 2017/9/7.
 */

public class QueryEvent {
    private String datas;

    public String getDatas() {
        return datas;
    }

    public  QueryEvent(String datas) {
        this.datas = datas;
    }
}
