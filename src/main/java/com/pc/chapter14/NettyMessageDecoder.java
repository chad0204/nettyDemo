package com.pc.chapter14;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pengchao
 * @since 19:24 2019-09-19
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder/*支持TCP沾包和拆包处理*/ {

    MarshallingDecoder marshallerDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallerDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        /*支持TCP沾包和拆包处理*/
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if(frame == null) {
            return  null;
        }

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        int size = in.readInt();
        if(size > 0) {
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for(int i=0;i<size;i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, StandardCharsets.UTF_8);
                attch.put(key,marshallerDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }

        if(in.readableBytes() > 4) {
            message.setBody(marshallerDecoder.decode(in));
        }
        message.setHeader(header);
        return message;
    }
}
