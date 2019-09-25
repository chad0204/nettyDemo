package com.pc.netty_anth_guide.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *
 * @author pengchao
 * @since 16:08 2019-09-11
 */
public class TestFuture {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

//        normalExecutor();

        futureExecutor();

    }


    public static void normalExecutor() throws InterruptedException {
        Long start = System.currentTimeMillis();

        //等凉菜  必须要等待返回结果，所以要调用join方法
        Thread t1 = new ColdDishThread();
        t1.start();
        t1.join();

        //等包子  必须要等待返回结果，所以要调用join方法
        Thread t2 = new BumThread();
        t2.start();
        t2.join();

        Long end = System.currentTimeMillis();
        System.out.println("总共耗时："+(end-start));//4秒

    }


    @SuppressWarnings("unchecked")
    public static void futureExecutor() throws InterruptedException, ExecutionException {
        Long start = System.currentTimeMillis();

        //等凉菜  必须要等待返回结果
        Callable ca1 = () -> {
            Thread.sleep(1000);
            return "凉菜准备完毕";
        };
        FutureTask<String> ft1 = new FutureTask<>(ca1);
        new Thread(ft1).start();


        //等包子  必须要等待返回结果
        Callable ca2 = () -> {
            Thread.sleep(1000*3);
            return "包子准备完毕";
        };
        FutureTask<String> ft2 = new FutureTask<>(ca2);
        new Thread(ft2).start();


        System.out.println(ft1.get());
        System.out.println(ft2.get());

        Long end = System.currentTimeMillis();
        System.out.println("总共耗时："+(end-start));//3秒

    }
}
