package com.pc.test;

import io.netty.buffer.*;
import io.netty.util.CharsetUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Test {

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(88);
        String value = "abcdefghijklmn";

        buffer.put(value.getBytes());
        buffer.flip();

        byte[] vArr = new byte[buffer.remaining()];

        buffer.get(vArr);

        String decodeValue = new String(vArr);

        System.out.println();


        ByteBuf buf = Unpooled.copiedBuffer(new StringBuilder("123456789abcdefghigk"), CharsetUtil.UTF_8);

        buf.writeByte(10);
        int newCapacity = 10 / 8 * 8;

        System.out.println(newCapacity);



        ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf dst = ALLOC.buffer(10);
        ByteBuf directBuffer = ALLOC.directBuffer();
        ByteBuf heapBuffer = ALLOC.heapBuffer();






    }
}
