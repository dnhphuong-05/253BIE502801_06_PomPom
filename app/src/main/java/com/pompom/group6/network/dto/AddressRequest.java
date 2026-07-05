package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body để thêm địa chỉ mới (POST /api/users/:id/addresses). */
public class AddressRequest {
    @SerializedName("label") public String label;
    @SerializedName("recipient_name") public String recipientName;
    @SerializedName("phone") public String phone;
    @SerializedName("address_line") public String addressLine;
    @SerializedName("ward") public String ward;
    @SerializedName("district") public String district;
    @SerializedName("city") public String city;
    @SerializedName("is_default") public boolean isDefault;

    public AddressRequest(String label, String recipientName, String phone, String addressLine,
                          String ward, String district, String city, boolean isDefault) {
        this.label = label;
        this.recipientName = recipientName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }
}
