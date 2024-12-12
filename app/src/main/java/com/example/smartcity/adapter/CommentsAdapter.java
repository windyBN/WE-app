package com.example.smartcity.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartcity.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
/**
 * @Author he sheng yuan
 * @Date 2024/10/11 21:49
 * @Version 1.0
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private final List<AVObject> comments;

    public CommentsAdapter(List<AVObject> comments) {
        this.comments = comments;
    }



    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        AVObject comment = comments.get(position);  // 获取评论对象
        holder.commentText.setText(comment.getString("text"));  // 设置评论内容

        // 获取并处理作者信息 (从 user 字段中获取 AVUser 对象)
        AVUser author = comment.getAVObject("user");  // 使用 getAVObject 加载关联的用户对象
        if (author != null) {
            String nickname = author.getString("nickname") != null ?
                    author.getString("nickname") : "匿名";
            holder.userNickname.setText(nickname);
            Log.d("CommentsAdapter", "Author: " + (author != null ? author.getObjectId() : "null"));
            String avatarUrl = author.getString("avatar");
            // 打印头像路径
            Log.d("CommentsAdapter", "头像 URL: " + avatarUrl);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(holder.userAvatar.getContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.h5)  // 占位图
                        .error(R.drawable.h1)        // 加载失败时显示的图片
                        .into(holder.userAvatar);
            } else {
                holder.userAvatar.setImageResource(R.drawable.h1);  // 默认头像
            }
        } else {
            holder.userNickname.setText("未知用户");
            holder.userAvatar.setImageResource(R.drawable.h5);  // 默认头像
        }

        // 设置评论时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.commentTime.setText(sdf.format(comment.getCreatedAt()));
    }





    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        TextView userNickname;  // 用户昵称
        ImageView userAvatar;   // 用户头像
        TextView commentTime;   // 评论时间

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_text);
            userNickname = itemView.findViewById(R.id.user_nickname);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }
}
