package com.eb.sc.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eb.sc.bean.DataInfo;
import com.eb.sc.bean.ItemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyj on 2017/7/29.
 */

public class Utils {
    //根据code得到项目
    public static String getXiangmu(Context context) {
        BaseConfig bg = new BaseConfig(context);
        String s = bg.getStringValue(Constants.address, "-1");
        String list_item = bg.getStringValue(Constants.px_list, "");
        if (TextUtils.isEmpty(list_item)) {
            return "";
        }
        List<ItemInfo> mList = JSON.parseArray(list_item, ItemInfo.class);
        for (int i = 0; i < mList.size(); i++) {
            if (s.equals(mList.get(i).getCode())) {
                return mList.get(i).getName();
            }
        }
        return "";
    }

    //根据code得到项目
    public static String getPrice(Context context) {
        BaseConfig bg = new BaseConfig(context);
        String s = bg.getStringValue(Constants.address, "-1");
        String list_item = bg.getStringValue(Constants.px_list, "");
        if (TextUtils.isEmpty(list_item)) {
            return "";
        }
        List<ItemInfo> mList = JSON.parseArray(list_item, ItemInfo.class);
        for (int i = 0; i < mList.size(); i++) {
            if (s.equals(mList.get(i).getCode())) {
                return mList.get(i).getPrice();
            }
        }
        return "";
    }





    //根据code得到id
    public static String getItemId(Context context) {
        BaseConfig bg = new BaseConfig(context);
        String s = bg.getStringValue(Constants.address, "-1");
        String list_item = bg.getStringValue(Constants.px_list, "");
        if (TextUtils.isEmpty(list_item)) {
            return "";
        }
        List<ItemInfo> mList = JSON.parseArray(list_item, ItemInfo.class);
        for (int i = 0; i < mList.size(); i++) {
            if (s.equals(mList.get(i).getCode())){
                return mList.get(i).getId();
            }
        }
        return "";
    }


    //获取设备码
    public static String getImui(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }

    public static String getMjScan(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        Log.i("tttt","she="+she);
        String nr_16 = HexStr.str2HexStr(msg);

        String data = "4001" + she  + nr_16;
        return data.toUpperCase();
    }



    public static String getscan(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);

        String data = "4001" + she  + nr_16;
        return data.toUpperCase();
    }

    public static String getIdcard(Context context, String str) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        String nr_16 = HexStr.str2HexStr(str);
        String data = "4002" + she  + nr_16;
        return data.toUpperCase();
    }

    public static String getscan_t(Context context, String str) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        String nr_16 = HexStr.str2HexStr(str);
        String data = "4013" + she  + nr_16;
        return data.toUpperCase();
    }
    //同步 二维码
    public static String getIdcard_t(Context context, String str) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        String nr_16 = HexStr.str2HexStr(str);

        String data = "4012" + she  + nr_16;
        return data.toUpperCase();
    }

   // 票务通同步 二维码
    public static String getscan_t_mj(Context context, String str) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        String nr_16 = HexStr.str2HexStr(str);
        String leng_16 = HexStr.shiTo16(nr_16.length());
        if (leng_16.length() <= 1) {
            leng_16 = "000" + leng_16;
        } else if (leng_16.length() <= 2) {
            leng_16 = "00" + leng_16;
        } else if (leng_16.length() <= 3) {
            leng_16 = "0" + leng_16;
        }
        String data = "4014" + she + leng_16 + nr_16;
        return data.toUpperCase();
    }

    public static String getShebeipul(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.address, "");
        String str = msg + "&" + she;
        String nr_16 = HexStr.str2Hex16(str);
        String data = "40100001"  + nr_16;
        Log.i("tttt", "data=" + data);
        return data;
    }

    //取消订单
    public static String cancelOrder(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4023" + she  + nr_16;
        return data.toUpperCase();
    }

    //发送购买参数
    public static String lunxun(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4022" + she  + nr_16;
        return data.toUpperCase();
    }

    //登录
    public static String ToLogin(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4030" + she  + nr_16;
        return data.toUpperCase();
    }
    //查询
    public static String Toquery(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4024" + she  + nr_16;
        return data.toUpperCase();
    }


    //发送购买参数
    public static String getBuy(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4020" + she  + nr_16;
        return data.toUpperCase();
    }
    //发送收款二维码
    public static String sentBuy(Context context, String msg) {
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");//后台给的
        String nr_16 = HexStr.str2HexStr(msg);
        String data = "4021" + she  + nr_16;
        return data.toUpperCase();
    }

    //检测获取订单
    public static boolean getOrderid(String sty){
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("20".equals(sgs)) {
            return true;
        }
        return false;
    }

    //登录
    public static boolean getLogin(String sty){
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("30".equals(sgs)) {
            return true;
        }
        return false;
    }
    //查询
    public static boolean getQuery(String sty){
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("24".equals(sgs)) {
            return true;
        }
        return false;
    }

    //轮询
    public static boolean getResult(String sty){
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("22".equals(sgs)) {
            return true;
        }
        return false;
    }

    //支付成功
    public static boolean getPay(String sty){
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("21".equals(sgs)) {
            return true;
        }
        return false;
    }

    //检测到的是设备号
    public static boolean pullShebei(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("10".equals(sgs)) {
            return true;
        }
        return false;
    }

    //检测是项目list
    public static boolean pullItem(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        Log.i("tttt", "sgs=" + sgs);
        if ("11".equals(sgs)) {
            return true;
        }
        return false;
    }

    //检测是身份证
    public static boolean pullIdCard(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        if (sty.length() < 12) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        if ("02".equals(sgs)) {
            return true;
        }
        return false;
    }

    //检测是
    public static String pullScan(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return "";
        }
        String sgs = sty.substring(0,2);
        if ("01".equals(sgs)) {
            return "无效票";
        }else if("02".equals(sgs)){
            return "已使用";
        }else if("03".equals(sgs)){
            return "团队票";
        }else if("04".equals(sgs)){
            return "儿童票";
        }else if("05".equals(sgs)){
            return "成人票";
        }else if("06".equals(sgs)){
            return "老年票";
        }else if("07".equals(sgs)){
            return "优惠票";
        }else {
            return "无效票";
        }
    }

    //检测是同步(身份证)
    public static boolean pullShengji(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        if (sty.length() < 12) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        if ("80".equals(sgs)) {
            return true;
        }
        return false;
    }



    //检测是同步(身份证)
    public static boolean pullSync(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        if (sty.length() < 12) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        if ("12".equals(sgs)) {
            return true;
        }
        return false;
    }

    //检测是同步(二维码)
    public static boolean pullscan(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        if (sty.length() < 12) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        if ("13".equals(sgs)) {
            return true;
        }
        return false;
    }
    //检测是同步(二维码) 票务通
    public static boolean pullscanMj(String sty) {
        if (TextUtils.isEmpty(sty)) {
            return false;
        }
        String sgs = sty.substring(2, 4);
        if ("14".equals(sgs)) {
            return true;
        }
        return false;
    }


    //
    public static String pullString(String strs) {
        if (TextUtils.isEmpty(strs)) {
            return "";
        }
        if (strs.length() < 12) {
            return "";
        }
        String arr = strs.substring(12, strs.length());
        return arr;
    }


    public static String xintiao(Context context) {
        //心跳
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        return "4099" + she + "7A";
    }
    public static String shengji(Context context) {
        //升级
        BaseConfig bg = new BaseConfig(context);
        String she = bg.getStringValue(Constants.shebeihao, "");
        return "4080" + she + "7A";
    }

    /**
     * 按照异常批次号对已开单数据进行分组
     * @param billingList
     * @return
     * @throws Exception
     */
    public static  Map<String, List<DataInfo>> groupDataInfo(List<DataInfo> billingList) throws Exception{
        Map<String, List<DataInfo>> resultMap = new HashMap<String, List<DataInfo>>();
        try{
            for(DataInfo tmExcpNew : billingList){

                if(resultMap.containsKey(tmExcpNew.getName())){//map中异常批次已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
                    resultMap.get(tmExcpNew.getName()).add(tmExcpNew);
                }else{//map中不存在，新建key，用来存放数据
                    List<DataInfo> tmpList = new ArrayList<DataInfo>();
                    tmpList.add(tmExcpNew);
                    resultMap.put(tmExcpNew.getName(), tmpList);
                }
            }
        }catch(Exception e){
            throw new Exception("按照异常批次号对已开单数据进行分组时出现异常", e);
        }
        return resultMap;
    }

}
