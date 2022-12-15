package com.moh.alarmclock.Clock.AlarmBoot;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.moh.alarmclock.Clock.AlarmClockManager;


public class AlarmBootService extends JobIntentService {

    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AlarmBootService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        AlarmClockManager.getInstance().activateNextAlarm(this.getBaseContext());
    }



}
