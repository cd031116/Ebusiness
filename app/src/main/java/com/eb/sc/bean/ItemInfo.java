package com.eb.sc.bean;

import java.io.Serializable;

/**
 * Created by lyj on 2017/8/7.
 */

public class ItemInfo implements Serializable{
    private String Id;
    private String Name;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
