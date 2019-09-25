package com.pc.netty_anth_guide.chapter2.four;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * TODO
 *
 * @author pengchao
 * @since 17:04 2019-09-11
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel result) {
        this.channel = result;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String req = new String(body, StandardCharsets.UTF_8);
        System.out.println("The time server receiver order:" + req);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ?
                new Date(System.currentTimeMillis()).toString() + "\n" :
                "BAD ORDER\n";
        doWrite(currentTime);
    }

    private void doWrite(String currentTime) {
        if(currentTime != null && currentTime.trim().length() > 0) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    //如果没有发送完成，继续发送
                    if(attachment.hasRemaining()) {
                        channel.write(attachment,attachment,this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
