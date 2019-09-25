package com.pc.netty_in_action.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author pengchao
 * @since 17:47 2019-09-25
 */
public class Test {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);//用以监听制定端口上的连接
        Socket clientSocket = serverSocket.accept();//调用accept方法会阻塞直到一个连接建立

        //下面的流对象派生于该套接字的流对象
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String request,response;

        while ((request = in.readLine()) != null) {//readLine方法将会阻塞，直到读取到换行符和回车符字符串
            //如果客户端发送Done则结束
            if("Done".equals(request)) {
                break;
            }
            response = processRequest(request);//请求被传递给服务器的处理方法
            out.println(response);//服务端响应请求发送给客户端
        }

    }

    private static String processRequest(String request) {
        return request.trim();
    }
}
