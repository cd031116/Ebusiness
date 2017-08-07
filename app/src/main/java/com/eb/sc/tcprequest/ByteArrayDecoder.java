package com.eb.sc.tcprequest;

import android.util.Log;

import com.eb.sc.utils.Utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Created by LYJ on 2017/8/5.
 */

public class ByteArrayDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        Log.i("ClientSessionHandler", "IoBuffer" + in.toString());
        Log.i("ClientSessionHandler", "IoBuffer3====" + in.getHexDump().toString().replace(" ",""));

            out.write(in.getHexDump().toString().replace(" ",""));
        if(in!=null){
            in.flip();
        }
        return false;//处理成功，让父类进行接收下个包
    }
}
