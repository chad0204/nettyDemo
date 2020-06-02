package com.pc.netty_anth_guide.chapter2.nio;

public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        try {
            //打开channel监听端口，创建多路复用器selector，并开启。将channel注册到多路复用器，监听accept事件
            MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
            //不需要循环阻塞，也不用每次连接都创建线程
            new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
