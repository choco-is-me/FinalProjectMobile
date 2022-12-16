package com.moh.alarmclock.Clock;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.moh.alarmclock.Runnable.Runnable;

import java.util.ArrayList;
import java.util.List;

public class AlarmListView extends ArrayAdapter<AlarmClock> {

    private Context context;
    private List<AlarmClock> alarms;
    private java.lang.Runnable update;

    private java.lang.Runnable deleteModeUI;
    private Runnable turnSelectAll;
    private Runnable turnDelete;
    private Runnable changeTitleRun;
    private boolean deleteAnimation;



    public AlarmListView(Context context, int resource, ArrayList<AlarmClock> objects, java.lang.Runnable r, java.lang.Runnable dmui, Runnable tsa, Runnable td, Runnable ct)
    {
        super(context, resource, objects);
        this.context = context;
        this.alarms = objects;
        this.update = r;
        this.deleteModeUI = dmui;
        this.turnSelectAll = tsa;
        this.turnDelete = td;
        this.changeTitleRun = ct;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.update.run();
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        final AlarmClock alarmClock = alarms.get(position);
        return alarmClock.display(context,
                this,
                deleteAnimation
        );
    }


    public void activateDeleteMode(){
        AlarmClock.isInDeleteMode = true;
        deleteAnimation = true;
        this.deleteModeUI.run();
        Handler handler = new Handler();
        handler.postDelayed(()-> {
            deleteAnimation = false;
        },200);
        notifyDataSetChanged();
    }


    public void deActivateDeleteMode(){
        AlarmClock.isInDeleteMode = false;
        for(AlarmClock a: alarms){
            a.setSelected(false);
        }
        notifyDataSetChanged();
    }


    private void changeSelectAll(){
        if(AlarmClock.isInDeleteMode){
            if(allIsSelected()){
                this.turnSelectAll.run(true);
            }else{
                this.turnSelectAll.run(false);
            }
        }
    }

    void onSelect(){
        changeSelectAll();
        showDelete();
        changeTitle();
    }

    private void changeTitle(){
        this.changeTitleRun.run(AlarmClockManager.getInstance().getSelectedCount());
    }


    private boolean allIsSelected(){
        for(AlarmClock c: alarms){
            if(!c.isSelected()){
                return false;
            }
        }
        return true;
    }


    private void showDelete(){
        if(AlarmClockManager.getInstance().getSelectedCount() > 0){
            turnDelete.run(true);
        }else{
            turnDelete.run(false);
        }
    }



    public void selectDeselectAll(boolean select){
        for(AlarmClock a: alarms){
            a.setSelected(select);
        }
        onSelect();
        notifyDataSetChanged();
    }




    public void setDeleteModeUI(java.lang.Runnable r){
        this.deleteModeUI = r;
    }




}
