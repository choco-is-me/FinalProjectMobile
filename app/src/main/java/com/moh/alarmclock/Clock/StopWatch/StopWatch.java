package com.moh.alarmclock.Clock.StopWatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.moh.alarmclock.Animation.Animation;
import com.moh.alarmclock.Date.TimeUtils;
import com.moh.alarmclock.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class StopWatch {

    public static StopWatch universal = new StopWatch() {
        @Override
        public void onTick() {}
    };

    private static long PERIOD_OF_TICK = 10;
    public static long PERIOD = 24*3600*7;
    private final LapManager lapManager = new LapManager();


    private Timer timer;
    // 3 text watch hr mm ss
    private TextView[] stopWatchTv;

    // start,stop,lap
    private Button[] buttons;

    // time elapsed in milliseconds
    private long milliSecondsElapsed;

    private Activity activity;

    private boolean isRunning,isCreated;

    private TimerTask savedNotificationTask;


    private CountDownTimer countDownTimer;

    private boolean updateTextViews = true;

    public StopWatch(TextView[] stopWatchTv, Activity activity){
        this.stopWatchTv = stopWatchTv;
        this.activity = activity;
        this.initClass();
    }

    public StopWatch(){
        this.initClass();
    }

    private void initClass(){
        this.stopWatchTv = null;
        this.activity = null;
        this.setRunning(false);
    }


    public void start(TimerTask ... timerTasks){
//        if(countDownTimer!=null)
//            this.countDownTimer.cancel();

        if(this.timer!=null)
            this.timer.cancel();
        timer = new Timer();



        if(timerTasks!=null && timerTasks.length > 0){
            // it is usually from a notification
            PERIOD_OF_TICK = 10;
            this.savedNotificationTask = timerTasks[0];
//            this.countDownTimer = timerTasks[0];
//            this.countDownTimer.start();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(milliSecondsElapsed%1000 == 0){
                        System.out.println("yeppppppppppppppppppskojgaoiuuguisfughuhfgsfig");
                        timerTasks[0].run();
                    }
                    incrementStopWatch();
                }
            }, 0, PERIOD_OF_TICK);

        }else{
            PERIOD_OF_TICK = 10;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    incrementStopWatch();
                    onTick();
                    if(activity != null) {
                        activity.runOnUiThread(StopWatch.this::updateTv);
                    }
                }
            },0,PERIOD_OF_TICK);

        }

        this.setRunning(true);
        this.setCreated(true);
        this.changeButtonLayout(false,true,true);
    }


    public void incrementStopWatch(){
        milliSecondsElapsed+=PERIOD_OF_TICK;
    }



    /**
     * if running stop,
     * else resume running
     */
    public void stop(TimerTask ... timerTasks){
        if(this.isRunning){
            this.cancel();
        }else{
            this.start(timerTasks);
        }

    }

    /**
     * cancels the timer
     * and sets running to false
     */
    public void cancel(){
        if(this.timer!=null) {
            this.timer.cancel();

        }
        this.setRunning(false);
    }

    public String getStopString(){
        return this.isRunning?"Pause":"Resume";
    }

    public int getStopColor(){
        return this.isRunning? activity.getColor(R.color.error_color):activity.getColor(R.color.colorPrimary);
    }

    public String getLapString(){
        return this.isRunning?"Lap":"Reset";
    }

    /**
     * creates one lap and adds it to the list
     * returns true if it is reseting the stop watch
     * false otherwise
     */
    public boolean lap(){
        if(isRunning){
            Lap lap = new Lap(milliSecondsElapsed, this.lapManager.getLaps().isEmpty()?
                    milliSecondsElapsed: milliSecondsElapsed - this.lapManager.getLaps().get(this.lapManager.getLaps().size()-1).getTimeInMilli());
            this.lapManager.add(lap);
            // make a lap
        }else{
            // rest this class
            this.reset();
            return true;
        }
        return false;

    }

    public int getLapsCount() {
        return this.lapManager.size();
    }

    public void reset(){
        this.cancel();
        this.lapManager.reset();
        this.milliSecondsElapsed = 0;
        this.updateTv();
        this.changeButtonLayout(true,false,false);
        this.setCreated(false);
        this.setRunning(false);
    }

    private void updateTv(){
        if(this.stopWatchTv!=null && updateTextViews){
            // no hour is needed
            String[] texts = TimeUtils.convertMilli(milliSecondsElapsed);
            stopWatchTv[0].setText(texts[1]);
            stopWatchTv[1].setText(texts[2]);
            stopWatchTv[2].setText(texts[3]);
        }
    }

    public abstract void onTick();


    void changeButtonLayout(boolean start, boolean stop, boolean lap) {
        if(this.buttons!=null && this.buttons.length == 3){
            Animation.animateNoTag(buttons[0],start ? View.VISIBLE : View.GONE,start? Animation.APPEAR: Animation.DISAPPEAR);
            Animation.animateNoTag(buttons[1],stop ? View.VISIBLE : View.GONE,stop? Animation.APPEAR: Animation.DISAPPEAR);
            Animation.animateNoTag(buttons[2],lap ? View.VISIBLE : View.GONE,lap? Animation.APPEAR: Animation.DISAPPEAR);
            //this.changeButtonText();
        }
    }

    public void changeButtonText(){
        if(isCreated && this.buttons!=null){
            this.buttons[1].setText(this.getStopString());
            this.buttons[2].setText(this.getLapString());
        }
    }

    public void startNotificationService(Context context){
        if(this.isCreated){
            Intent intent = new Intent(context, StopWatchService.class);
            ContextCompat.startForegroundService(context,intent);
        }
    }

    public void cancelNotificationService(Context context){
        if(this.isCreated) {
            Intent intent1 = new Intent(context, StopWatchService.class);
            context.stopService(intent1);
            boolean preIsRunning = this.isRunning;
            this.cancel();
            if(!preIsRunning){
                this.updateTv();
                this.changeButtonLayout(false,true,true);
            }else{
                this.start();
            }
            this.changeButtonText();
        }
    }


    private void setRunning(boolean r){
        this.isRunning = r;
    }


    public void setStopWatchTv(TextView ... stopWatchTv) {
        this.stopWatchTv = stopWatchTv;
        updateTv();
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setButtons(Button... buttons) {
        this.buttons = buttons;
        this.correctButtonLayout();
    }

    private void correctButtonLayout(){
        if(this.isRunning){
            this.changeButtonLayout(false,true,true);
        }else{
            this.changeButtonLayout(true,false,false);
        }
    }


    public void setCreated(boolean created) {
        isCreated = created;
    }

    public List<Lap> getLaps() {
        return lapManager.getLaps();
    }

    public long getMilliSecondsElapsed() {
        return milliSecondsElapsed;
    }

    public boolean isRunning() {
        return isRunning;
    }
    public boolean isCreated() {
        return isCreated;
    }


    public String getLapCounter(){
        return lapManager.getLapCounter();
    }

    public LapManager getMoLapManager() {
        return lapManager;
    }

    public StopWatch setUpdateTextViews(boolean updateTextViews) {
        this.updateTextViews = updateTextViews;
        return this;
    }
}
