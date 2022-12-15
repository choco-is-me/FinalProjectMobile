package com.moh.alarmclock.Clock.AlarmSession;

import com.moh.alarmclock.Clock.AlarmClock;
import com.moh.alarmclock.Clock.AlarmClockManager;
import com.moh.alarmclock.Clock.EmptyAlarmException;

public class Information {


    private  String title;
    private  String leftButton;
    private  String rightButton;
    private  InitAlarmSession.Type type;

    // only if its an alarm clock
    private int id;
    private AlarmClock clock;

    public Information(String t, InitAlarmSession.Type type){
        this.title = t;
        this.type = type;
    }

    public Information(String t, InitAlarmSession.Type type, int i){
        this.title = t;
        this.type = type;
        this.id = i;
        if(type == InitAlarmSession.Type.CLOCK){
            try {
                this.clock = AlarmClockManager.getInstance().getAlarm(id);
            } catch (EmptyAlarmException e) {
                //e.printStackTrace();
            }
        }
    }

    public AlarmClock getClock(){
        return this.clock;
    }

    public boolean isClock() {
        return this.type == InitAlarmSession.Type.CLOCK;
    }


    public void changeTitleIfEmpty(String t){
        if(this.title.isEmpty()){
            this.title = t;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public InitAlarmSession.Type getType() {
        return type;
    }

    public void setType(InitAlarmSession.Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLeftButton(String leftButton) {
        this.leftButton = leftButton;
    }

    public void setRightButton(String rightButton) {
        this.rightButton = rightButton;
    }

    public String getLeftButton() {
        return leftButton;
    }

    public String getRightButton() {
        return rightButton;
    }
}
