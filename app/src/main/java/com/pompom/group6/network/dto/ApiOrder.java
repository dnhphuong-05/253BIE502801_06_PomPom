package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Đơn hàng cho danh sách (khớp GET /api/orders). */
public class ApiOrder {
    @SerializedName("id") public String id;
    @SerializedName("order_number") public String orderNumber;
    @SerializedName("final_amount") public double finalAmount;
    @SerializedName("status") public String status;
    @SerializedName("payment_method") public String paymentMethod;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("item_count") public int itemCount;
    @SerializedName("first_item_name") public String firstItemName;
    @SerializedName("first_item_image") public String firstItemImage;
}
