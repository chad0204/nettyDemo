package com.pc.netty_anth_guide.chapter14;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
import java.io.IOException;

/**
 * netty消息解码工具类
 *
 * @author pengchao
 * @since 19:26 2019-09-19
 */
public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        this.unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    public Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);

        unmarshaller.start(input);
        Object obj = unmarshaller.readObject();
        unmarshaller.finish();
        in.readerIndex(in.readerIndex() + objectSize);
        return obj;
    }
}
