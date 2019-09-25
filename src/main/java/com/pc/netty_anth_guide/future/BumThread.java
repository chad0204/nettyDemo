package com.pc.netty_anth_guide.future;

/**
 * 包子
 * @author pengchao
 * @since 16:05 2019-09-11
 */
public class BumThread extends Thread {

    @Override
    public void run() {
        try {
            Thread.sleep(1000*3);
            System.out.println("包子准备完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
