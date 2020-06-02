package com.pc.netty_anth_guide.chapter2.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为每个新的客户端连接都创建一个新的线程，太耗资源。
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
            server = new ServerSocket(port);
            System.out.println("The time server is start in port:" + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();//阻塞
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
