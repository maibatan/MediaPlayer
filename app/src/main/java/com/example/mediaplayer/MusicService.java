package com.example.mediaplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {


    private final IBinder musicBind = new MusicBinder();
    private MediaPlayer player;
    private ArrayList<File> songs;
    private int songPos;
    private boolean shuffle=false;
    private int repeatTimes=0;
    private int duration=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        initMusicPlayer();

        return musicBind;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();

    }
    @Override
    public void onCompletion(MediaPlayer mp) {

            mp.reset();
            playNext();


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        duration=player.getDuration();
        mp.start();
        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification.Builder nBuilder = new Notification.Builder(this);
        nBuilder.setContentIntent(pendingIntent)
                .setTicker(songs.get(songPos).getName().replace(".mp3","").replace(".ogg",""))
                .setSmallIcon(R.drawable.ic_baseline_play_circle_outline_24)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songs.get(songPos).getName().replace(".mp3","").replace(".ogg",""));
        Notification notification = nBuilder.getNotification();
        startForeground(1, notification);
    }

    public void playSong() {
        player.reset();
        Uri trackUri = Uri.parse(songs.get(songPos).toString());

        try {
            player.setDataSource(this, trackUri);

        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error starting data source", e);
        }
        player.prepareAsync();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
    }



    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }


    public void playPrev() {

        if(repeatTimes==0) {
            if(shuffle){
                Random r=new Random();
                int randomPos=(songPos-r.nextInt(songs.size())+songs.size())%songs.size();
                while(randomPos==songPos)
                {
                    randomPos=(songPos-r.nextInt(songs.size()) +songs.size())%songs.size();
                }
                songPos=randomPos;
                playSong();
            }
            else {
                songPos = (songPos-1+songs.size())%songs.size();
                playSong();
            }
        }
        if(repeatTimes==1) {
            playSong();

            repeatTimes=0;
        }
        if(repeatTimes==2) {
            playSong();


        }
    }

    public void playNext() {
        if(repeatTimes==0) {
            if(shuffle){
                Random r=new Random();
                int randomPos=(songPos+r.nextInt(songs.size())+songs.size())%songs.size();
                while(randomPos==songPos)
                {
                    randomPos=(songPos+r.nextInt(songs.size())+songs.size())%songs.size();
                }
                songPos=randomPos;
                playSong();
            }
            else {
                songPos = (songPos+1)%songs.size();
            playSong();
            }
        }
        if(repeatTimes==1) {
            playSong();

            repeatTimes=0;
        }
        if(repeatTimes==2) {
            playSong();


        }
    }



    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public int getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes%3;
    }
    public void setSongPosition(int songIndex) {
        songPos = songIndex;
    }
    public int getSongPosition(){return songPos;}
    public void setSongList(ArrayList<File> songs)
    {
        this.songs=songs;


    }

    public int getCurrentPosition() {

        return player.getCurrentPosition();
    }

    public int getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }


    public void pausePlayer() {
        player.pause();
    }

    public void seek(int pos) {
        player.seekTo(pos);
    }

    public void startPlayer() {
        player.start();
    }

}






