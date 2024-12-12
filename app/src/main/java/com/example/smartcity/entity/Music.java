package com.example.smartcity.entity;

public class Music {
    private String title;    // 歌曲名
    private String artist;   // 歌手
    private String duration; // 时长
    private String url;      // 音乐文件地址

    public Music() {
        // 空构造函数
    }

    public Music(String title, String artist, String duration, String url) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.url = url;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}