package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    @SerializedName("is_reviewed") public boolean isReviewed;
    @SerializedName("item_product_ids") public List<ReorderItem> reorderItems;

    /** Một dòng sản phẩm gọn để "Mua lại" thêm đúng số lượng vào giỏ. */
    public static class ReorderItem {
        @SerializedName("product_id") public String productId;
        @SerializedName("quantity") public int quantity;
    }
}
