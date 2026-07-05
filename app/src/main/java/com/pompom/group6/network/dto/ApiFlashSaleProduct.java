package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Sản phẩm Flash Sale (khớp GET /api/flash-sale). */
public class ApiFlashSaleProduct {
    @SerializedName("product_id") public String productId;
    @SerializedName("name") public String name;
    @SerializedName("image_url") public String imageUrl;
    @SerializedName("original_price") public double originalPrice;
    @SerializedName("sale_price") public double salePrice;
    @SerializedName("discount_percent") public int discountPercent;
    @SerializedName("total_stock") public int totalStock;
    @SerializedName("sold_count") public int soldCount;
}
