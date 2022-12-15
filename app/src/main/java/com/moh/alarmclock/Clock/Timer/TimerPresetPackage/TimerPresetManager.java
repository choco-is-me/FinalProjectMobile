package com.moh.alarmclock.Clock.Timer.TimerPresetPackage;

import android.content.Context;

import com.moh.alarmclock.ReadWrite.ReadWrite;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;

import java.util.ArrayList;

public class TimerPresetManager {

    private static final String FILE_NAME = "timer_presets";

    private static ArrayList<TimerPreset> presets = new ArrayList<>();


    public static void add(TimerPreset preset, Context context) {
        presets.add(preset);
        save(context);
    }


    public static void remove(int index, Context context) {
        presets.remove(index);
        save(context);
    }

    public static int size() {
        return presets.size();
    }


    public static void save(Context context) {
        ReadWrite.saveFile(FILE_NAME, MoFile.getData(presets), context);
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    public static void load(String data, Context context) {
        if (presets.isEmpty()) {
            String[] parts = MoFile.loadable(ReadWrite.readFile(FILE_NAME, context));
            if (MoFile.isValidData(parts)) {
                String[] presetsData = MoFile.loadable(parts[0]);
                for (String s : presetsData) {
                    if (!s.isEmpty()) {
                        TimerPreset preset = new TimerPreset();
                        preset.load(s, context);
                        presets.add(preset);
                    }
                }
            }
        }
    }


    /**
     * returns the size of selected preset items
     *
     * @return
     */
    public static int getSelectedSize() {
        int i = 0;
        for (TimerPreset p : presets) {
            if (p.isSelected()) {
                i++;
            }
        }
        return i;
    }

    /**
     * sets the selected boolean to b
     *
     * @param b
     */
    public static void selectAllPresets(boolean b) {
        for (TimerPreset p : presets) {
            p.setSelected(b);
        }
    }

    /**
     * deletes all the selected presets
     */
    public static void deleteSelected(Context context) {
        for (int i = presets.size() - 1; i >= 0; i--) {
            if (presets.get(i).isSelected()) {
                presets.remove(i);
            }
        }
        save(context);
    }

    public static ArrayList<TimerPreset> getPresets() {
        return presets;
    }
}
