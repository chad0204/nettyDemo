package com.pc.chapter2.two;

import com.pc.chapter2.one.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {

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
            //创建IO任务线程池
            TimeServerHandlerExecutePool executePool = new TimeServerHandlerExecutePool(50, 10000);

            while (true) {
                socket = server.accept();
                executePool.execute(new TimeServerHandler(socket));
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
