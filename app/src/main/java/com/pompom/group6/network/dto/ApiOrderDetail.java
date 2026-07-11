package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Chi tiết một đơn hàng (khớp GET /api/orders/{id}) — kèm sản phẩm và lịch sử trạng thái. */
public class ApiOrderDetail {
    @SerializedName("id") public String id;
    @SerializedName("order_number") public String orderNumber;
    @SerializedName("status") public String status;
    @SerializedName("payment_method") public String paymentMethod;
    @SerializedName("payment_status") public String paymentStatus;
    @SerializedName("shipping_carrier") public String shippingCarrier;
    @SerializedName("tracking_number") public String trackingNumber;
    @SerializedName("note") public String note;
    @SerializedName("created_at") public String createdAt;

    @SerializedName("total_amount") public double totalAmount;
    @SerializedName("shipping_fee") public double shippingFee;
    @SerializedName("discount_amount") public double discountAmount;
    @SerializedName("final_amount") public double finalAmount;

    @SerializedName("items") public List<Item> items;
    @SerializedName("status_history") public List<History> statusHistory;
    @SerializedName("is_reviewed") public boolean isReviewed;

    /** Một dòng sản phẩm trong đơn. */
    public static class Item {
        @SerializedName("product_id") public String productId;
        @SerializedName("product_name") public String productName;
        @SerializedName("product_thumbnail") public String productThumbnail;
        @SerializedName("quantity") public int quantity;
        @SerializedName("price") public double price;
        @SerializedName("is_reviewed") public boolean isReviewed;
    }

    /** Một mốc trạng thái trong lịch sử đơn. */
    public static class History {
        @SerializedName("status") public String status;
        @SerializedName("note") public String note;
        @SerializedName("created_at") public String createdAt;
    }
}
