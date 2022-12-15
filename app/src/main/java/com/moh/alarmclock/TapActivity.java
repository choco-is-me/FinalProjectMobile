package com.moh.alarmclock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.moh.alarmclock.Clock.AlarmSession.AlarmSessionBroadCast;

public class TapActivity extends AppCompatActivity {

    private final int MAX_TAPS = 40;
    private final int MIN_TAPS = 25;

    private TextView counter;
    private Button nextTest;
    private ConstraintLayout layout;
    private int counterValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmSessionBroadCast.activityList.add(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init(){

        this.counter.setText(this.counterValue+"");

        this.nextTest.setOnClickListener((b)->{
           // next test
        });


    }

    @Override
    public void onBackPressed() {

    }



    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

}
