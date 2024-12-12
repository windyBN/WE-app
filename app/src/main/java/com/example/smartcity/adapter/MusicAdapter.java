package com.example.smartcity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.entity.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<Music> musicList;
    private OnMusicClickListener listener;

    public interface OnMusicClickListener {
        void onMusicClick(Music music, int position);
    }

    public MusicAdapter(List<Music> musicList, OnMusicClickListener listener) {
        this.musicList = musicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.titleText.setText(music.getTitle());
        holder.artistText.setText(music.getArtist());
        holder.durationText.setText(music.getDuration());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMusicClick(music, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView artistText;
        TextView durationText;

        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            artistText = itemView.findViewById(R.id.text_artist);
            durationText = itemView.findViewById(R.id.text_duration);
        }
    }
} 