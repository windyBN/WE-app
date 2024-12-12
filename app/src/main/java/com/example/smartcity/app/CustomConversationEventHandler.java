package com.example.smartcity.app;

import java.util.List;

import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMConversationEventHandler;

/**
 * @Author he sheng yuan
 * @Date 2024/10/12 21:49
 * @Version 1.0
 */
public class CustomConversationEventHandler extends AVIMConversationEventHandler {
    @Override
    public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {

    }

    @Override
    public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {

    }

    @Override
    public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {

    }

    @Override
    public void onInvited(AVIMClient client, AVIMConversation conversation, String operator) {

    }
}
