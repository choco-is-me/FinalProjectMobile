package com.moh.alarmclock.Clock.StopWatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moh.alarmclock.MainActivity;
import com.moh.alarmclock.Section.SectionManager;

import java.util.TimerTask;

public class StopWatchBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action == null)
            return;

        switch (action){
            case StopWatchService.STOP_ACTION:
                StopWatch.universal.stop(new TimerTask() {
                    @Override
                    public void run() {
                        StopWatchService.updateNotification(context,true);
                    }
                });
                StopWatchService.updateNotification(context,false);
                break;
            case StopWatchService.LAP_ACTION:
                boolean r = StopWatch.universal.lap();
                if(r) {
                    StopWatch.universal.reset();
                    Intent intent1 = new Intent(context, StopWatchService.class);
                    context.stopService(intent1);
                    //System.out.println("reseting from service");
                }else{
                    StopWatchService.updateNotification(context,false);
                }
                break;
            case StopWatchService.OPEN_ACTION:
                SectionManager.getInstance().setSection(SectionManager.STOP_WATCH_SECTION);
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
        }

    }
}
