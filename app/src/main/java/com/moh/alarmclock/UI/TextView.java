package com.moh.alarmclock.UI;

public class TextView {


    public static void syncColor(int color, android.widget.TextView... tvs){
        for(android.widget.TextView t: tvs){
            t.setTextColor(color);
        }
    }

}
