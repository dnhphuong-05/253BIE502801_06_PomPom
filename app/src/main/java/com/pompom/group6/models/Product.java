package com.pompom.group6.models;

public class Product {
    private int imageResId;
    private String title;
    private String price;
    private String originalPrice; // optional

    public Product(int imageResId, String title, String price, String originalPrice) {
        this.imageResId = imageResId;
        this.title = title;
        this.price = price;
        this.originalPrice = originalPrice;
    }

    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getOriginalPrice() { return originalPrice; }
}
