package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Giỏ hàng server (khớp GET /api/carts?user_id=). */
public class ApiCart {
    @SerializedName("items") public List<ApiCartItem> items;
    @SerializedName("total") public double total;

    public static class ApiCartItem {
        @SerializedName("product_id") public String productId;
        @SerializedName("product_name") public String productName;
        @SerializedName("price") public double price;
        @SerializedName("quantity") public int quantity;
        @SerializedName("thumbnail_url") public String thumbnailUrl;
    }
}
