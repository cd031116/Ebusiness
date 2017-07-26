package com.eb.sc.sdk.bean;/**
 * Created by Administrator on 2017/7/26.
 */

import java.io.Serializable;

/**
*
*@author lyj
*@description  解析基类
*@date 2017/7/26
*/

public class BaseBean implements Serializable {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
