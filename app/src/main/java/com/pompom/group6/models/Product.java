package com.pompom.group6.models;

public class Product {
    private int id;
    private String title;
    private String price;
    private String originalPrice;
    private String imageUrl;
    private int imageResId;
    private String description;
    private float rating;
    private int reviewCount;
    private String sku;
    private int stock;
    private String categoryName;
    private String brandName;
    private int discountPercent;

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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
}
