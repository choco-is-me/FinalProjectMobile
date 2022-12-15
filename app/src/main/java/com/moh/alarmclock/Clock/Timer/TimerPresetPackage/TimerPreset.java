package com.moh.alarmclock.Clock.Timer.TimerPresetPackage;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.moh.alarmclock.Date.TimeUtils;
import com.moh.alarmclock.InflatorView.InflaterView;
import com.moh.alarmclock.InflatorView.ViewDisplayable;
import com.moh.alarmclock.R;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

public class TimerPreset implements MoSavable, MoLoadable, ViewDisplayable {


    public static boolean isInDeleteMode = false;

    private static final String SEP_KEY = "motimerpresetsepkey";

    private String name;
    private long milliseconds;
    private boolean isSelected;

    public TimerPreset(String n, long ms){
        this.name = n;
        this.milliseconds = ms;
    }

    TimerPreset(){

    }

    public String getReadableTime(){
        return TimeUtils.convertToReadableFormat(this.milliseconds);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void click(){
        this.isSelected = !this.isSelected;
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] parts = MoFile.loadable(data);
        this.name = parts[0];
        this.milliseconds = Long.parseLong(parts[1]);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(this.name,this.milliseconds);
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
        Context context = (Context)args[0];
        View v = InflaterView.inflate(R.layout.timer_preset_layout,context);

        TextView title = v.findViewById(R.id.timer_preset_text);
        TextView time = v.findViewById(R.id.timer_preset_time);

        title.setText(this.name);
        time.setText(TimeUtils.convertToReadableFormat(this.milliseconds));

        return v;
    }
}
