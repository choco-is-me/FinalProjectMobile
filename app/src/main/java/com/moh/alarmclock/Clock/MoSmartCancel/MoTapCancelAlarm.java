package com.moh.alarmclock.Clock.MoSmartCancel;

import android.app.Activity;
import android.content.Intent;

import java.util.Random;

import com.moh.alarmclock.TapActivity;

public class MoTapCancelAlarm extends MoSmartCancel {



    @Override
    public void show(Activity a) {
        MoSmartCancel.startActivityForResult(a,new Intent(a, TapActivity.class),MoSmartCancel.TAP_CANCEL);
    }


    public static int getRandom(int max){
        Random r = new Random();
        return r.nextInt(max);
    }


}
