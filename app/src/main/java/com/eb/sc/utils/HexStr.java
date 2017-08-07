package com.eb.sc.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lyj on 2017/8/2.
 */

public class HexStr {
    /**
     * 字符串转换成十六进制字符串
     *
     * @param // 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String strPart) {
        String str = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    private static String hexString = "0123456789ABCDEF";
    /**
     * 十六进制转换字符串
     *
     * @param (//Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
        public static String hexStr2Str(String datas) {
            if (datas == null || datas.equals("")) {
                return null;
            }
            datas = datas.replace(" ", "");
            byte[] baKeyword = new byte[datas.length() / 2];
            for (int i = 0; i < baKeyword.length; i++) {
                try {
                    baKeyword[i] = (byte) (0xff & Integer.parseInt(
                            datas.substring(i * 2, i * 2 + 2), 16));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                datas = new String(baKeyword, "gbk");
                new String();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return datas;
        }



    /**
     * bytes转换成十六进制字符串
     *
     * @param
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param strText 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    public static String strToUnicode(String strText)
            throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * @param //16进制字符串
     * @return 字节数组
     * @throws
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     */
    public static byte[] hex2byte(String hexString) {
//        if (hexString == null || hexString.equals("")) {
//            return null;
//        }
//        //  hexString = hexString.toUpperCase();
//        int length = hexString.length() / 2;
//        char[] hexChars = hexString.toCharArray();
//        byte[] d = new byte[length];
//        for (int i = 0; i < length; i++) {
//            int pos = i * 2;
//            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
//            if (d[i]> 127){
//                d[i] = (byte)(d[i] - 256);
//            }
//        }
//        return d;
          /*对输入值进行规范化整理*/
        hexString = hexString.trim().replace(" ", "").toUpperCase(Locale.US);
        //处理值初始化
        int m=0,n=0;
        int iLen=hexString.length()/2; //计算长度
        byte[] ret = new byte[iLen]; //分配存储空间

        for (int i = 0; i < iLen; i++){
            m=i*2+1;
            n=m+1;
            ret[i] = (byte)(Integer.decode("0x"+ hexString.substring(i*2, m) + hexString.substring(m,n)) & 0xFF);
        }
        return ret;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c & 0xffff);
    }

    /**
     * @param src
     * @return
     * @throws
     * @Title:char2Byte
     * @Description:字符转成字节数据char-->integer-->byte
     */
    public static Byte char2Byte(Character src) {
        return Integer.valueOf((int) src).byteValue();
    }

    public static byte[] charsToBytes(char[] src) {
        CharBuffer charBuffer = CharBuffer.allocate(src.length);
        charBuffer.put(src);
        charBuffer.flip();
        Charset cs = Charset.forName("ASCII");
        System.out.println(cs.name());
        ByteBuffer byteBuffer = cs.encode(charBuffer);
        return byteBuffer.array();
    }

    //   ------------------------------------------------
   /* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
            * @param src byte[] data
 * @return hex string
 */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByted(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public static byte[] HexStringToBytes(String hexStr) {
        if (TextUtils.isEmpty(hexStr)) {
            return new byte[0];
        }

        if (hexStr.startsWith("0x")) {
//            hexStr = hexStr.Remove(0, 2);
            hexStr = hexStr.substring(2, hexStr.length());
        }

        int count = hexStr.length();

        if (count % 2 == 1) {
//            throw new ArgumentException("Invalid length of bytes:" + count);
        }

        int byteCount = count / 2;
        byte[] result = new byte[byteCount];
        for (int ii = 0; ii < byteCount; ++ii) {
//            String tempBytes = Byte.parseByte(hexStr.substring(2 * ii,  2), System.Globalization.NumberStyles.HexNumber);
//            result[ii] = tempBytes;
        }
        return result;
    }


    public static byte[] hexToByte(String hexString) {
//        String digital = "0123456789ABCDEF";
//        char[] hex2char = hexString.toCharArray();
//        byte[] bytes = new byte[hexString.length() / 2];
//        int temp;
//        for (int i = 0; i < bytes.length; i++) {
//            // 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了
//            // 然后和低四位相加， 相当于 位操作"|"
//            //相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}
//            temp = digital.indexOf(hex2char[2 * i]) * 16;
//            temp += digital.indexOf(hex2char[2 * i + 1]);
//            bytes[i] = (byte) (temp & 0xff);
//        }
//        return bytes;
            byte[] b = new byte[hexString.length() / 2];
            int j = 0;
            for (int i = 0; i < b.length; i++) {
                char c0 = hexString.charAt(j++);
                char c1 = hexString.charAt(j++);
                b[i] = (byte) ((parse(c0) << 4) | parse(c1));
            }
            return b;
        }
    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }
}
