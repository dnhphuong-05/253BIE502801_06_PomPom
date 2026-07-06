package com.pompom.group6.models;

public class CartItem {
    private int productId;
    private String productOid; // id ObjectId của sản phẩm cloud (null nếu là sản phẩm SQLite/local)
    private String title;
    private String price;
    private String originalPrice; // Added for UI
    private String imageUrl;
    private int quantity;
    private String variantId;   // ObjectId biến thể (cloud) hoặc null nếu không chọn màu
    private String variantName; // Tên hiển thị của biến thể đã chọn, vd "Tone 01 – Sáng"

    public CartItem(int productId, String title, String price, String imageUrl, int quantity) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }

    public String getProductOid() { return productOid; }
    public void setProductOid(String productOid) { this.productOid = productOid; }

    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    
    public String getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }

    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceValue() {
        if (price == null) return 0;
        try {
            String cleanPrice = price.replaceAll("[^0-9.]", "");
            if (cleanPrice.isEmpty()) return 0;
            return Double.parseDouble(cleanPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getSubtotal() {
        return getPriceValue() * quantity;
    }
}
