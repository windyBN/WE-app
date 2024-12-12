package com.example.smartcity.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author he sheng yuan
 * @Date 2024/10/15 16:58
 * @Version 1.0
 */

public class Chatlist {
    private String sender;
    private String message;
    private String time;

    public Chatlist(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.time = getCurrentTime();
    }

    public Chatlist(String sender, String message, String time) {
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
