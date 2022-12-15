package com.moh.alarmclock.Clock.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moh.alarmclock.MainActivity;
import com.moh.alarmclock.Section.SectionManager;

public class TimerNotificationBroadCast extends BroadcastReceiver {


    /**
     * when the user presses a notification action button, this class
     * gets activated
     * @param context
     * @param intent
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action == null)
            return;

        // don't do anything if timer has not been created
        if (!Timer.universalTimer.isCreated())
            return;

        switch (action){
            case Timer.CANCEL_ACTION:
                Timer.universalTimer.cancel(context,false,false);
                break;
            case Timer.PAUSE_ACTION:
                Timer.universalTimer.pause(true);
                Timer.universalTimer.updateNotification(context,false);
                break;
            case Timer.OPEN_ACTION:
                SectionManager.getInstance().setSection(SectionManager.TIMER_SECTION);
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
        }


        //System.out.println(action);

    }
}
