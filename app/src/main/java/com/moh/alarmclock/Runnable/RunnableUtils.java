package com.moh.alarmclock.Runnable;

public class RunnableUtils {
    public static void runIfNotNull(Runnable r){
        if(r!=null){
            r.run();
        }
    }

    public static void runIfNotNull(java.lang.Runnable refreshScreen) {
    }
}
