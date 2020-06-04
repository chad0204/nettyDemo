package com.pc.netty_anth_guide.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import java.nio.ByteBuffer;

/**
 *
 * 1.ByteBuffer只有一个position指针，需要flip()操作归位，才知道可读空间在哪。ByteBuf提供了readIndex和writeIndex，不需要归位操作。
 *
 * 2.ByteBuffer不可自动扩容，put前需要判断可用，并手动扩容。而ByteBuf写操作前会根据大小自动扩容。
 *
 *
 * @author pengchao
 * @date 14:45 2020-06-03
 */
public class TestByteBuffer {

    public static void main(String[] args) {

        //开辟一个1k的buffer
        ByteBuffer buffer = ByteBuffer.allocate(2);

//        ByteBuffer.allocateDirect(10);//堆外内存，也就是直接内存

        //向buffer中写入byte数组
        byte[] writeBytes = "abcdefgh".getBytes();

        //判断buffer空间大小是否足够，不够需要手动扩容
        if(buffer.remaining() > writeBytes.length) {
            buffer.put(writeBytes);
        } else {
            buffer = ByteBuffer.allocate(writeBytes.length);
            buffer.put(writeBytes);
        }


        //设置limit=position,position归0
        buffer.flip();

        //创建一个长度为length = limit-position的字节数组，接收buffer的数据
        int length = buffer.remaining();
        byte[] readBytes = new byte[length];
        buffer.get(readBytes);//获取0~length的数据
        String result = new String(readBytes);//数组转字符串


        System.out.println(result);





    }
}
