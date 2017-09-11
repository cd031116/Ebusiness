package com.eb.sc.tcprequest;

import android.util.Log;

import com.eb.sc.utils.HexStr;
import com.eb.sc.utils.Utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * Created by LYJ on 2017/8/5.
 */

public class ByteArrayDecoder extends CumulativeProtocolDecoder {


    @Override
    protected boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
        if(buf.remaining() > 0) {
            String getmsg = buf.getHexDump().toString().replace(" ", "");
            buf.mark();
            buf.reset();
            if (getmsg.startsWith("4022")) {
                String getbody = HexStr.hexStr2Str((getmsg.toString()).substring(8, getmsg.toString().length()));
                if (getbody.length() < 114) {

                    return false;
                }else {
                    out.write(buf.getHexDump().toString().replace(" ", ""));
                    Log.i("ClientSessionHandler", "IoBuffer1====" + buf.getHexDump().toString().replace(" ", ""));
                    if(buf.remaining() > 0){
                        return true;
                    }
                    if (buf != null) {
                        buf.flip();
                    }
                }
            }else if(getmsg.startsWith("4024")||getmsg.startsWith("4011")){
                String getbody = HexStr.hexStr2Str((getmsg.toString()).substring(8, getmsg.toString().length()));
                if(!getbody.endsWith("&")){
                    return false;
                }else {
                    String thisjieguo=buf.getHexDump().toString().replace(" ", "");
                    out.write(thisjieguo.substring(0,thisjieguo.length()-1));
                    Log.i("ClientSessionHandler", "IoBuffer1====" + buf.getHexDump().toString().replace(" ", ""));
                    if(buf.remaining() > 0){
                        return true;
                    }
                    if (buf != null) {
                        buf.flip();
                    }
                }
            }else {
                out.write(buf.getHexDump().toString().replace(" ", ""));
                Log.i("ClientSessionHandler", "IoBuffer2====" + buf.getHexDump().toString().replace(" ", ""));
                if(buf.remaining() > 0){
                    return true;
                }
                if (buf != null) {
                    buf.flip();
                }
            }
        }
        return false;//处理成功，让父类进行接收下个包
    }
}
