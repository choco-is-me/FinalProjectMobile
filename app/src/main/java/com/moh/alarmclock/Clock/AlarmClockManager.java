package com.moh.alarmclock.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.moh.alarmclock.Clock.ClockSugestions.ClockSuggestionManager;
import com.moh.alarmclock.Clock.Snooze.Snooze;
import com.moh.alarmclock.Date.Date;
import com.moh.alarmclock.Id.Id;
import com.moh.alarmclock.MainActivity;
import com.moh.alarmclock.Vibration.Vibration;
import com.moh.alarmclock.Vibration.VibrationTypes;
import com.moh.alarmclock.ReadWrite.ReadWrite;
import com.moh.alarmclock.Runnable.RunnableUtils;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AlarmClockManager implements Iterable<AlarmClock>, MoSavable, MoLoadable {


    public static String SET_ID = "id";
    private static final String FILE_NAME_ALARMS = "xcviui";

    private List<AlarmClock> clockList;
    private HashSet<Integer> reservedIds;
    public static Runnable refreshScreen;
    private int nextId;
    private static AlarmClockManager ourInstance = new AlarmClockManager();
    public static AlarmClockManager getInstance() {
        return ourInstance;
    }

    private AlarmClockManager() {
        this.clockList = new ArrayList<>();
        this.reservedIds = new HashSet<>();
        this.nextId = 0;
    }


    public void onDestroy() {
        ourInstance = null;
        ourInstance = new AlarmClockManager();
        refreshScreen = null;
    }

    public ArrayList<AlarmClock> getAlarms() {
        return (ArrayList<AlarmClock>) this.clockList;
    }


    public void addAlarm(AlarmClock c, Context context) {
        addAlarm(c, context, true);
    }

    public void addAlarm(AlarmClock c, Context context, boolean toast) {
        this.loadIfNotLoaded(context);
        if (this.clockList.contains(c)) {
            turnOn(c, context);
        } else {
            this.clockList.add(c);
        }
        if (toast) {
            Toast.makeText(context, "Alarm set for " +
                    c.getDate().getReadableDifference(Calendar.getInstance()), Toast.LENGTH_LONG).show();
        }
        ClockSuggestionManager.add(c, context);
        saveActivate(context);
    }


    public void createAlarm(Context context, String title, Date date, boolean snooze, boolean vibration, boolean music) {
        AlarmClock c = new AlarmClock();
        c.setId(AlarmClockManager.getInstance().getNextId());
        c.setTitle(title);
        c.setDateTime(date);
        c.setActive(true);
        c.setSnooze(new Snooze(context, snooze));
        c.setVibration(new Vibration(VibrationTypes.BASIC, vibration));
        c.setPathToMusic(music);
        c.setRepeating(new Repeating());
        addAlarm(c, context);
    }


    public void addAlarmNoToast(AlarmClock c, Context context) {
        addAlarm(c, context, false);
    }


    public boolean isEmpty() {
        return this.clockList.isEmpty();
    }

    private void turnOn(AlarmClock c, Context context) {
        for (AlarmClock clock : this.clockList) {
            if (clock.equals(c)) {
                clock.setActive(true);
                clock.setDateTime(c.getDateTime());
                clock.setTitle(c.getTitle());
                clock.setSnooze(c.getSnooze());
                clock.setVibration(c.getVibration());
                clock.setPathToMusic(c.hasMusic());
                clock.setRepeating(c.getRepeating());
                break;
            }
        }
    }

    public void save(Context context) {
        ReadWrite.saveFile(FILE_NAME_ALARMS, this.getData(), context);
    }

    public void saveActivate(Context context) {
        save(context);
        activateNextAlarm(context);
    }

    public void saveActivateRefresh(Context context) {
        save(context);
        activateNextAlarm(context);
        RunnableUtils.runIfNotNull(refreshScreen);
    }

    public void snoozeAlarm(int id, int minutes, Context context) {
        try {
            AlarmClock c = getAlarm(id);
            c.snooze(context);
            ClockSuggestionManager.add(c, context);
            saveActivateRefresh(context);
        } catch (EmptyAlarmException e) {
            Toast.makeText(context, "Failed to snooze", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public int getNextAlarmIndex() throws EmptyAlarmException {
        if (this.clockList.isEmpty() || !this.hasAnActiveClock())
            throw new EmptyAlarmException();
        int index = -1;
        for (int i = 0; i < this.clockList.size(); i++) {
            if (index == -1 && this.clockList.get(i).isActive()) {
                index = i;
            } else if (this.clockList.get(i).isActive()) {
                boolean iIsBeforeIndex = this.clockList.get(i).getDateTime()
                        .before(this.clockList.get(index).getDateTime());
                if (iIsBeforeIndex)
                    index = i;
            }
        }

        return index;
    }

    private boolean hasAnActiveClock() {
        for (AlarmClock c : this.clockList) {
            if (c.isActive()) {
                return true;
            }
        }
        return false;
    }

    public AlarmClock getNextAlarm() throws EmptyAlarmException {
        return this.clockList.get(this.getNextAlarmIndex());
    }


    public AlarmClock getAlarm(int id) throws EmptyAlarmException {
        for (AlarmClock c : this.clockList) {
            if (c.getId() == id) {
                return c;
            }
        }
        throw new EmptyAlarmException();
    }

    public void removeAlarm(Id id, Context context) {
        this.loadIfNotLoaded(context);
        try {
            AlarmClock c = this.getAlarm(id.getId());
            this.clockList.remove(c);
            ReadWrite.saveFile(FILE_NAME_ALARMS, this.getData(), context);
        } catch (EmptyAlarmException e) {
            System.out.println("There is no alarm with id: " + id);
        }
        this.activateNextAlarm(context);
    }

    public void removeAlarm(int index, Context context) {
        this.loadIfNotLoaded(context);
        this.cancelAlarm(this.clockList.get(index), context);
        this.clockList.remove(index);
        saveActivate(context);
    }

    public void removeAlarm(int index, Context context, boolean save) {
        this.loadIfNotLoaded(context);
        this.cancelAlarm(this.clockList.get(index), context);
        this.clockList.remove(index);
        if (save) {
            saveActivate(context);
        }
    }

    public void removeSelectedAlarms(Context context) {
        for (int i = this.clockList.size() - 1; i >= 0; i--) {
            if (this.clockList.get(i).isSelected()) {
                removeAlarm(i, context, false);
            }
        }
        saveActivate(context);
    }

    public int getSelectedCount() {
        int count = 0;
        for (AlarmClock c : clockList) {
            if (c.isSelected()) {
                count++;
            }
        }
        return count;
    }

    public void activateNextAlarm(Context c) {
        this.loadIfNotLoaded(c);
        for (AlarmClock clock : clockList) {
            if (clock.isActive()) {
                this.activateAlarm(clock, c);
            } else {
                this.cancelPendingIntent(clock, c);
            }
        }
    }

    private void activateAlarm(AlarmClock c, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(SET_ID, c.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, c.getId(), intent, 0);
        Intent i2 = new Intent(context, MainActivity.class);
        PendingIntent pi2 = PendingIntent.getActivity(context, 0, i2, 0);
        AlarmManager.AlarmClockInfo ac =
                new AlarmManager.AlarmClockInfo(c.getDateTime().getTimeInMillis(),
                        pi2);
        assert alarmMgr != null;
        alarmMgr.setAlarmClock(ac, pendingIntent);
    }

    public void cancelAlarm(AlarmClock c, Context activity) {
        AlarmManager alarmMgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, c.getId(), intent, 0);
        c.cancel();
        assert alarmMgr != null;
        alarmMgr.cancel(pendingIntent);
    }


    public void cancelAlarm(int id, Context context) throws EmptyAlarmException {
        loadIfNotLoaded(context);
        this.cancelAlarm(this.getAlarm(id), context);
        saveActivate(context);
    }

    public void cancelPendingIntent(AlarmClock c, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, c.getId(), intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    public Id getNextId() {
        nextId++;
        while (reservedIds.contains(nextId)) {
            nextId++;
        }
        reservedIds.add(nextId);
        return new Id(nextId);
    }


    @NonNull
    @Override
    public Iterator<AlarmClock> iterator() {
        return this.clockList.iterator();
    }

    @Override
    public String getData() {
        return MoFile.getData(this.clockList);
    }

    @Override
    public void load(String data, Context context) {
        this.clockList.clear();
        String[] components = MoFile.loadable(ReadWrite.readFile(FILE_NAME_ALARMS, context));
        if (MoFile.isValidData(components)) {
            String[] alarms = MoFile.loadable(components[0]);
            for (String a : alarms) {
                if (!a.isEmpty()) {
                    AlarmClock c = new AlarmClock();
                    c.load(a, context);
                    if (c.getId() > nextId) {
                        nextId = c.getId();
                    }
                    reservedIds.add(c.getId());
                    this.clockList.add(c);
                }
            }
        }
    }

    public void loadIfNotLoaded(Context c) {
        if (this.clockList.isEmpty()) {
            this.load("", c);
        }
    }

}