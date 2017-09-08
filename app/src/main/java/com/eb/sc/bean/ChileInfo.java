package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/8.
 */

public class ChileInfo implements Serializable{
    private String Name;

    private  String Num;

    private String CheckNum;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public String getCheckNum() {
        return CheckNum;
    }

    public void setCheckNum(String checkNum) {
        CheckNum = checkNum;
    }
}
