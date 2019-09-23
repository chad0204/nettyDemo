package com.pc.chapter14;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;
import java.io.IOException;

/**
 *
 * netty消息编码工具类
 *
 * @author pengchao
 * @since 17:48 2019-09-19
 */
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACHEOLDER = new byte[4];

    Marshaller marshaller;


    public MarshallingEncoder(Marshaller marshaller) throws IOException {
        this.marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    public void encode(Object msg, ByteBuf out) throws IOException {
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACHEOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex()-lengthPos-4);
        } finally {
            marshaller.close();
        }
    }
}
