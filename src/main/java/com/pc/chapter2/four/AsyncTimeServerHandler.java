package com.pc.chapter2.four;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author pengchao
 * @since 16:33 2019-09-11
 */
public class AsyncTimeServerHandler implements Runnable {

    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;//异步的服务端通道

    public AsyncTimeServerHandler(int port) {
        try {
            this.port = port;
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The time server is start in port : "+port);
    }

    @Override
    public void run() {
        //作用是在完成一组真正执行的操作之前，允许当前的线程一直阻塞，这里可以让线程在此阻塞，防止服务端执行完成后退出
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
    }
}
