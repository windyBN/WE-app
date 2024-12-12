package com.example.smartcity.app;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.AVIMMessageHandler;
import cn.leancloud.im.v2.messages.AVIMTextMessage;

/**
 * @Author he sheng yuan
 * @Date 2024/10/12 21:49
 * @Version 1.0
 */
public class CustomMessageHandler extends AVIMMessageHandler {
    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        if (message instanceof AVIMTextMessage) {
            // 获取并处理文本消息
            String textMessage = ((AVIMTextMessage) message).getText();
            Log.d("ChatMessage", "Received message: " + textMessage);

            // 调用 displayMessage 方法来处理 UI 更新
            displayMessage(message);
        }
    }

    // 将 displayMessage 方法移到类外部
    private void displayMessage(AVIMMessage message) {
        String messageId = message.getMessageId(); // 获取消息 ID
        String messageText = ((AVIMTextMessage) message).getText(); // 获取消息内容
        String senderId = message.getFrom(); // 获取发送者 ID
        long timestamp = message.getTimestamp(); // 获取时间戳

        // 转换时间戳为可读格式
        String formattedTime = new SimpleDateFormat("MM月dd日 HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));

        // 在 UI 中显示信息
        String displayMessage = String.format("消息 ID: %s\n内容: %s\n发送者: %s\n发送时间: %s",
                messageId,
                messageText,
                senderId,
                formattedTime);

        Log.d("ChatMessage", displayMessage); // 输出到日志，或用 TextView 显示在界面
        // TODO: 在这里更新你的 UI，例如更新 RecyclerView 或 TextView
    }
}
