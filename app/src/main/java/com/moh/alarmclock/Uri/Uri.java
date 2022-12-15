package com.moh.alarmclock.Uri;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class Uri {

    public static android.net.Uri get(Context c, int sharedPrefString){
        String customUri = PreferenceManager.getDefaultSharedPreferences(c)
                .getString(c.getString(sharedPrefString),"");
        if(!customUri.isEmpty()){
            try{
                // check to see if the uri is working before returning it
                android.net.Uri u = android.net.Uri.parse(customUri);
                return u;
            }catch(Exception ignore){}
        }
        return null;
    }

}
