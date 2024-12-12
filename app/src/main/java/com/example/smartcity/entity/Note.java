package com.example.smartcity.entity;

/**
 * @Author he sheng yuan
 * @Date 2024/10/6 11:49
 * @Version 1.0
 */
public class Note {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String publishTime; // 新增时间字段

    public Note(String id, String title, String content, String imageUrl, String publishTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.publishTime = publishTime;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getPublishTime() { return publishTime; } // 时间的 Getter
}
