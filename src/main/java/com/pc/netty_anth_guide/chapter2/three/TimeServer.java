package com.pc.netty_anth_guide.chapter2.three;

public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        try {
            MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
            new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
