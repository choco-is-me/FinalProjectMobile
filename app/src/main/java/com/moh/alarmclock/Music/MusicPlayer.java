package com.moh.alarmclock.Music;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

public class MusicPlayer {


    private final long DURATION_INCREASE = 20000;
    private final long INCREASE_EVERY = 3000;

    private MediaPlayer mMediaPlayer;
    private AudioManager am;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;
    private boolean musicWasPlaying;
    private float currentVolume = 1f;



    public MusicPlayer(){}



    public void onDestroy(Context context){
        if(am!=null){
            am.abandonAudioFocus(focusChangeListener);
            focusChangeListener = null;
            //plays the music all the time
            if(musicWasPlaying) {
                Intent i = new Intent("com.android.music.musicservicecommand");
                i.putExtra("command", "play");
                context.sendBroadcast(i);
            }
        }
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    private  void play(Context context, Uri uri){
        mMediaPlayer = new MediaPlayer();
        Volume.setVolume(0,context);
        try{
            mMediaPlayer.setDataSource(context,uri);
        }catch (Exception ignore){
            try {
                mMediaPlayer.setDataSource(context,normalRingtone());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setVolume(currentVolume, currentVolume);
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.start();
        }
    }


    public void playStopOthers(Context context,Uri uri){
        focusChangeListener =
                focusChange -> {
                    switch (focusChange) {
                        case
                                (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            // Lower the volume while ducking.
                            mMediaPlayer.setVolume(0.2f, 0.2f);
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            mMediaPlayer.pause();
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS):
                            mMediaPlayer.stop();
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):

                            mMediaPlayer.setVolume(1f, 1f);

                            break;
                        default:
                            break;
                    }
                };

        am = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        int result = 0;
        if (am != null) {
            if(am.isMusicActive()){
                result = am.requestAudioFocus(focusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                musicWasPlaying = true;
            }else {
                result = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
                musicWasPlaying = false;
            }

        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            play(context,uri);
        }
    }


    private Uri normalRingtone(){
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    public void mute(boolean m){
        if(mMediaPlayer == null)
            return;
        if(m){
            mMediaPlayer.setVolume(0,0);
        }else{
            mMediaPlayer.setVolume(1,1);
        }
    }


    public void increaseVol(Context context){
        Volume.increaseVolume(context);
    }



}
