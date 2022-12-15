package com.moh.alarmclock.Runnable;

public interface MoRunnable {


    /**
     * you can pass any arguments into this runnable
     * @param args
     * @param <T>
     */
    <T> void run(T ... args);

}
