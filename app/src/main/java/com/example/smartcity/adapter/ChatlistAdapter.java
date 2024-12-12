package com.example.smartcity.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.entity.Chatlist;

import java.util.List;

/**
 * @Author he sheng yuan
 * @Date 2024/10/15 16:58
 * @Version 1.0
 */
public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ViewHolder> {

    private Context context;
    private List<Chatlist> chatList;

    public ChatlistAdapter(Context context, List<Chatlist> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chatlist chat = chatList.get(position);
        
        // 设置基本信息
        holder.senderTextView.setText(chat.getSender());
        holder.messageTextView.setText(chat.getMessage());
        holder.timeTextView.setText(chat.getTime());

        // 根据发送者类型设置布局
        if (chat.getSender().equals("AI助手")) {
            // AI消息靠左
            holder.infoLayout.setGravity(Gravity.START);
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        } else {
            // 用户消息靠右
            holder.infoLayout.setGravity(Gravity.END);
            holder.messageCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, messageTextView, timeTextView;
        LinearLayout infoLayout;
        CardView messageCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.sender);
            messageTextView = itemView.findViewById(R.id.message);
            timeTextView = itemView.findViewById(R.id.time);
            infoLayout = itemView.findViewById(R.id.info_layout);
            messageCard = itemView.findViewById(R.id.message_card);
        }
    }
}
