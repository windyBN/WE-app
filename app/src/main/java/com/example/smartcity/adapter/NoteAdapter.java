package com.example.smartcity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.entity.Note;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.leancloud.AVFile;
/**
 * @Author he sheng yuan
 * @Date 2024/10/6 11:49
 * @Version 1.0
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnItemClickListener onItemClickListener;

    public NoteAdapter(List<Note> notes, OnItemClickListener onItemClickListener) {
        this.notes = notes;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Note note, int position);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        // 设置发布时间
        //holder.timeTextView.setText(note.getPublishTime());
        // 设置格式化后的时间
        holder.timeTextView.setText(note.getPublishTime());


        // 使用 loadImage 方法加载图像
        loadImage(holder.imageView, note.getImageUrl()); // 确保传入 imageUrl

        // 处理点击事件
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(note, position));

        // 处理点赞按钮点击事件
        holder.likeButton.setOnClickListener(v -> {
            if (holder.isLiked) {
                holder.likeButton.setImageResource(R.drawable.ic_like); // 恢复原图
                holder.isLiked = false; // 更新状态
            } else {
                holder.likeButton.setImageResource(R.drawable.ic_liked); // 点赞后的图标
                holder.isLiked = true; // 更新状态
            }
        });

        // 处理收藏按钮点击事件
        holder.favoriteButton.setOnClickListener(v -> {
            if (holder.isFavorited) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite); // 恢复原图
                holder.isFavorited = false; // 更新状态
            } else {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorited); // 收藏后的图标
                holder.isFavorited = true; // 更新状态
            }
        });

        // 处理评论按钮点击事件
        holder.commentButton.setOnClickListener(v -> {
            // 评论按钮的逻辑，这里可以打开评论界面或其他操作
        });
    }

    public static void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl) // 使用图像 URL
                    .placeholder(R.drawable.y0) // 可选：占位图
                    .error(R.drawable.y1) // 可选：错误图
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.y3); // 设置默认图像
        }
    }


    public static void loadImage(ImageView imageView, AVFile imageFile) {
        if (imageFile != null) {
            // 使用 Picasso 加载 AVFile 的 URL
            Picasso.get()
                    .load(imageFile.getUrl()) // 确保获取的是 URL
                    .placeholder(R.drawable.y0) // 可选：占位图
                    .error(R.drawable.y2) // 可选：错误图
                    .into(imageView);
        } else {
            // 如果 AVFile 为空，可以设置一个默认图像
            imageView.setImageResource(R.drawable.y3); // 默认图像
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public ImageButton likeButton, favoriteButton, commentButton;
        public ImageView imageView;
        TextView titleTextView, contentTextView, timeTextView; // 新增时间 TextView
        boolean isLiked = false; // 点赞状态
        boolean isFavorited = false; // 收藏状态

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            contentTextView = itemView.findViewById(R.id.note_content);
            imageView = itemView.findViewById(R.id.note_image);
            likeButton = itemView.findViewById(R.id.btn_like);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            commentButton = itemView.findViewById(R.id.btn_comment);
            timeTextView = itemView.findViewById(R.id.note_time); // 初始化时间 TextView
        }
    }



}
