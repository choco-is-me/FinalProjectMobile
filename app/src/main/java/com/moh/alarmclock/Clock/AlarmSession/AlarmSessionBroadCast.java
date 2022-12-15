package com.moh.alarmclock.Clock.AlarmSession;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.Timer.Timer;

import java.util.ArrayList;
import java.util.List;

public class AlarmSessionBroadCast extends BroadcastReceiver {

    public static List<Activity> activityList = new ArrayList<>();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action == null)
            return;

        boolean closeAllActivities = true;
        switch (action) {
            case NotificationAlarmSession.STOP_ACTION:
                NotificationAlarmSession.alarmSituation();
                Intent i = new Intent(context, NotificationAlarmSession.class);
                context.stopService(i);
                break;
            case NotificationAlarmSession.SNOOZE_ACTION:
                NotificationAlarmSession.snoozeSituation();
                Intent i3 = new Intent(context, NotificationAlarmSession.class);
                context.stopService(i3);
                AlarmClockManager.getInstance().snoozeAlarm(
                        NotificationAlarmSession.information.getId(),5,context);
                break;
            case NotificationAlarmSession.OPEN_ACTION:
                Intent fullScreenIntent = new Intent(context, AlarmSessionActivity.class);
                fullScreenIntent.putExtra(AlarmSessionActivity.EXTRA_INFO, NotificationAlarmSession.CHANNEL_ID_ALARM);
                fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(fullScreenIntent);
                closeAllActivities = false;
                break;
            case NotificationTimerSession.STOP_ACTION_ID:
                if (!NotificationTimerSession.enabled)
                    return;
                Intent i2 = new Intent(context, NotificationTimerSession.class);
                context.stopService(i2);
                break;
            case NotificationTimerSession.RESET_ACTION:
                if (!NotificationTimerSession.enabled)
                    return;
                Intent i4 = new Intent(context, NotificationTimerSession.class);
                context.stopService(i4);
                //reset it
                Timer.universalTimer.reset(context);
                break;
        }


        if (closeAllActivities) {
            closeAllActivities();
        }
    }

    public static void closeAllActivities() {
        for (Activity a : activityList) {
            a.finish();
        }
        activityList = new ArrayList<>();
    }
}
