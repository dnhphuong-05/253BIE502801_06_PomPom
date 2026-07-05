package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Hồ sơ người dùng trả về từ backend (khớp toUserDto). Id là chuỗi ObjectId. */
public class ApiUser {
    @SerializedName("id") public String id;
    @SerializedName("full_name") public String fullName;
    @SerializedName("email") public String email;
    @SerializedName("phone_number") public String phoneNumber;
    @SerializedName("avatar_url") public String avatarUrl;
    @SerializedName("bio") public String bio;
    @SerializedName("voucher_count") public int voucherCount;
    @SerializedName("membership_level") public String membershipLevel;
    @SerializedName("points") public int points;
    @SerializedName("gender") public String gender;
    @SerializedName("birth_date") public String birthDate;
    @SerializedName("skin_type") public String skinType;
}
