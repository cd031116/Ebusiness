package com.eb.sc.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/2.
 */

public class isIdNum {
    public static boolean isIdNum(String idNum) {
        // 中国公民身份证格式：长度为15或18位，最后一位可以为字母
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");

        // 格式验证
        if (!idNumPattern.matcher(idNum).matches())
            return false;

        // 合法性验证

        int year = 0;
        int month = 0;
        int day = 0;

        if (idNum.length() == 15) {

            // 一代身份证

            System.out.println("一代身份证：" + idNum);

            // 提取身份证上的前6位以及出生年月日
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{2})(\\d{2})(\\d{2}).*");

            Matcher birthDateMather = birthDatePattern.matcher(idNum);

            if (birthDateMather.find()) {

                year = Integer.valueOf("19" + birthDateMather.group(1));
                month = Integer.valueOf(birthDateMather.group(2));
                day = Integer.valueOf(birthDateMather.group(3));

            }

        } else if (idNum.length() == 18) {

            // 二代身份证

            System.out.println("二代身份证：" + idNum);

            // 提取身份证上的前6位以及出生年月日
            Pattern birthDatePattern = Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");

            Matcher birthDateMather = birthDatePattern.matcher(idNum);

            if (birthDateMather.find()) {

                year = Integer.valueOf(birthDateMather.group(1));
                month = Integer.valueOf(birthDateMather.group(2));
                day = Integer.valueOf(birthDateMather.group(3));
            }

        }

        // 年份判断，100年前至今

        Calendar cal = Calendar.getInstance();

        // 当前年份
        int currentYear = cal.get(Calendar.YEAR);

        if (year <= currentYear - 100 || year > currentYear)
            return false;

        // 月份判断
        if (month < 1 || month > 12)
            return false;

        // 日期判断

        // 计算月份天数

        int dayCount = 31;

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                dayCount = 31;
                break;
            case 2:
                // 2月份判断是否为闰年
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    dayCount = 29;
                    break;
                } else {
                    dayCount = 28;
                    break;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                dayCount = 30;
                break;
        }

        System.out.println(String.format("生日：%d年%d月%d日", year, month, day));

        System.out.println(month + "月份有：" + dayCount + "天");

        if (day < 1 || day > dayCount)
            return false;

        return true;
    }
}
