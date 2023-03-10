package com.moh.alarmclock.Section;

import android.content.Context;
import android.webkit.ValueCallback;

import com.moh.alarmclock.ReadWrite.ReadWrite;
import com.moh.alarmclock.ReadWrite.Save;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.ArrayList;
import java.util.List;

public class SectionManager implements MoSavable, Save, MoLoadable {

    private final String FILE_NAME = "sectionmanager";

    public static final int ALARM_SECTION = 0 ;
    public static final int STOP_WATCH_SECTION = 1;
    public static final int TIMER_SECTION = 2;


    private int section;
    private List<ValueCallback<Integer>> onSectionChangedListener = new ArrayList<>();


    private static SectionManager ourInstance = new SectionManager();

    public static SectionManager getInstance() {
        return ourInstance;
    }

    private SectionManager() {
        this.section = ALARM_SECTION;
    }


    public void subscribe(ValueCallback<Integer> listener) {
        if (onSectionChangedListener.contains(listener))
            return;
        onSectionChangedListener.add(listener);
    }

    public void unsubscribe(ValueCallback<Integer> listener) {
        onSectionChangedListener.remove(listener);
    }

    @Override
    public void load(String data, Context context) {
        String value = ReadWrite.readFile(FILE_NAME,context);
        this.section = Integer.parseInt(value);
    }

    @Override
    public String getData() {
        return this.section+"";
    }

    @Override
    public void save(Context context) {
        ReadWrite.saveFile(FILE_NAME,this.getData(),context);
    }


    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
        onSectionChangedListener.forEach(integerValueCallback -> integerValueCallback.onReceiveValue(section));
    }

    public void onDestroy() {
        ourInstance.onSectionChangedListener.clear();
        ourInstance = null;
        ourInstance = new SectionManager();
    }
}
