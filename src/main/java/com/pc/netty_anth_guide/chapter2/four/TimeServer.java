package com.pc.netty_anth_guide.chapter2.four;

/**
 *  AIO
 *
 * @author pengchao
 * @since 16:32 2019-09-11
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);

        new Thread(timeServer,"AIO-AsyncTimeServerHandler-001").start();
    }
}
