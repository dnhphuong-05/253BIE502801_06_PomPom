package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Voucher từ backend (khớp /api/vouchers & /api/users/:id/vouchers). */
public class ApiVoucher {
    @SerializedName("id") public String id;
    @SerializedName("code") public String code;
    @SerializedName("discount_type") public String discountType;
    @SerializedName("discount_value") public double discountValue;
    @SerializedName("min_order_amount") public double minOrderAmount;
    @SerializedName("end_date") public String endDate;
    @SerializedName("usage_limit") public int usageLimit;
    @SerializedName("used_count") public int usedCount;
}
