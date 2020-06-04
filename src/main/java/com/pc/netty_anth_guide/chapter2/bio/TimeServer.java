package com.pc.netty_anth_guide.chapter2.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为每个新的客户端连接都创建一个新的线程，太耗资源。
 *
 *
 * ✨注意ServerSocket只是用来接收客户端连接，通过accept方法创建socket和客户端socket通信。
 * 所以一次客户端请求，需要用到三个socket，一个是服务端创建的ServerSocket，一个是客户端创建的socket，还有一个是服务端通过accept方法创建的socket
 *
 */
public class TimeServer {

    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        ServerSocket server = null;

        try {
            //只做一件事，用于接收客户端连接，后面通过serverSocket.accept()创建socket与客户端通信
            server = new ServerSocket(port);
            System.out.println("The time server is start in port:" + port);
            Socket socket = null;//通过accept创建的通信socket

            //✨为什么一定要开多个线程，因为是accept接收连接和后面读取消息的read/readLine都是阻塞的。
            //没有连接进来，accept阻塞等待客户端连接，有一个连接进来，accept执行结束然后在read方法阻塞等待客户端消息。
            //如果此时再来一个连接，由于当前线程是阻塞的，那么将没有多余的线程去接收连接。所以每个客户端请求都要创建新线
            // 程处理。而nio的读写都不是阻塞的，所以服务端只需要一个线程。
            while (true) {
                //阻塞方法，如果有客户端连接，则创建对应客户端socket的socket，nio是注册事件监听，有消息来就通知我然后select。
                socket = server.accept();
                //每个新的客户端连接，都会创建一个新的线程，线程数量和客户端数量是一一对应的。
                new Thread(new TimeServerHandler(socket),"Thread-"+count.getAndIncrement()).start();
            }

        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }

    }
}
