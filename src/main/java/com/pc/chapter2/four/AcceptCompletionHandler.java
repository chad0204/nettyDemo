package com.pc.chapter2.four;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * TODO
 *
 * @author pengchao
 * @since 16:43 2019-09-11
 */
public class AcceptCompletionHandler implements CompletionHandler<java.nio.channels.AsynchronousSocketChannel,AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        //继续使用这个通道接收客户端连接，形成循环，每当一个客户端读连接成功后，再异步接收新的客户端连接
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer,buffer,new ReadCompletionHandler(result));

    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}
