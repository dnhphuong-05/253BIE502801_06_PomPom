package com.pompom.group6.models;

public class ProductVariant {
    private int id;
    private int productId;
    private String name;
    private String sku;
    private double additionalPrice;
    private int stock;
    private String imageUrl;

    public ProductVariant(int id, int productId, String name, String sku, double additionalPrice, int stock, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.sku = sku;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public double getAdditionalPrice() { return additionalPrice; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
}
