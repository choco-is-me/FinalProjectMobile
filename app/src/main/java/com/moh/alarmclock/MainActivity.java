package com.moh.alarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.moh.alarmclock.Animation.MoAnimation;
import com.moh.alarmclock.Clock.MoAlarmClockManager;
import com.moh.alarmclock.Clock.MoStopWatch.MoStopWatch;
import com.moh.alarmclock.Clock.MoTimer.MoTimer;
import com.moh.alarmclock.Clock.MoTimer.MoTimerPresetPackage.MoTimerPreset;
import com.moh.alarmclock.Section.MoSectionManager;
import com.moh.alarmclock.Sensor.MoShakeListener;
import com.moh.alarmclock.SharedPref.MoSharedPref;
import com.moh.alarmclock.Theme.MoTheme;


public class MainActivity extends AppCompatActivity {


    public static boolean isInApp = true;
    private final TimerSectionManager timerSectionManager = new TimerSectionManager(this);
    private final StopWatchManager stopWatchManager = new StopWatchManager(this);
    private final WorldClockSectionManager worldClockSectionManager = new WorldClockSectionManager(this);
    private AlarmSectionManager alarmSectionManager;
    private MoShakeListener smartShake;
    /**
     * world clock
     */
    public static final int ADD_WORLD_CLOCK_CODE = 0;
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
            case ADD_WORLD_CLOCK_CODE:
                worldClockSectionManager.onWorldClockChanged();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void init() {
        MoSharedPref.loadAll(this);
        MoAnimation.initAllAnimations(this);
        this.worldClockSectionManager.root = findViewById(R.id.layout_worldClock);
        this.timerSectionManager.timer_liner_layout = findViewById(R.id.linear_timer_layout);
        this.bottomDeleteBar = findViewById(R.id.delete_mode_preset);
        this.timerSectionManager.initTimerSection();
        this.stopWatchManager.initStopWatchSection();
        this.initBottomNavigation();
        this.initAlarmSection();
        worldClockSectionManager.initWorldClockSection();
        this.initSmartShakeListeners();
        timerSectionManager.closeTimerService();
        // adding all the animations to a sparse array
        MoTheme.updateTheme(this);
    }


    private void initSmartShakeListeners() {
        smartShake = new MoShakeListener(true) {
            /**
             * when this class detects a shake
             */
            @Override
            public void onShakeDetected() {
                switch (MoSectionManager.getInstance().getSection()) {
                    case MoSectionManager.ALARM_SECTION:
                        alarmSectionManager.onSmartShake(this);
                        break;
                    case MoSectionManager.TIMER_SECTION:
                        timerSectionManager.onSmartShake(this);
                        break;
                }
            }
        };
    }


    private void initBottomNavigation() {
        this.bottomNavigation = findViewById(R.id.bottom_navigation);
        this.stopWatchManager.root.setVisibility(View.INVISIBLE);
        this.timerSectionManager.timer_liner_layout.setVisibility(View.INVISIBLE);
        this.bottomNavigation.setOnNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.Alarm_Section:
                    changeLayout(true, false, false, false, false);
                    MoSectionManager.getInstance().setSection(MoSectionManager.ALARM_SECTION);
                    return true;
                case R.id.StopWatch_Section:
                    changeLayout(false, true, false, false, false);
                    MoSectionManager.getInstance().setSection(MoSectionManager.STOP_WATCH_SECTION);
                    return true;
                case R.id.Timer_Section:
                    changeLayout(false, false, true, false, false);
                    MoSectionManager.getInstance().setSection(MoSectionManager.TIMER_SECTION);
                    return true;
                case R.id.WorldClock_Section:
                    changeLayout(false, false, false, true, false);
                    MoSectionManager.getInstance().setSection(MoSectionManager.WORLD_CLOCK_SECTION);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void switchSection() {
        switch (MoSectionManager.getInstance().getSection()) {
            case MoSectionManager.ALARM_SECTION:
                changeLayout(true, false, false, false, true);
                break;
            case MoSectionManager.STOP_WATCH_SECTION:
                changeLayout(false, true, false, false, true);
                break;
            case MoSectionManager.TIMER_SECTION:
                changeLayout(false, false, true, false, true);
                break;
            case MoSectionManager.WORLD_CLOCK_SECTION:
                changeLayout(false, false, false, true, true);
                break;
        }
    }


    private void changeLayout(boolean alarm, boolean stopwatch, boolean timer, boolean world, boolean setSelected) {
        this.stopWatchManager.root.setVisibility(stopwatch ? View.VISIBLE : View.INVISIBLE);
        this.timerSectionManager.timer_liner_layout.setVisibility(timer ? View.VISIBLE : View.INVISIBLE);
        this.alarmSectionManager.root.setVisibility(alarm ? View.VISIBLE : View.INVISIBLE);
        this.worldClockSectionManager.root.setVisibility(world ? View.VISIBLE : View.INVISIBLE);
        if (!setSelected) {
            return;
        }
        if (alarm) {
            this.bottomNavigation.setSelectedItemId(R.id.Alarm_Section);
        } else if (timer) {
            this.bottomNavigation.setSelectedItemId(R.id.Timer_Section);
        } else if (stopwatch) {
            this.bottomNavigation.setSelectedItemId(R.id.StopWatch_Section);
        } else if (world) {
            this.bottomNavigation.setSelectedItemId(R.id.WorldClock_Section);
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
        boolean worldClockIsSelecting = worldClockSectionManager.isSelecting();
        boolean timerPresetIsSelecting = timerSectionManager.isSelecting();

        if (alarmIsSelecting) {
            MoSectionManager.getInstance().setSection(MoSectionManager.ALARM_SECTION);
        } else if (worldClockIsSelecting) {
            MoSectionManager.getInstance().setSection(MoSectionManager.WORLD_CLOCK_SECTION);
        } else if (timerPresetIsSelecting) {
            MoSectionManager.getInstance().setSection(MoSectionManager.TIMER_SECTION);
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
        } else if (MoTimerPreset.isInDeleteMode) {
            timerSectionManager.cancelDeleteAlarmMode();
        } else if (worldClockSectionManager.onBackPressed()) {
            // we have consumed the back press there, so we can ignore it here
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MoAlarmClockManager.getInstance().onDestroy();
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
            MoTimer.universalTimer.startService(this);
            MoStopWatch.universal.startNotificationService(this);
            this.smartShake.stop();
            changeIsInApp(false);
        } else {
            alarmSectionManager.updateSubTitle();
            timerSectionManager.closeTimerService();
            MoStopWatch.universal.cancelNotificationService(this);
            stopWatchManager.update();
            this.smartShake.start(this);
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
