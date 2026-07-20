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
    // Chỉ set khi đặt hàng không đăng nhập (userId null) — dùng để tra cứu lại đơn theo SĐT.
    @SerializedName("guest_name") public String guestName;
    @SerializedName("guest_phone") public String guestPhone;
    @SerializedName("guest_email") public String guestEmail;

    public OrderRequest(String userId, String paymentMethod, double shippingFee, String note, List<Item> items) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.shippingFee = shippingFee;
        this.note = note;
        this.items = items;
    }

    /** Đơn hàng của khách vãng lai (chưa đăng nhập): không có user_id, thay bằng thông tin liên hệ. */
    public static OrderRequest forGuest(String guestName, String guestPhone, String guestEmail,
                                         String paymentMethod, double shippingFee, String note, List<Item> items) {
        OrderRequest req = new OrderRequest(null, paymentMethod, shippingFee, note, items);
        req.guestName = guestName;
        req.guestPhone = guestPhone;
        req.guestEmail = guestEmail;
        return req;
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
