package com.example.smartcity.app;

import android.app.Application;

import cn.leancloud.AVOSCloud;
import cn.leancloud.im.v2.AVIMMessageManager;

/**
 * @Author he sheng yuan
 * @Date 2024/10/4 22:48
 * @Version 1.0
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this,
                "gMKoqRbo00tqNh94vsGoep4V-gzGzoHsz",
                "Y4BPAnAmuub4JZk5dPLUyqmf",
                "https://gmkoqrbo.lc-cn-n1-shared.com"
        );
        // 设置全局的对话事件处理器
        AVIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());

        // 设置全局的消息处理器
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("words", "Hello world!");
//        testObject.saveInBackground().blockingSubscribe();
    }
}
