package com.pc.netty_anth_guide.chapter2.fakenio;

import com.pc.netty_anth_guide.chapter2.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 使用线程池控制线程数量
 * 但由于读取操作和写入操作是阻塞的，如果因为网络等原因导致延迟，在没有处理完之前，其他客户端消息只能在队列中排队。最后导致新的客户端
 * 连接被拒绝，产生大量的连接超时。
 */
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
                //使用线程池控制线程数量
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
