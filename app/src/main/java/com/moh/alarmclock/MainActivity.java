package com.moh.alarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moh.alarmclock.Animation.Animation;
import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.StopWatch.StopWatch;
import com.moh.alarmclock.Clock.Timer.Timer;
import com.moh.alarmclock.Clock.Timer.TimerPresetPackage.TimerPreset;
import com.moh.alarmclock.Section.SectionManager;
import com.moh.alarmclock.SharedPref.SharedPref;
import com.moh.alarmclock.Theme.Theme;


public class MainActivity extends AppCompatActivity {


    public static boolean isInApp = true;
    private final TimerSectionManager timerSectionManager = new TimerSectionManager(this);
    private final StopWatchManager stopWatchManager = new StopWatchManager(this);
    private AlarmSectionManager alarmSectionManager;
    private BottomNavigationView bottomNavigation;
    private View bottomDeleteBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case AlarmSectionManager.CREATE_ALARM_CODE:
                alarmSectionManager.updateAll();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void init() {
        SharedPref.loadAll(this);
        Animation.initAllAnimations(this);
        this.timerSectionManager.timer_liner_layout = findViewById(R.id.linear_timer_layout);
        this.bottomDeleteBar = findViewById(R.id.delete_mode_preset);
        this.timerSectionManager.initTimerSection();
        this.stopWatchManager.initStopWatchSection();
        this.initBottomNavigation();
        this.initAlarmSection();
        timerSectionManager.closeTimerService();
        Theme.updateTheme(this);
    }




    private void initBottomNavigation() {
        this.bottomNavigation = findViewById(R.id.bottom_navigation);
        this.stopWatchManager.root.setVisibility(View.INVISIBLE);
        this.timerSectionManager.timer_liner_layout.setVisibility(View.INVISIBLE);
        this.bottomNavigation.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.Alarm_Section:
                    changeLayout(true, false, false, false, false);
                    SectionManager.getInstance().setSection(SectionManager.ALARM_SECTION);
                    return true;
                case R.id.StopWatch_Section:
                    changeLayout(false, true, false, false, false);
                    SectionManager.getInstance().setSection(SectionManager.STOP_WATCH_SECTION);
                    return true;
                case R.id.Timer_Section:
                    changeLayout(false, false, true, false, false);
                    SectionManager.getInstance().setSection(SectionManager.TIMER_SECTION);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void switchSection() {
        switch (SectionManager.getInstance().getSection()) {
            case SectionManager.ALARM_SECTION:
                changeLayout(true, false, false, false, true);
                break;
            case SectionManager.STOP_WATCH_SECTION:
                changeLayout(false, true, false, false, true);
                break;
            case SectionManager.TIMER_SECTION:
                changeLayout(false, false, true, false, true);
                break;
        }
    }


    private void changeLayout(boolean alarm, boolean stopwatch, boolean timer, boolean world, boolean setSelected) {
        this.stopWatchManager.root.setVisibility(stopwatch ? View.VISIBLE : View.INVISIBLE);
        this.timerSectionManager.timer_liner_layout.setVisibility(timer ? View.VISIBLE : View.INVISIBLE);
        this.alarmSectionManager.root.setVisibility(alarm ? View.VISIBLE : View.INVISIBLE);
        if (!setSelected) {
            return;
        }
        if (alarm) {
            this.bottomNavigation.setSelectedItemId(R.id.Alarm_Section);
        } else if (timer) {
            this.bottomNavigation.setSelectedItemId(R.id.Timer_Section);
        } else if (stopwatch) {
            this.bottomNavigation.setSelectedItemId(R.id.StopWatch_Section);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeIsInApp(true);
        this.alarmSectionManager.onResume();

        // if we are in select mode and they come through the notification, we must still show the select
        // mode sections instead of jumping to other states
        boolean alarmIsSelecting = alarmSectionManager.isSelecting();
        boolean timerPresetIsSelecting = timerSectionManager.isSelecting();

        if (alarmIsSelecting) {
            SectionManager.getInstance().setSection(SectionManager.ALARM_SECTION);
        } else if (timerPresetIsSelecting) {
            SectionManager.getInstance().setSection(SectionManager.TIMER_SECTION);
        }
        switchSection();
    }


    private void changeIsInApp(boolean b) {
        isInApp = b;
    }


    @Override
    public void onBackPressed() {
        if (alarmSectionManager.onBackPressed()) {
           // we have consumed the back press there, so we can ignore it here
        } else if (TimerPreset.isInDeleteMode) {
            timerSectionManager.cancelDeleteAlarmMode();
        }else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmClockManager.getInstance().onDestroy();
    }

    /**
     * whenever the window is not infocus we should make a service for the timer
     * if the timer is running
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        timerSectionManager.onWindowFocusChanged();
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Timer.universalTimer.startService(this);
            StopWatch.universal.startNotificationService(this);
            changeIsInApp(false);
        } else {
            alarmSectionManager.updateSubTitle();
            timerSectionManager.closeTimerService();
            StopWatch.universal.cancelNotificationService(this);
            stopWatchManager.update();
            changeIsInApp(true);
        }
    }


    private void initAlarmSection() {
        alarmSectionManager = new AlarmSectionManager(this);
        alarmSectionManager.initAlarmSection();
    }

    public BottomNavigationView getBottomNavigation() {
        return bottomNavigation;
    }

    public View getBottomDeleteBar() {
        return bottomDeleteBar;
    }


    public interface SelectModeInterface {
        boolean isSelecting();
    }
}
