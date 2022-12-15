package com.moh.alarmclock.Clock;

import android.content.Context;

import androidx.annotation.NonNull;

import com.moh.alarmclock.List.List;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Repeating implements MoLoadable {

    public static final String EVERYDAY = "Everyday";

    private java.util.List<Integer> repeating;

    public static HashMap<Integer,Integer> map = new HashMap<Integer,Integer>(){{
        put(0, Calendar.SUNDAY);
        put(1, Calendar.MONDAY);
        put(2, Calendar.TUESDAY);
        put(3, Calendar.WEDNESDAY);
        put(4, Calendar.THURSDAY);
        put(5, Calendar.FRIDAY);
        put(6, Calendar.SATURDAY);
    }};

    /**
     *
     * @param days
     * integers from [Calendar.SUNDAY, etc]
     */
    public Repeating(Integer ... days){
        this.repeating = new ArrayList<>();
        for(Integer i : days) {
            this.repeating.add(map.get(i));
        }
    }

    public Repeating(java.util.List<Integer> days){
        this.repeating = new ArrayList<>();
        for(Integer i : days) {
            this.repeating.add(map.get(i));
        }
    }


    public boolean isEmpty(){
        return this.repeating.isEmpty();
    }

    public java.util.List<Integer> getRepeating() {
        return repeating;
    }

    public String readableFormat(){
        if(repeating.size() == 7){
            return EVERYDAY;
        }
        StringBuilder sb = new StringBuilder();
        for(Integer i:repeating){
            sb.append(firstLetterDay(i)).append(" ");
        }
        return sb.toString();
    }

    public static String readableFormat(java.util.List<Integer> repeating){
        Repeating r = new Repeating(repeating);
        return r.readableFormat();
    }

    public static String firstLetterDay(int day){
        switch (day){
            case Calendar.SUNDAY:
                return "SUN";
                case Calendar.MONDAY:
                    return "MON";
                    case Calendar.TUESDAY:
                        return "TUE";
                        case Calendar.WEDNESDAY:
                            return "WED";
                            case Calendar.THURSDAY:
                                return "THU";
                                case Calendar.FRIDAY:
                                    return "FRI";
                                    case Calendar.SATURDAY:
                                        return "SAT";
        }
        return "";
    }

    public void setRepeating(java.util.List<Integer> repeating) {
        this.repeating = repeating;
    }

    @NonNull
    @Override
    public String toString() {
        return this.repeating.toString();
    }


    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        this.repeating = new ArrayList<>();
        List.get(repeating, List.get(new ArrayList<>(),data));
    }
}
