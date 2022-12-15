package com.moh.alarmclock.Clock.MoAlarmSession;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.moh.alarmclock.Date.MoDate;
import com.moh.alarmclock.Id.MoId;
import com.moh.alarmclock.MoVibration.MoVibration;
import com.moh.alarmclock.Music.MoMusicPlayer;
import com.moh.alarmclock.Music.MoVolume;
import com.moh.alarmclock.Notification.MoNotificationChannel;
import com.moh.alarmclock.R;
import com.moh.alarmclock.Uri.MoUri;

public class MoNotificationAlarmSession extends Service {

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
    public static MoInformation moInformation;
    private static MoMusicPlayer moMusicPlayer;
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
        if (MoInitAlarmSession.list.isEmpty())
            return;
        moInformation = MoInitAlarmSession.list.remove();
        int imp;
        // imp = NotificationManager.IMPORTANCE_HIGH;
        imp = NotificationManager.IMPORTANCE_MAX;
        MoNotificationChannel.createNotificationChannel(NAME, DESCRIPTION, this, CHANNEL_ID_ALARM, imp);
        // we need to assign a different id each time to show the
        // heads up notification (otherwise the system wouldn't do it)
        FORE_GROUND_SERVICE_ID = MoId.getRandomInt();
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
        if (moMusicPlayer != null) {
            moMusicPlayer.onDestroy(this);
        }
        MoVibration.cancel();
        MoVibration.vibrateOnce(this,200);
        MoAlarmWakeLock.releaseCpuLock();
        if(destroySituation  == SITUATION_ALARM_STOP) {
            // only show this if the alarm was stopped
            Toast.makeText(this,ALARM_STOPPED,Toast.LENGTH_SHORT).show();
        }
        resetSituation();
    }




    public Notification notification(Context context) {
        Intent fullScreenIntent = new Intent(context, MoAlarmSessionActivity.class);
        fullScreenIntent.putExtra(MoAlarmSessionActivity.EXTRA_INFO, CHANNEL_ID_ALARM);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_ONE_SHOT);

        playSound(context);
        vibrate();

        MoDate date = new MoDate();

        NotificationCompat.Builder customNotification;
        int importance;
        importance = NotificationManager.IMPORTANCE_HIGH;

        PreferenceManager.getDefaultSharedPreferences(context);
        moInformation.getClock().getSnooze().isActive();

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
        Intent pendingIntent = new Intent(context, MoAlarmSessionBroadCast.class);
        pendingIntent.setAction(action);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private Uri getAlarmUri() {
        // checking to see if user has set a custom alert for this
        Uri customAlert = MoUri.get(this,R.string.alarm_music);
        if(customAlert!=null){
            return customAlert;
        }
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private void vibrate() {
        if (!moInformation.getClock().getVibration().isActive()) {
            return;
        }
        MoVibration.vibrate(this, 2000);
    }


    private void playSound(Context context) {
        if (!moInformation.getClock().hasMusic()) {
            // if they said no music just return
            return;
        }
        moMusicPlayer = new MoMusicPlayer();
        // making the initial volume zero and then increasing it every INCREASE_EVERY
        MoVolume.setVolume(0, this);
        moMusicPlayer.playStopOthers(context,getAlarmUri());
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
        if(moMusicPlayer==null)
            return;
        moMusicPlayer.mute(m);
    }



    private void increaseVol(){
        MoVolume.increaseVolume(this);
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
