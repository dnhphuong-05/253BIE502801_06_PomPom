package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body thêm sản phẩm vào giỏ server (POST /api/carts/items). */
public class CartItemRequest {
    @SerializedName("user_id") public String userId;
    @SerializedName("product_id") public String productId;
    @SerializedName("variant_id") public String variantId;
    @SerializedName("quantity") public int quantity;

    public CartItemRequest(String userId, String productId, int quantity) {
        this(userId, productId, null, quantity);
    }

    public CartItemRequest(String userId, String productId, String variantId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }
}
