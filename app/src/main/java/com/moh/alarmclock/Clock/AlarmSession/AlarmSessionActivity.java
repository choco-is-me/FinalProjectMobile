package com.moh.alarmclock.Clock.AlarmSession;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.Timer.Timer;
import com.moh.alarmclock.MainActivity;
import com.moh.alarmclock.R;

import java.util.Objects;

public class AlarmSessionActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    public static final String EXTRA_INFO = "emoinfo";

    private Information information;
    InitAlarmSession.Type moType;
   // private GestureDetectorCompat detectorCompat;

    private boolean snoozed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mo_alarm_session);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN|
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON|
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );
        AlarmSessionBroadCast.activityList.add(this);
        hideSystemUI();
        init();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    private void init(){
        switch (Objects.requireNonNull(getIntent().getStringExtra(EXTRA_INFO))){
            case NotificationAlarmSession.CHANNEL_ID_ALARM:
                this.information = NotificationAlarmSession.information;
                break;
            case NotificationTimerSession.CHANNEL_ID_TIMER:
                this.information = NotificationTimerSession.information;
                break;
        }
        if(information == null) {
            Toast.makeText(this, "The information was lost. Please report this", Toast.LENGTH_LONG).show();
            return;
        }


        TextView title = findViewById(R.id.title_alarm);
        ImageView snoozeButton = findViewById(R.id.snooze_button);
        TextView snoozeText = findViewById(R.id.snooze_text);

        snoozeText.setText(information.getLeftButton());
        findViewById(R.id.text_clock);
        title.setText(information.getTitle());
        this.moType = information.getType();

        if (!information.isClock()) {
            snoozeButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_restore_24));
        }

        snoozeButton.setOnLongClickListener((v) -> {
            switch (moType){
                case CLOCK:
                    this.snoozeAlarm();
                    break;
                case TIMER:
                    this.resetTimer();
                    break;
            }
            return true;
        });



//        this.detectorCompat = new GestureDetectorCompat(this,this);
//        detectorCompat.setOnDoubleTapListener(this);

        // cancel the timer of notification timer
        NotificationTimerSession.cancelTimer();
    }



    // activates it only if the user wants it
    // also add a delay so that the alarm rings if they want

    private void snoozeAlarm() {
        if (information.getClock().getSnooze().isActive()) {
            snoozed = true;
            stopAlarm();
            AlarmClockManager.getInstance().snoozeAlarm(information.getId(),5,this);
        } else {
            Toast.makeText(this, "Snooze is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTimer(){
        this.stopAlarm();
        MainActivity.isInApp = false;
        Timer.universalTimer.reset(this);
        //Timer.universalTimer.startService(this);
    }

    @Override
    public void onBackPressed() {
        // dont close the activity on back pressed
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            //Do something
            boolean b = handleVolumeDownUp();
            if(b)
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean handleVolumeDownUp() {
        // only continue if this is an alarm
        // volume buttons during timer should inc/dec volume
        if(this.moType != InitAlarmSession.Type.CLOCK)
            return false;

        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        String index = s.getString(getString(R.string.volume_button),"-3");
        switch (index){
            case "-3":
                // do nothing
                return true;
            case "1":
                // snooze alarm
                snoozeAlarm();
                return true;
            case "2":
                // control volumes
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        detectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    private void stopAlarm() {
        if(information != null) {
            Intent i;
            switch (information.getType()) {
                case CLOCK:
                    // different situations where the service needs to know about it
                    if(snoozed){
                        NotificationAlarmSession.snoozeSituation();
                    }else{
                        NotificationAlarmSession.alarmSituation();
                    }
                    i = new Intent(this, NotificationAlarmSession.class);
                    break;
                case TIMER:
                    i = new Intent(this, NotificationTimerSession.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + information.getType());
            }
            stopService(i);
        }
        finishAffinity();
        finishAndRemoveTask();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        // we snooze the alarm for another time that they already have picked

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
//        stopAlarmConsiderSmart();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


}
