package com.eb.sc.tcprequest;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

/**
 * Created by Administrator on 2017/8/5.
 */

public class ByteArrayCodecFactory  implements ProtocolCodecFactory  {
    private ByteArrayDecoder decoder;
    private TextLineEncoder encoder;
    public ByteArrayCodecFactory() {
        encoder = new TextLineEncoder();
        decoder = new ByteArrayDecoder();
    }
    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

}
