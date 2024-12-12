package com.example.smartcity.entity;

/**
 * @Author he sheng yuan
 * @Date 2024/10/5 16:47
 * @Version 1.0
 */
public class Post {
    private String title;
    private int likes;
    private int imageResId;

    public Post(String title, int likes, int imageResId) {
        this.title = title;
        this.likes = likes;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getLikes() {
        return likes;
    }

    public int getImageResId() {

        return 0;
    }
}