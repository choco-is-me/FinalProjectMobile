package com.moh.alarmclock.Clock.Snooze;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.moh.alarmclock.Date.Date;
import com.moh.alarmclock.R;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.Calendar;

public class Snooze implements MoSavable, MoLoadable {

    private final String SNOOZE_REPEAT = "3";
    private final String SNOOZE_TIME = "5";
    private final int INFINITE_SNOOZE = -1;


    private SnoozeInterval interval;
    private boolean isActive;

    public Snooze(){
        this.interval = new SnoozeInterval(5,3);
        this.isActive = true;
    }

//    public Snooze(int wt, int r, boolean active){
//        this.interval = new SnoozeInterval(wt,r);
//        this.isActive = active;
//    }

    public Snooze(Context context, boolean isActive){
        this.isActive = isActive;
        initInterval(context);
    }

    private void initInterval(Context context) {
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        int r,wt;
        try{
            r = Integer.parseInt(s.getString(context.getString(R.string.snooze_repeat),SNOOZE_REPEAT));
        }catch(Exception e){
            r = Integer.parseInt(SNOOZE_REPEAT);
        }
        try{
            wt = Integer.parseInt(s.getString(context.getString(R.string.snooze_time),SNOOZE_TIME));
        }catch (Exception e){
            wt = Integer.parseInt(SNOOZE_TIME);
        }
        this.interval = new SnoozeInterval(wt,r);
    }

    /**
     * if this is active and number of repeats
     * is more than 0 (which means we can call snooze one more time)
     * @return
     */
    public boolean isActive() {
        return isActive && (interval.repeats>0 || interval.repeats == INFINITE_SNOOZE);
    }

    public void applySnooze(Date date, Context context){
        date.add(Calendar.MINUTE,interval.waitTime);
        if (interval.repeats != INFINITE_SNOOZE) {
            interval.repeats--;
        }
        Toast.makeText(context,"Alarm snoozed for " + interval.waitTime +
                " minutes from now",Toast.LENGTH_LONG).show();
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public SnoozeInterval getInterval() {
        return interval;
    }

    public void setInterval(SnoozeInterval interval) {
        this.interval = interval;
    }


    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(this.isActive,this.interval.getData());
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] comps = MoFile.loadable(data);
        this.isActive = Boolean.parseBoolean(comps[0]);
        this.interval.load(comps[1],context);
    }
}
