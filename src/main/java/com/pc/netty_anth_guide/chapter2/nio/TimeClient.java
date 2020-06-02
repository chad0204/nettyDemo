package com.pc.netty_anth_guide.chapter2.nio;

public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        new Thread(new TimeClientHandle("localhost", port), "TimeClient-001").start();


    }
}
