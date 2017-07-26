package com.eb.sc.sdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.eb.sc.BuildConfig;
import com.eb.sc.sdk.http.DTODataParseHttp;

import org.aisen.android.common.setting.Setting;
import org.aisen.android.network.biz.ABizLogic;
import org.aisen.android.network.http.HttpConfig;
import org.aisen.android.network.http.IHttpUtility;
import org.aisen.android.network.http.Params;
import org.aisen.android.network.task.TaskException;
import org.json.JSONObject;

import java.util.Map;

/**
*
*@author lyj
*@description 服务端接口api
*@date 2017/7/26
*/

public class SDK extends ABizLogic {
    private static Activity context;

    private SDK() {
        this(CacheMode.disable);
    }

    private SDK(CacheMode mode) {
        super(mode);
    }

    public static SDK newInstance(Activity context) {
        SDK.context=context;
        return newInstance(CacheMode.disable);
    }

    public static SDK newInstance(CacheMode mode) {
        return new SDK(mode);
    }

    /**
     * 封装基础参数
     * @param paramsJson
     * @return
     */
    public JSONObject getBasicParams(JSONObject paramsJson){
        try{
            Params basicParams = basicParams(null);
            for (String key : basicParams.getKeys()) {
                paramsJson.put(key, basicParams.getParameter(key));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return paramsJson;
    }


//    /**
//     * 查询评论列表
//     *
//     * @param bProductId //产品id
//     * @return
//     * @throws TaskException
//     */
//    public CommentListBean queryComment(String bProductId) throws TaskException {
//        Setting action = newSetting("queryComment", "/bProduct/comment", "查询评论列表");
//        Params params = new Params();
//        params.addParameter("bProductId", bProductId);
//        // 测试接口
////        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TestCommentListAPI.class.getName(), ""));
//        return doGet(action, basicParams(params), CommentListBean.class);
//    }


    @Override
    protected IHttpUtility configHttpUtility(){
        return new DTODataParseHttp(context);
    }

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig config = new HttpConfig();
        // 服务端请求地址
        config.baseUrl = BuildConfig.BASE_URL;
//        http://192.168.10.115:9100
        config.addHeader("Content-Type", "application/json");
        return config;
    }

    // 服务端参数基础封装
    private Params basicParams(Params params) {
        if (params == null) {
            params = new Params();
        }
        params.addParameter("version",BuildConfig.VERSION_NAME);
        return params;
    }

}
