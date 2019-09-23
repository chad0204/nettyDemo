package com.pc.future;

/**
 * 凉菜
 * @author pengchao
 * @since 16:07 2019-09-11
 */
public class ColdDishThread extends Thread {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println("凉菜准备完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
