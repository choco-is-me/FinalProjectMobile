package com.moh.alarmclock.Clock.AlarmSession;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.moh.alarmclock.Date.Date;
import com.moh.alarmclock.Id.Id;
import com.moh.alarmclock.Vibration.Vibration;
import com.moh.alarmclock.Music.MusicPlayer;
import com.moh.alarmclock.Music.Volume;
import com.moh.alarmclock.Notification.NotificationChannel;
import com.moh.alarmclock.R;
import com.moh.alarmclock.Uri.Uri;

public class NotificationAlarmSession extends Service {

    public final static String CHANNEL_ID_ALARM = "ascid";

    public final static String NAME = "Alarm";
    public final static String DESCRIPTION = "Alarm session";
    public final static String ALARM_STOPPED = "Alarm stopped";

    public final static int REQUEST_CODE = 12;
    public static int FORE_GROUND_SERVICE_ID = 10;

    public final static int SITUATION_ALARM_STOP = 0;
    public final static int SITUATION_ALARM_SNOOZE = 1;


    public final static String STOP_ACTION = "Stop";
    public final static String SNOOZE_ACTION = "Snooze";
    public final static String OPEN_ACTION = "open";

    private final long DURATION_INCREASE = 20000;
    private final long INCREASE_EVERY = 3000;


    private CountDownTimer volumeTimer;
    public static Information information;
    private static MusicPlayer musicPlayer;
    // what situation are we in when the user presses destroy
    private static int destroySituation;
    private int volCount = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (InitAlarmSession.list.isEmpty())
            return;
        information = InitAlarmSession.list.remove();
        int imp;
        // imp = NotificationManager.IMPORTANCE_HIGH;
        imp = NotificationManager.IMPORTANCE_MAX;
        NotificationChannel.createNotificationChannel(NAME, DESCRIPTION, this, CHANNEL_ID_ALARM, imp);
        // we need to assign a different id each time to show the
        // heads up notification (otherwise the system wouldn't do it)
        FORE_GROUND_SERVICE_ID = Id.getRandomInt();
        startForeground(FORE_GROUND_SERVICE_ID, notification(this));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (volumeTimer != null) {
            volumeTimer.cancel();
        }
        if (musicPlayer != null) {
            musicPlayer.onDestroy(this);
        }
        Vibration.cancel();
        Vibration.vibrateOnce(this,200);
        AlarmWakeLock.releaseCpuLock();
        if(destroySituation  == SITUATION_ALARM_STOP) {
            // only show this if the alarm was stopped
            Toast.makeText(this,ALARM_STOPPED,Toast.LENGTH_SHORT).show();
        }
        resetSituation();
    }




    public Notification notification(Context context) {
        Intent fullScreenIntent = new Intent(context, AlarmSessionActivity.class);
        fullScreenIntent.putExtra(AlarmSessionActivity.EXTRA_INFO, CHANNEL_ID_ALARM);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_ONE_SHOT);

        playSound(context);
        vibrate();

        Date date = new Date();

        NotificationCompat.Builder customNotification;
        int importance;
        importance = NotificationManager.IMPORTANCE_HIGH;

        PreferenceManager.getDefaultSharedPreferences(context);
        information.getClock().getSnooze().isActive();

        customNotification = new NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setContentTitle(context.getString(R.string.generic_alarm))
                .setContentText(date.getReadableDate() + " " + date.getReadableTime())
                .setPriority(importance)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setContentIntent(fullScreenPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        return customNotification.build();
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    public static PendingIntent getAction(Context context, String action) {
        Intent pendingIntent = new Intent(context, AlarmSessionBroadCast.class);
        pendingIntent.setAction(action);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private android.net.Uri getAlarmUri() {
        // checking to see if user has set a custom alert for this
        android.net.Uri customAlert = Uri.get(this,R.string.alarm_music);
        if(customAlert!=null){
            return customAlert;
        }
        android.net.Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private void vibrate() {
        if (!information.getClock().getVibration().isActive()) {
            return;
        }
        Vibration.vibrate(this, 2000);
    }


    private void playSound(Context context) {
        if (!information.getClock().hasMusic()) {
            // if they said no music just return
            return;
        }
        musicPlayer = new MusicPlayer();
        // making the initial volume zero and then increasing it every INCREASE_EVERY
        Volume.setVolume(0, this);
        musicPlayer.playStopOthers(context,getAlarmUri());
        this.volumeTimer = new CountDownTimer(DURATION_INCREASE, INCREASE_EVERY) {
                    @Override
                    public void onTick(long l) {
                        if(volCount!=0) {
                            increaseVol();
                        }
                        volCount++;
                    }
                    @Override
                    public void onFinish() {}
        }.start();
    }

    // mute the media player if m
    public static void mute(boolean m){
        if(musicPlayer ==null)
            return;
        musicPlayer.mute(m);
    }



    private void increaseVol(){
        Volume.increaseVolume(this);
    }


    /**
     * these following methods are used to
     * see whether user snoozed or stopped the alarm
     * from activity or from the notification
     */

    public static void resetSituation(){
        destroySituation = -1;
    }
    public static void snoozeSituation(){
        destroySituation = SITUATION_ALARM_SNOOZE;
    }
    public static void alarmSituation(){
        destroySituation = SITUATION_ALARM_STOP;
    }


}
