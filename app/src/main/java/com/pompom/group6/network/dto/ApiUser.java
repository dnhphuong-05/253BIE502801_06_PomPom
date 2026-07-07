package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Hồ sơ người dùng trả về từ backend (khớp toUserDto). Id là chuỗi ObjectId. */
public class ApiUser {
    @SerializedName("id") public String id;
    @SerializedName("full_name") public String fullName;
    @SerializedName("email") public String email;
    @SerializedName("phone_number") public String phoneNumber;
    @SerializedName("avatar_url") public String avatarUrl;
    @SerializedName("avatar_frame") public String avatarFrame;
    @SerializedName("bio") public String bio;
    @SerializedName("voucher_count") public int voucherCount;
    @SerializedName("membership_level") public String membershipLevel;
    @SerializedName("points") public int points;
    @SerializedName("gender") public String gender;
    @SerializedName("birth_date") public String birthDate;

    // ---- Hồ sơ mỹ phẩm (có thể null nếu user chưa nhập -> UI hiện "Thêm thông tin") ----
    @SerializedName("skin_type") public String skinType;
    @SerializedName("skin_concerns") public String skinConcerns;
    @SerializedName("skin_tone") public String skinTone;
    @SerializedName("avoid_ingredients") public String avoidIngredients;

    // ---- Số liệu community & mua sắm (đọc thật từ DB qua toUserDto) ----
    @SerializedName("followers_count") public int followersCount;
    @SerializedName("following_count") public int followingCount;
    @SerializedName("post_count") public int postCount;
    @SerializedName("story_count") public int storyCount;
    @SerializedName("review_count") public int reviewCount;
    @SerializedName("saved_count") public int savedCount;
    @SerializedName("consultation_count") public int consultationCount;
    @SerializedName("wishlist_count") public int wishlistCount;
    @SerializedName("address_count") public int addressCount;
}
