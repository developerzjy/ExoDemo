package com.example.exodemo.bean;

public class VideoBean {

    private String name;
    private String coverUrl;
    private String videoUrl;

    public VideoBean(String name, String coverUrl, String videoUrl) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.videoUrl = videoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
