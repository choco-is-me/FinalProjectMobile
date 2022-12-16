package com.moh.alarmclock.Clock.StopWatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.moh.alarmclock.Animation.Animation;

import java.util.List;

public class LapListAdapter extends ArrayAdapter<Lap> {


    private Context context;
    private List<Lap> laps;


    public LapListAdapter(Context context, int resource, List<Lap> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.laps = objects;
    }

    //called when rendering the list
    public View getView(final int position, final View convertView, ViewGroup parent) {

        //get the property we are displaying
        final Lap lap = laps.get(laps.size() - (position + 1));
        //get the inflater and inflate the XML layout for each item
        //final View view = conversation.getView(context);
        int index = laps.size() - (position + 1);
        View v = lap.display(context,index, StopWatch.universal.getMoLapManager().getStatus(lap));
        //TODO animation does not work as intended (fix it)
        if(!lap.isDoneAnimation()){
            // only animate the last element
//            if(index%2==0){
//                v.startAnimation(Animation.get(Animation.LEFT_TO_RIGHT));
//            }else{
//                v.startAnimation(Animation.get(Animation.RIGHT_TO_LEFT));
//            }
            v.startAnimation(Animation.get(Animation.HALF_TOP_TO_BOTTOM));

            lap.setDoneAnimation(true);
        }
        return v;
    }

}
