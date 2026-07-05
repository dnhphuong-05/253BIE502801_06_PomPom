package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Sản phẩm từ backend (khớp GET /api/products & /api/products/:id). Id là chuỗi ObjectId. */
public class ApiProduct {
    @SerializedName("id") public String id;
    @SerializedName("name") public String name;
    @SerializedName("description") public String description;
    @SerializedName("price") public double price;
    @SerializedName("sale_price") public double salePrice;
    @SerializedName("stock") public int stock;
    @SerializedName("sku") public String sku;
    @SerializedName("brand") public String brand;
    @SerializedName("thumbnail_url") public String thumbnailUrl;
    @SerializedName("category_name") public String categoryName; // chỉ có ở endpoint chi tiết

    // Chỉ có ở endpoint chi tiết (/api/products/:id)
    @SerializedName("images") public List<String> images;
    @SerializedName("variants") public List<ApiProductVariant> variants;
    @SerializedName("review_count") public int reviewCount;
    @SerializedName("rating_avg") public double ratingAvg;
}
