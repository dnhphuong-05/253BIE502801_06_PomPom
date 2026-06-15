package com.pompom.group6.models;

public class Product {
    private int id;
    private String title;
    private String price;
    private String originalPrice;
    private String imageUrl;
    private int imageResId;

    public Product(int id, String title, String price, String originalPrice) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.originalPrice = originalPrice;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getOriginalPrice() { return originalPrice; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
