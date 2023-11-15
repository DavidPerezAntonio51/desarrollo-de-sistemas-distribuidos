package com.escom.ipn;

/**
 * Hello world!
 */
public class Hilos extends Thread {
    static long n;
    static Object lock = new Object();

    @Override
    public void run() {
        for (int i = 0; i < 100000; i++) {
            synchronized (lock) {
                n++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Hilos t1 = new Hilos();
        Hilos t2 = new Hilos();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(n);
    }
}
