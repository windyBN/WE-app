package com.example.smartcity.fragment;

import static cn.leancloud.AVUser.getCurrentUser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartcity.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import cn.leancloud.im.v2.callback.AVIMMessagesQueryCallback;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author he sheng yuan
 * @Date 2024/10/11 11:49
 * @Version 1.0
 */
public class NewFragment extends Fragment {

    private LinearLayout messagesLayout;
    private EditText usernameEditText;
    private EditText messageEditText;
    private Button sendButton;
    private Button startChatButton;

    private AVIMClient client; // LeanCloud IM Client
    private AVIMConversation conversation; // 当前对话

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        messagesLayout = view.findViewById(R.id.messages_layout);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);
        startChatButton = view.findViewById(R.id.start_chat_button);

        // 设置发送按钮的点击事件
        sendButton.setOnClickListener(v -> sendMessage());

        // 设置发起聊天按钮的点击事件
        startChatButton.setOnClickListener(v -> startChat());

        return view;
    }

    // 发起聊天
    private void startChat() {
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "请输入对方用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前用户
        AVUser currentUser = getCurrentUser();
        if (currentUser != null) {
            // 创建IMClient并打开连接
            client = AVIMClient.getInstance(currentUser);
            client.open(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e == null) {
                        // 创建对话
                        checkOrCreateConversation(username);
                    } else {
                        Toast.makeText(getContext(), "登录失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // 检查或创建对话
    private void checkOrCreateConversation(String username) {
        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground().subscribe(new Observer<List<AVUser>>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(List<AVUser> users) {
                if (users.isEmpty()) {
                    Toast.makeText(getContext(), "用户不存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                AVUser targetUser = users.get(0);
                List<String> members = Arrays.asList(targetUser.getObjectId(), getCurrentUser().getObjectId());

                // 查询是否已有会话
                client.createConversation(members, null, null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            conversation = avimConversation; // 保存当前对话
                            loadMessages(); // 加载历史消息
                        } else {
                            Toast.makeText(getContext(), "对话创建失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("NewFragment", "对话创建失败: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "查询用户失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {}
        });
    }

    private void sendMessage() {
        if (conversation == null) {
            Toast.makeText(getContext(), "请先发起聊天", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(getContext(), "消息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        AVIMTextMessage msg = new AVIMTextMessage();
        msg.setText(messageText);

        // 发送消息
        conversation.sendMessage(msg, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    messageEditText.setText(""); // 清空输入框
                    displayMessage(msg); // 直接添加到布局
                } else {
                    Toast.makeText(getContext(), "消息发送失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayMessage(AVIMMessage message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            String messageText = ((AVIMTextMessage) message).getText(); // 获取消息内容
            String senderId = message.getFrom(); // 获取发送者 ID

            // 获取消息时间并格式化
            long timestamp = message.getTimestamp(); // 获取时间戳
            String formattedTime = formatTimestamp(timestamp); // 格式化时间

            // 创建消息视图并设置内容
            View messageView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, messagesLayout, false);
            TextView messageTextView = messageView.findViewById(R.id.message_text);
            TextView messageUserNameTextView = messageView.findViewById(R.id.message_user_name);
            TextView messageTimeTextView = messageView.findViewById(R.id.message_time); // 时间 TextView

            // 设置消息内容、发送者 ID 和时间
            messageTextView.setText(messageText);
            messageUserNameTextView.setText(senderId);
            messageTimeTextView.setText(formattedTime);

            // 将消息视图添加到消息布局中
            messagesLayout.addView(messageView);

            // 自动滚动到最新消息
            scrollToBottom();
        });
    }

    // 时间格式化工具方法
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

//    private void displayMessage(AVIMMessage message) {
//        new Handler(Looper.getMainLooper()).post(() -> {
//            String messageText = ((AVIMTextMessage) message).getText(); // 获取消息内容
//            String senderId = message.getFrom(); // 获取发送者 ID
//
//            // 创建消息视图并设置内容
//            View messageView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, messagesLayout, false);
//            TextView messageTextView = messageView.findViewById(R.id.message_text);
//            TextView messageUserNameTextView = messageView.findViewById(R.id.message_user_name);
//            messageTextView.setText(messageText);
//            messageUserNameTextView.setText(senderId);
//
//            // 将消息视图添加到消息布局中
//            messagesLayout.addView(messageView);
//
//            // 自动滚动到最新消息
//            scrollToBottom();
//        });
//    }

    // 自动滚动到最底部
    private void scrollToBottom() {
        messagesLayout.post(() -> {
            ScrollView scrollView = (ScrollView) messagesLayout.getParent(); // 获取ScrollView
            scrollView.fullScroll(View.FOCUS_DOWN); // 滚动到最底部
        });
    }

// 加载历史消息
    private void loadMessages() {
        if (conversation == null) {
            return;
        }

        // 加载对话的消息
        conversation.queryMessages(100, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> messages, AVIMException e) {
                if (e == null) {
                    messagesLayout.removeAllViews(); // 清空当前消息

                    for (AVIMMessage message : messages) {
                        displayMessage(message); // 使用新的 displayMessage 方法添加消息视图
                    }

                    // 自动滚动到最底部
                    messagesLayout.post(() -> messagesLayout.scrollTo(0, messagesLayout.getHeight()));
                } else {
                    Toast.makeText(getContext(), "加载消息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    // 添加消息到布局的方法
    private void addMessageToLayout(AVIMMessage message) {
        View messageView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, messagesLayout, false);

        TextView messageTextView = messageView.findViewById(R.id.message_text);
        TextView messageUserNameTextView = messageView.findViewById(R.id.message_user_name);

        if (message instanceof AVIMTextMessage) {
            messageTextView.setText(((AVIMTextMessage) message).getText());
        }

        // 获取消息发送者
        //AVUser sender = (AVUser) message.getFrom();
        String nickname = getCurrentUser().getString("nickname"); // 获取用户的昵称

//        if (sender != null) {
//            String nickname = sender.getString("nickname"); // 获取用户的昵称
//            messageUserNameTextView.setText(nickname != null && !nickname.isEmpty() ? nickname : sender.getUsername()); // 显示昵称或用户名
//        }

        messagesLayout.addView(messageView); // 添加消息视图
    }
}


