package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by lyj on 2017/8/7.
 */

public class ItemInfo implements Serializable{
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
