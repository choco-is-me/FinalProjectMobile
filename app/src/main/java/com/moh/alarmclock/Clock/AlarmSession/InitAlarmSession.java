package com.moh.alarmclock.Clock.AlarmSession;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.moh.alarmclock.Clock.AlarmClock;
import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.EmptyAlarmException;

import java.util.LinkedList;
import java.util.Queue;

public class InitAlarmSession {

    public enum Type{
        CLOCK,
        TIMER,
    }


    public static Queue<Information> list = new LinkedList<>();

    public static void start(Context context,String title,String lb,String rb,Type type,int id){

        Information information = new Information(title, type,id);
        information.setLeftButton(lb);
        information.setRightButton(rb);
        list.add(information);
        switch (type){
            case TIMER:
                information.changeTitleIfEmpty("Timer");
                Intent i = new Intent(context, NotificationTimerSession.class);
                ContextCompat.startForegroundService(context,i);
                break;
            case CLOCK:
                information.changeTitleIfEmpty("Alarm");
                Intent i2 = new Intent(context, NotificationAlarmSession.class);
                ContextCompat.startForegroundService(context,i2);
                break;
        }

    }



    public static void startAlarm(Context context,int id) throws EmptyAlarmException {
        AlarmClock c = AlarmClockManager.getInstance().getAlarm(id);
        InitAlarmSession.start(context,c.getTitle(),"Long click to snooze",
                "Long click to stop", InitAlarmSession.Type.CLOCK,id);
    }

    public static void startTimer(Context context){
        InitAlarmSession.start(context,"Timer",
                "Long click to reset","Long click to stop", Type.TIMER,1);
    }

}
