package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/10.
 */

public class DetailInfo implements Serializable{
private String name;

    private String num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public DetailInfo(String name, String num) {
        this.name = name;
        this.num = num;
    }
}
