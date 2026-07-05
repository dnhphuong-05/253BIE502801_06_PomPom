package com.pompom.group6.models;

public class PromotionProduct {
    private String productId;
    private String name;
    private String imageUrl;
    private double originalPrice;
    private double salePrice;
    private int discountPercent;
    private int totalStock;
    private int soldCount;

    public PromotionProduct(String productId, String name, String imageUrl, double originalPrice, double salePrice, int discountPercent, int totalStock, int soldCount) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.discountPercent = discountPercent;
        this.totalStock = totalStock;
        this.soldCount = soldCount;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public double getOriginalPrice() { return originalPrice; }
    public double getSalePrice() { return salePrice; }
    public int getDiscountPercent() { return discountPercent; }
    public int getTotalStock() { return totalStock; }
    public int getSoldCount() { return soldCount; }
}