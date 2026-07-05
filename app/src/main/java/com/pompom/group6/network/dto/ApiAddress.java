package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Địa chỉ giao hàng (khớp GET /api/users/:id/addresses). Id là chuỗi ObjectId. */
public class ApiAddress {
    @SerializedName("id") public String id;
    @SerializedName("label") public String label;
    @SerializedName("recipient_name") public String recipientName;
    @SerializedName("phone") public String phone;
    @SerializedName("address_line") public String addressLine;
    @SerializedName("ward") public String ward;
    @SerializedName("district") public String district;
    @SerializedName("city") public String city;
    @SerializedName("is_default") public boolean isDefault;
}
