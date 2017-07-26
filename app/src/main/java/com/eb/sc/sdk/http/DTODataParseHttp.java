package com.eb.sc.sdk.http;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eb.sc.sdk.bean.BaseBean;

import org.aisen.android.network.http.DefHttpUtility;
import org.aisen.android.network.task.TaskException;

/**
*
*@author lyj
*@description  将服务端的接口数据中的Data包装体转换成DTO
*@date 2017/7/26
*/
public class DTODataParseHttp extends DefHttpUtility {
      private Activity context;
    public DTODataParseHttp(Activity context){
        this.context=context;
    }
    @Override
    protected <T> T parseResponse(String resultStr, Class<T> responseCls) throws TaskException {
        try {
            JSONObject jsonObject = JSON.parseObject(resultStr);
            Log.i("tttt","resultStr="+resultStr);
            String code=jsonObject.getString("code");
            if(TextUtils.isEmpty(code)){
                code=jsonObject.getString("retCode");
            }
            T result;
            //单点登录
            if (!code.equals("0")&&!code.equals("3")) {
                throw new TaskException(jsonObject.getString("code"), jsonObject.getString("message"));
            }
             BaseBean bean = null;
              if(code.equals("3")){
                  result = super.parseResponse(resultStr, responseCls);
              }  else {
                  if (jsonObject.containsKey("data")) {
                      result = super.parseResponse(jsonObject.getString("data"), responseCls);
                      if (result instanceof BaseBean) {
                          bean = (BaseBean) result;
                      }
                  } else {
                      bean = new BaseBean();
                      result = (T) bean;
                  }
                  if (bean != null) {
                      bean.setCode(jsonObject.getString("code"));
                      bean.setMessage(jsonObject.getString("message"));
                  }
              }
            return result;

        } catch (Throwable e) {
            if (e instanceof TaskException) {
                throw e;
            }
            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
    }
}
