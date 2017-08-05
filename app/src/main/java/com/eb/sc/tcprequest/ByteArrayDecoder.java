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
        Log.i("ClientSessionHandler", "IoBuffer3" + in.getHexDump().toString().replace(" ",""));

            out.write(in.getHexDump().toString().replace(" ",""));

//        byte[] b = new byte[in.limit()];
//        in.get(b);
//        //此处用stringbuffer是因为　String类是字符串常量，是不可更改的常量。而StringBuffer是字符串变量，它的对象是可以扩充和修改的。
//        StringBuffer stringBuffer = new StringBuffer();
//        for (int i = 0; i < b.length; i++) {
//            stringBuffer.append((Byte) b[i]); //可以根据需要自己改变类型
//        }
//        Log.i("ClientSessionHandler", "IoBuffer111" + stringBuffer.toString());
//        byte[] bt = stringBuffer.toString().getBytes();
//
//        IoBuffer ioBuffer = IoBuffer.allocate(bt.length);
//        ioBuffer.put(bt, 0, bt.length);
//        ioBuffer.flip();
//
//        out.write(bt);
//
//        Log.i("ClientSessionHandler", "IoBuffer111" + bt.toString());
//            return stringBuffer.toString();
//        if(in.remaining() > 4){//前4字节是包头
//            //标记当前position的快照标记mark，以便后继的reset操作能恢复position位置
//            in.mark();
//            byte[] l = new byte[4];
//            in.get(l);
//
//            //包体数据长度
//            int len = Utils.byte2int(l);//将byte转成int
//
//            //注意上面的get操作会导致下面的remaining()值发生变化
//            if(in.remaining() < len){
//                //如果消息内容不够，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据
//                in.reset();
//                return false;
//            }else{
//                //消息内容足够
//                in.reset();//重置恢复position位置到操作前
//                int sumlen = 4+len;//总长 = 包头+包体
//                byte[] packArr = new byte[sumlen];
//                in.get(packArr, 0 , sumlen);
//
//                IoBuffer buffer = IoBuffer.allocate(sumlen);
//                buffer.put(packArr);
//                buffer.flip();
//                out.write(buffer);
//                buffer.free();
//
//                if(in.remaining() > 0){//如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
//                    return true;
//                }
//            }
//        }
        return false;//处理成功，让父类进行接收下个包
    }
}
