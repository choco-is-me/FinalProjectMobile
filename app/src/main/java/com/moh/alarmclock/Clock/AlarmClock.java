package com.moh.alarmclock.Clock;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.moh.alarmclock.Animation.Animation;
import com.moh.alarmclock.Clock.Snooze.Snooze;
import com.moh.alarmclock.Color.Color;
import com.moh.alarmclock.CreateAlarmActivity;
import com.moh.alarmclock.Date.Date;
import com.moh.alarmclock.Id.Id;
import com.moh.alarmclock.InflatorView.InflaterView;
import com.moh.alarmclock.InflatorView.ViewDisplayable;
import com.moh.alarmclock.MoVibration.Vibration;
import com.moh.alarmclock.MoVibration.VibrationTypes;
import com.moh.alarmclock.R;
import com.moh.alarmclock.UI.TextInput;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;

import java.util.Calendar;
import java.util.Objects;

public class AlarmClock implements MoSavable, ViewDisplayable, MoLoadable, MoSelectableItem {

    private Id id;
    private String title;
    private Vibration vibration;
    private Snooze snooze;
    private Repeating repeating;
    private Date dateTime;
    private boolean isActive;
    private boolean pathToMusic;


    // UI
    private TextView titleTextView;
    private TextView time;
    private TextView date;
    private TextView repeatingDays;
    private CheckBox checkBox;
    private SwitchMaterial active;
    private CardView cardView;
    private View clockLayoutView;


    private AlarmListView arrayAdapter;
    private MoAlarmClockListener listener;

    private boolean isSelected = false;
    public static boolean isInDeleteMode = false;

    public AlarmClock(){
        this.title = "";
        this.vibration = new Vibration(VibrationTypes.BASIC,false);
        this.snooze = new Snooze();
        this.repeating = new Repeating();
        this.dateTime = new Date();
        this.id = new Id();
        this.isActive = true;
        this.pathToMusic = true;
    }

    public AlarmClock setListener(MoAlarmClockListener listener) {
        this.listener = listener;
        return this;
    }

    public int getId() {
        return id.getId();
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public boolean hasMusic() {
        return pathToMusic;
    }

    public void setPathToMusic(boolean pathToMusic) {
        this.pathToMusic = pathToMusic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Vibration getVibration() {
        return vibration;
    }

    public void setVibration(Vibration vibration) {
        this.vibration = vibration;
    }

    public Snooze getSnooze() {
        return snooze;
    }

    public void setSnooze(Snooze snooze) {
        this.snooze = snooze;
    }

    public Repeating getRepeating() {
        return repeating;
    }

    public void setRepeating(Repeating repeating) {
        this.repeating = repeating;
        if(!this.repeating.isEmpty()){
            // if repeating is not empty we need to change the date
            this.dateTime.setCalendar(Date.getNextOccuring(this.repeating.getRepeating(),this.dateTime.getCalendar()));
            if (this.listener != null) {
                this.listener.onRepeatingChanged();
            }
        }
    }

    public Calendar getDateTime() {
        return dateTime.getCalendar();
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime.setCalendar(dateTime);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (listener != null) {
            listener.onActiveChanged(isActive);
        }
    }

    public void setActiveWithoutInvokingListener(boolean active) {
        isActive = active;
    }


    public void setInDeleteMode(boolean inDeleteMode) {
        isInDeleteMode = inDeleteMode;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Date getDate(){
        return this.dateTime;
    }

    public void snooze(Context context){
        // amount can be changed later for dynamic things
        this.isActive = true;
        //this is to make sure if the alarm has repeating, the repeating wont take over
        this.dateTime = new Date();
        this.snooze.applySnooze(this.dateTime,context);

    }

    public Id getMoId(){
        return this.id;
    }

    public void addDateField(int field, int amount){
        this.dateTime.getCalendar().add(field, amount);
    }

    public void setDateField(int field,int value){
        this.dateTime.getCalendar().set(field,value);
    }


    public String getReadableDifference(){
        return this.dateTime.getReadableDifference(Calendar.getInstance());
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(id.getData(),title,vibration,
                snooze.getData(),repeating,dateTime.getData(),isActive,pathToMusic);
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
        // we assume that the first element that is passed is the context
        Context c = (Context) args[0];
        // and assume that the second element is array adapter
        this.arrayAdapter = (AlarmListView) args[1];
        Boolean doDeleteAnimation = (Boolean) args[2];

        clockLayoutView = InflaterView.inflate(R.layout.card_view_alarm,c);
        assert clockLayoutView != null;


        initTexts();
        initCheckBox();
        initSwitches(c);
        initCardView(c);
        updateUIState(c,doDeleteAnimation);


        return clockLayoutView;
        // turn on or off

    }

    private void initCardView(Context c) {
        cardView = clockLayoutView.findViewById(R.id.alarm_card_view);
        cardView.setOnClickListener(view -> {
            if(!isInDeleteMode){
                // go to edit mode
                CreateAlarmActivity.clock = AlarmClock.this;
                Intent intent = new Intent(c, CreateAlarmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }else{
                // change selected
                onSelect(checkBox);
            }
        });
        cardView.setOnLongClickListener(view -> {
            if(!isInDeleteMode){
                arrayAdapter.activateDeleteMode();
                onSelect(checkBox);
            }
            return true;
        });
    }

    private void initSwitches(Context c) {
        active = clockLayoutView.findViewById(R.id.is_active_switch);
        active.setChecked(this.isActive);
    }

    private void initTexts() {
        titleTextView = clockLayoutView.findViewById(R.id.alarm_title_textView);
        if(this.title.isEmpty()){
            titleTextView.setVisibility(View.GONE);
        }
        titleTextView.setText(this.title);
        time = clockLayoutView.findViewById(R.id.alarm_time_textView);
        time.setText(this.dateTime.getReadableTime());
        date = clockLayoutView.findViewById(R.id.alarm_date_TextView);
        date.setText(this.dateTime.getReadableDate());
        repeatingDays = clockLayoutView.findViewById(R.id.repeatingDaysTextView);
        if(!this.repeating.isEmpty()){
            repeatingDays.setText(this.repeating.readableFormat());
        } else {
            repeatingDays.setVisibility(View.GONE);
        }
    }

    private void initCheckBox() {

    }

    /**
     * updates the alarm clock layout based on the different events
     * @param c
     */
    private void updateUIState(Context c, boolean doDeleteAnimation){
        if(isInDeleteMode){
            Animation.animate(active,View.INVISIBLE, Animation.DISAPPEAR,id,doDeleteAnimation);
        }else{
            Animation.animate(active,View.VISIBLE, Animation.APPEAR,id);
        }

        // changing the state of this check box
        if(isInDeleteMode){
            checkBox.setChecked(this.isSelected);
            Animation.animate(checkBox,View.VISIBLE, Animation.APPEAR,id,doDeleteAnimation);
        }else{
            Animation.animate(checkBox,View.GONE, Animation.DISAPPEAR,id);
        }


        /**
         * setting the correct color for textViews, if they are enabled or not
         */
        TextInput.setColorCondition(isActive, Color.color_text_on_highlight, Color.color_text_disabled,time,c);
        TextInput.setColorCondition(isActive, Color.color_text_on_highlight, Color.color_text_disabled,date,c);
        TextInput.setColorCondition(isActive, Color.color_text_on_highlight, Color.color_text_disabled,repeatingDays,c);
        TextInput.setColorCondition(isActive, Color.color_text_on_normal, Color.color_text_disabled,titleTextView,c);
    }


    private void onSelect(CheckBox checkBox){
        isSelected = !isSelected;
        checkBox.setChecked(isSelected);
        arrayAdapter.onSelect();
    }


    public String getReadableDateTime(){
        return this.dateTime.getReadableDate()+", "+this.dateTime.getReadableTime();
    }


    public void activate(Context context){
        if(this.repeating.isEmpty()){
            // work on date
            Calendar current = Calendar.getInstance();
            if(this.dateTime.getCalendar().before(current)){
                // the time of the current clock is less than what the time is right now in real life
                this.dateTime.set(Calendar.YEAR,current.get(Calendar.YEAR));
                this.dateTime.set(Calendar.MONTH,current.get(Calendar.MONTH));
                this.dateTime.set(Calendar.DATE,current.get(Calendar.DATE));
                // if works here as well but just in case
                while(this.dateTime.getCalendar().before(current)){
                    this.dateTime.getCalendar().add(Calendar.DATE,1);
                }
            }
            // check whether the date is also before
        }else{
            this.dateTime.setCalendar(Date.getNextOccuring(this.repeating.getRepeating(),this.dateTime.getCalendar()));
            // work on which day is the next
            // and set the date based on that
        }
        AlarmClockManager.getInstance().saveActivate(context);
    }


    public void cancel(){
        if(this.repeating.isEmpty()){
            this.setActive(false);
        } else {
            this.setRepeating(this.repeating);
        }
    }

    /**
     * loads a savable object into its class
     *
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] components = MoFile.loadable(data);
        this.id.load(components[0],context);
        this.title = components[1];
        this.vibration.load(components[2],context);
        this.snooze.load(components[3],context);
        this.repeating.load(components[4],context);
        this.dateTime.load(components[5],context);
        this.isActive = Boolean.parseBoolean(components[6]);
        this.pathToMusic = Boolean.parseBoolean(components[7]);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean onSelect() {
        this.isSelected = !this.isSelected;
        return this.isSelected;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmClock that = (AlarmClock) o;
        return this.dateTime.equals(that.dateTime) &&
                this.repeating.getRepeating().equals(that.repeating.getRepeating());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime,repeating.getRepeating());
    }

    public interface MoAlarmClockListener {
        void onActiveChanged(boolean isActive);
        void onRepeatingChanged();
    }
}
