package com.moh.alarmclock.Clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moh.alarmclock.Clock.AlarmSession.AlarmWakeLock;
import com.moh.alarmclock.Clock.AlarmSession.InitAlarmSession;


public class AlarmReceiver extends BroadcastReceiver {

    String channelId = "";

    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmWakeLock.acquireCpuWakeLock(context);
        int id = intent.getIntExtra(AlarmClockManager.SET_ID,-1);
        channelId = id + "kl";
        try {
            AlarmClockManager.getInstance().cancelAlarm(id,context);
        } catch (EmptyAlarmException e) {
            return;
        }
        try {
            InitAlarmSession.startAlarm(context,id);
        } catch (EmptyAlarmException e) {
        }

    }

}
