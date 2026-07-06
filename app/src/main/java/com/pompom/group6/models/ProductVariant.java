package com.pompom.group6.models;

public class ProductVariant {
    private int id;
    private int productId;
    private String oid; // id chuỗi (ObjectId) khi lấy từ MongoDB; null nếu từ SQLite
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
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public double getAdditionalPrice() { return additionalPrice; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
}
