package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Tùy chọn đơn hàng (khớp GET /api/orders/meta). */
public class ApiOrderMeta {
    @SerializedName("carriers") public List<String> carriers;
    @SerializedName("payment_methods") public List<String> paymentMethods;
}
