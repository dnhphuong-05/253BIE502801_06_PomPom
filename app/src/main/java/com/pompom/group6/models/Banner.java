package com.pompom.group6.models;

public class Banner {
    private int id;
    private String imageUrl;
    private String title;
    private int imageRes; // Local fallback

    public Banner(int id, String imageUrl, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public Banner(int id, int imageRes, String title) {
        this.id = id;
        this.imageRes = imageRes;
        this.title = title;
    }

    public int getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public int getImageRes() { return imageRes; }
}
