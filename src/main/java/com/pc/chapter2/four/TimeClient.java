package com.pc.chapter2.four;

/**
 *
 * @author pengchao
 * @since 10:32 2019-09-12
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandler("127.0.0.1",port),"AIO-AsyncTimeClientHandler-001").start();
    }
}
