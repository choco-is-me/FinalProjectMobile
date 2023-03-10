package com.moh.alarmclock.Color;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class Color {


    public static int[] color_text_on_highlight =
            new int[]{android.graphics.Color.parseColor("#FF9800"), android.graphics.Color.parseColor("#FF9800")};

    public static int[] color_text_disabled =
            new int[]{android.graphics.Color.parseColor("#F28E8E8E"), android.graphics.Color.parseColor("#F28E8E8E")};


    public static int[] color_text_on_normal =
            new int[]{android.graphics.Color.parseColor("#E9212427"), android.graphics.Color.parseColor("#F2FAFAFA")};

    public static int getColor(int[] id,Context context){
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            return id[1];
        }else{
            int s = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (s){
                case Configuration.UI_MODE_NIGHT_YES:
                    return id[1];
                    case Configuration.UI_MODE_NIGHT_NO:
                        return id[0];
            }

            return id[0];
        }
    }

}
