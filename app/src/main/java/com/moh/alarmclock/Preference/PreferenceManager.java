package com.moh.alarmclock.Preference;

import android.content.SharedPreferences;

import java.util.HashMap;

public class PreferenceManager {


    private HashMap<String, Preference> map = new HashMap<>();



    public PreferenceManager add(Preference pref){
        map.put(pref.getKey(),pref);
        return this;
    }


    @SuppressWarnings("ConstantConditions")
    public void update(SharedPreferences sp, String key){
        if(map.containsKey(key)){
            map.get(key).updateSummary(sp.getAll().get(key));
        }
    }


}
