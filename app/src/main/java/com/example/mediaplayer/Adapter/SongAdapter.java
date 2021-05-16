package com.example.mediaplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.PlayerActivity;
import com.example.mediaplayer.R;

import java.io.File;
import java.util.ArrayList;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context context;
    private ArrayList<File> songs;

    public SongAdapter(Context context , ArrayList<File> songs) {
        this.context = context;
        this.songs=songs;

    }
    public static final class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);

        }
    }
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        holder.title.setText(songs.get(position).getName().replace(".mp3","").replace(".ogg",""));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent =new Intent(context,PlayerActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("songs",songs);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


}
