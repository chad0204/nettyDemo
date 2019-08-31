package com.pc.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Test {

    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10, true);


        for (int i = 0; i < 200; i++) {
            blockingQueue.add("ele-" + i);
            System.out.println(blockingQueue.size());
        }

    }
}
