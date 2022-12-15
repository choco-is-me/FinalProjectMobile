package com.moh.alarmclock;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.moh.alarmclock.Clock.MoAlarmSession.MoAlarmSessionBroadCast;

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
        setContentView(R.layout.activity_mo_tap);
        MoAlarmSessionBroadCast.activityList.add(this);
        init();
    }

    private void init(){
        this.counter = findViewById(R.id.tap_counter);
        this.nextTest = findViewById(R.id.Another_test_btn);
        this.layout = findViewById(R.id.tap_constrained_layout);


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
