package com.moh.alarmclock.Clock.MoAlarmSession;

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

import com.moh.alarmclock.Clock.MoAlarmClockManager;
import com.moh.alarmclock.Clock.MoTimer.MoTimer;
import com.moh.alarmclock.MainActivity;
import com.moh.alarmclock.R;

import java.util.Objects;

public class MoAlarmSessionActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    public static final String EXTRA_INFO = "emoinfo";

    private MoInformation moInformation;
    MoInitAlarmSession.Type moType;
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
        MoAlarmSessionBroadCast.activityList.add(this);
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
            case MoNotificationAlarmSession.CHANNEL_ID_ALARM:
                this.moInformation = MoNotificationAlarmSession.moInformation;
                break;
            case MoNotificationTimerSession.CHANNEL_ID_TIMER:
                this.moInformation = MoNotificationTimerSession.moInformation;
                break;
        }
        if(moInformation == null) {
            Toast.makeText(this, "The information was lost. Please report this", Toast.LENGTH_LONG).show();
            return;
        }


        TextView title = findViewById(R.id.title_alarm);
        ImageView snoozeButton = findViewById(R.id.snooze_button);
        ImageView stopButton = findViewById(R.id.stop_alarm);
        TextView snoozeText = findViewById(R.id.snooze_text);
        TextView stopText = findViewById(R.id.stop_alarm_text);

        stopText.setText(moInformation.getRightButton());
        snoozeText.setText(moInformation.getLeftButton());
        findViewById(R.id.text_clock);
        title.setText(moInformation.getTitle());
        this.moType = moInformation.getType();

        if (!moInformation.isClock()) {
            snoozeButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_restore_24));
            stopButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_timer_off_24));
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
        MoNotificationTimerSession.cancelTimer();
    }



    // activates it only if the user wants it
    // also add a delay so that the alarm rings if they want

    private void snoozeAlarm() {
        if (moInformation.getClock().getSnooze().isActive()) {
            snoozed = true;
            stopAlarm();
            MoAlarmClockManager.getInstance().snoozeAlarm(moInformation.getId(),5,this);
        } else {
            Toast.makeText(this, "Snooze is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTimer(){
        this.stopAlarm();
        MainActivity.isInApp = false;
        MoTimer.universalTimer.reset(this);
        //MoTimer.universalTimer.startService(this);
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
        if(this.moType != MoInitAlarmSession.Type.CLOCK)
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
        if(moInformation != null) {
            Intent i;
            switch (moInformation.getType()) {
                case CLOCK:
                    // different situations where the service needs to know about it
                    if(snoozed){
                        MoNotificationAlarmSession.snoozeSituation();
                    }else{
                        MoNotificationAlarmSession.alarmSituation();
                    }
                    i = new Intent(this, MoNotificationAlarmSession.class);
                    break;
                case TIMER:
                    i = new Intent(this, MoNotificationTimerSession.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + moInformation.getType());
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
