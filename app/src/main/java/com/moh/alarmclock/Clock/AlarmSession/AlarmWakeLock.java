package com.moh.alarmclock.Clock.AlarmSession;

import android.content.Context;
import android.os.PowerManager;

public class AlarmWakeLock {

    private static final String ALARM_SESSION_TAG = "MyApp::TagName";

    private static PowerManager.WakeLock sCpuWakeLock;

    public static PowerManager.WakeLock createPartialWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE), ALARM_SESSION_TAG);
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.acquire(10*1000*60);
    }

    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }


    public static void turnScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //@SuppressWarnings("deprecation")
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, ALARM_SESSION_TAG);
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
        wakeLock.release();
    }
}
