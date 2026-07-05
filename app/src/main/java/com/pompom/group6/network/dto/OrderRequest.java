package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Body tạo đơn hàng (POST /api/orders). */
public class OrderRequest {
    @SerializedName("user_id") public String userId;
    @SerializedName("payment_method") public String paymentMethod;
    @SerializedName("shipping_fee") public double shippingFee;
    @SerializedName("note") public String note;
    @SerializedName("items") public List<Item> items;

    public OrderRequest(String userId, String paymentMethod, double shippingFee, String note, List<Item> items) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.shippingFee = shippingFee;
        this.note = note;
        this.items = items;
    }

    public static class Item {
        @SerializedName("product_id") public String productId;
        @SerializedName("quantity") public int quantity;

        public Item(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
