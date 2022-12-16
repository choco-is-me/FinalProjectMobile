package com.moh.alarmclock.Clock.StopWatch;

import android.content.Context;
import android.view.View;

import com.moh.alarmclock.Date.TimeUtils;
import com.moh.alarmclock.InflatorView.InflaterView;
import com.moh.alarmclock.InflatorView.ViewDisplayable;
import com.moh.alarmclock.R;
import com.moh.alarmclock.UI.TextView;

public class Lap implements ViewDisplayable {

    private long timeInMilli;
    // the difference between this and the last molap
    private long lastDiff;

    private boolean doneAnimation;


    public Lap(long tim, long ld){
        this.timeInMilli = tim;
        this.lastDiff = ld;
        this.doneAnimation = false;
    }

    public long getTimeInMilli() {
        return timeInMilli;
    }

    public void setTimeInMilli(long timeInMilli) {
        this.timeInMilli = timeInMilli;
    }

    public long getLastDiff() {
        return lastDiff;
    }

    public void setLastDiff(long lastDiff) {
        this.lastDiff = lastDiff;
    }

    public boolean isDoneAnimation() {
        return doneAnimation;
    }

    public void setDoneAnimation(boolean doneAnimation) {
        this.doneAnimation = doneAnimation;
    }

    /**
     * takes args and makes the class which implements it
     * displayable for the user
     * for example this can be used in the U.I
     *
     * @param args arguments that are passed to this method via other classes
     */
    @Override
    public View display(Object... args) {
        Context c = (Context) args[0];

        int index = (int) args[1] + 1;
        int status = (int) args[2];

        View view = InflaterView.inflate(R.layout.lap_item,c);

        android.widget.TextView indexTv = view.findViewById(R.id.index_lap);
        android.widget.TextView timeLap = view.findViewById(R.id.time_lap);
        android.widget.TextView diffTimeLap = view.findViewById(R.id.diff_time_lap);


        indexTv.setText(index<10?"0"+index:index+"");
        diffTimeLap.setText(TimeUtils.convertToStopWatchFormat(timeInMilli));
        timeLap.setText(TimeUtils.convertToStopWatchFormat(lastDiff));

        switch (status){
            case LapManager.HIGHEST_STATUS:
                TextView.syncColor(c.getColor(R.color.error_color),indexTv,timeLap,diffTimeLap);
                break;
            case LapManager.LOWEST_STATUS:
                TextView.syncColor(c.getColor(R.color.colorPrimary),indexTv,timeLap,diffTimeLap);
                break;
            case LapManager.NONE_STATUS:
                TextView.syncColor(c.getColor(R.color.color_text_disabled),indexTv,timeLap,diffTimeLap);
                break;
        }

        return view;
    }


}
