package com.example.mediaplayer;


import android.content.ContentResolver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.Adapter.SongAdapter;


import java.io.File;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private ArrayList<File> songs=new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSongList();
        DisplayListSong();


    }




    protected void DisplayListSong() {

        RecyclerView listSong = findViewById(R.id.songs);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listSong.setLayoutManager(layoutManager);
        SongAdapter songAdapter = new SongAdapter(this,songs);
        listSong.setAdapter(songAdapter);

    }
    protected void getSongList() {
            Log.d("Get song", "OK");

            // Query external audio resources
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            // Iterate over results if valid
            if (musicCursor != null&& musicCursor.moveToFirst() ) {
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                //int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

                do {

                    Log.d("Get File", "OK");
                    String s=musicCursor.getString(idColumn);
                    if(!s.contains(".ogg")) {
                        File file = new File(s);
                        songs.add(file);
                    }
                }
                while (musicCursor.moveToNext());
            }


    }




}