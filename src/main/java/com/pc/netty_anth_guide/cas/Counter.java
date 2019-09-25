package com.pc.netty_anth_guide.cas;

/**
 *
 * @author pengchao
 * @since 10:05 2019-09-25
 */
class Number {

    private volatile int count = 0;

    private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();


    static Class<?> ak = Number.class;
    private static final int offset = U.arrayBaseOffset(ak);


    public synchronized int incrAndGet(int number) {
        this.count += number;
        return count;
    }

    public synchronized int get() {
        return count;
    }

    //用synchronized来解释cas语义
    public synchronized boolean compareAndSwap(int expect, int newValue) {
        int old = this.count;
        if(old == expect) {
            this.count = newValue;
            return true;
        }
        return false;
    }

    //unsafe,比较内存地址V上的旧值和预期值A相匹配，则将旧值更新为新值B。
    //预期值A是修改发起时从V上读取的，如果和旧值一致，表示在读取到更新这段时间内内存V被其他线程修改，返回false，如果和旧值一致，表示内存V没有并发修改，返回true。
    public final boolean compareAndSet(int expect, int newValue) {
        return U.compareAndSwapInt(this, offset, expect, newValue);
    }

    public final int addAndGet(int delta) {
        for (;;) {
            int current = get();
            int next = current +delta;
            if(compareAndSwap(current, next)) {
                return next;
            }
        }
    }


}

public class Counter implements Runnable {

    private Number number;

    public Counter(Number number) {
        this.number = number;
    }

    @Override
    public void run() {
        for (int i =0; i < 5 ; i++) {
            number.incrAndGet(1);
        }
    }



    public static void main(String[] args) throws InterruptedException {
        Number number = new Number();
        for (int i = 0;i<1000;i++) {
            new Thread(new Counter(number)).start();
        }




//        Thread.sleep(5000L);
        System.out.println(number.get());
    }



}
