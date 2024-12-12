package com.example.smartcity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartcity.R;
import com.example.smartcity.adapter.ChatlistAdapter;
import com.example.smartcity.entity.Chatlist;
import com.example.smartcity.utils.WenXinUtil;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceFragment extends Fragment {

    private EditText etChat;
    private Button btnSend;
    private RecyclerView rcChatList;
    private ChatlistAdapter chatAdapter;
    private List<Chatlist> mDatas;

    private final int MESSAGE_UPDATE_VIEW = 1;

    // Handler 用于在主线程更新 UI
    private final Handler gHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MESSAGE_UPDATE_VIEW) {
                chatAdapter.notifyDataSetChanged();  // 更新数据
                // 滚动到最新消息
                rcChatList.scrollToPosition(mDatas.size() - 1);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        init(view);  // 初始化组件

        // 确保在这里初始化mDatas
        mDatas = new ArrayList<>();
        
        // 添加一条欢迎消息
        mDatas.add(new Chatlist("AI助手", "您好，我是AI助手，请问有什么可以帮您？"));
        
        chatAdapter = new ChatlistAdapter(requireContext(), mDatas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rcChatList.setLayoutManager(layoutManager);
        rcChatList.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());
        return view;
    }

    private void init(View view) {
        etChat = view.findViewById(R.id.et_chat);
        btnSend = view.findViewById(R.id.btn_send);
        rcChatList = view.findViewById(R.id.rc_chatlist);
    }

    private void sendMessage() {
        String userAsk = etChat.getText().toString().trim();
        if (userAsk.isEmpty()) return;

        // 添加用户消息并立即清空输入框
        mDatas.add(new Chatlist("我", userAsk));
        chatAdapter.notifyItemInserted(mDatas.size() - 1);
        rcChatList.scrollToPosition(mDatas.size() - 1);
        etChat.setText("");

        // 在新线程中获取AI回复
        new Thread(() -> {
            try {
                WenXinUtil wx = new WenXinUtil();
                String reply = wx.getAnswer(userAsk);
                
                // 在主线程中添加AI回复
                requireActivity().runOnUiThread(() -> {
                    mDatas.add(new Chatlist("AI助手", reply));
                    chatAdapter.notifyItemInserted(mDatas.size() - 1);
                    rcChatList.scrollToPosition(mDatas.size() - 1);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    mDatas.add(new Chatlist("AI助手", "抱歉，获取回复失败，请重试"));
                    chatAdapter.notifyItemInserted(mDatas.size() - 1);
                    rcChatList.scrollToPosition(mDatas.size() - 1);
                });
            }
        }).start();
    }
}
