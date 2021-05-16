package com.example.mediaplayer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class PlayerActivity extends AppCompatActivity {
    private MusicService musicService;
    private boolean musicBound = false;

    private ArrayList<File> songs;
    private int position;
    private ImageView musicNote;
    private TextView txtTitle, txtStart, txtEnd, txtTimes;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnNext, btnPrevious, btnShuffle, btnRepeat;
    private Thread update;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intentData = getIntent();
        Bundle bundle = intentData.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("position", 0);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, musicConnection, BIND_AUTO_CREATE);

        musicNote = findViewById(R.id.musicNote);
        txtTitle=findViewById(R.id.titleName);
        txtStart = findViewById(R.id.txt_start);
        txtEnd = findViewById(R.id.txt_end);
        btnPlay = findViewById(R.id.btn_play);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        btnShuffle = findViewById(R.id.btn_shuffle);
        btnRepeat = findViewById(R.id.btn_repeat);
        seekBar = findViewById(R.id.seek_bar);
        txtTimes = findViewById(R.id.repeatTimes);
        txtTitle.setText(songs.get(position).getName().replace(".mp3", ""));
        setClickAction();


    }



    private void setClickAction() {


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.isPlaying()) {
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
                    musicService.pausePlayer();
                } else {
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    musicService.startPlayer();

                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playNext();
                position = musicService.getSongPosition();
                reset();


            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPrev();
                position = musicService.getSongPosition();
                reset();


            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.isShuffle()) {
                    musicService.setShuffle(false);
                } else musicService.setShuffle(true);
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (musicService.getRepeatTimes()) {
                    case 0:
                        txtTimes.setText("1");
                        musicService.setRepeatTimes(1);
                        break;
                    case 1:
                        txtTimes.setText("2");
                        musicService.setRepeatTimes(2);
                        break;
                    case 2:
                        txtTimes.setText("");
                        musicService.setRepeatTimes(0);
                        break;

                }


            }
        });


    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;
            musicService.setSongList(songs);
            musicService.setSongPosition(position);
            musicService.playSong();
            startThread();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    protected void startThread()
    {
        update=new Thread()
        {
            @Override
            public void run() {
                super.run();
                int totalDuration;
                int current=0;
                try {
                    sleep(500);
                    totalDuration= musicService.getDuration();
                    seekBar.setMax(totalDuration);


                    while (current<totalDuration)
                    {

                            sleep(500);
                            current=musicService.getCurrentPosition();
                            seekBar.setProgress(current);
                            current=0;
                            totalDuration=musicService.getDuration();


                    }
                }
                catch (InterruptedException|IllegalStateException e)
                {
                    e.printStackTrace();

                }



            }
        };

        update.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seek(seekBar.getProgress());
            }
        });

        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startAnimation();
                handler.postDelayed(this,500);
                txtEnd.setText(createTime(musicService.getDuration()));
                txtStart.setText(createTime(musicService.getCurrentPosition()));
                updateRepeat();
                if(position!=musicService.getSongPosition()) {
                   reset();
                }


            }
        },delay);

    }


    @Override
    protected void onDestroy() {
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = true;
        }

        super.onDestroy();
    }
    private void reset()
    {
        position=musicService.getSongPosition();
        txtStart.setText("0:00");
        txtEnd.setText(createTime(musicService.getDuration()));
        txtTitle.setText(songs.get(position).getName().replace(".mp3", ""));
    }
    private void updateRepeat()
    {
      int times= musicService.getRepeatTimes();
      if(times==0) txtTimes.setText("");
      else txtTimes.setText(String.valueOf(times));

    }


    private String createTime(int duration) {
        String time = "";
        int minutes = duration/1000 / 60;
        int seconds = duration/1000% 60;
        time += minutes + ":";
        if(seconds<10)
        {
            time+="0";
        }
        time+=seconds;
        return time;
    }

    private void startAnimation() {
        ObjectAnimator animator=ObjectAnimator.ofFloat(musicNote,"Rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

}
