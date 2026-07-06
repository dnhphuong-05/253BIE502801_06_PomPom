package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Đánh giá sản phẩm (khớp GET /api/products/:id/reviews). */
public class ApiReview {
    @SerializedName("rating") public int rating;
    @SerializedName("comment") public String comment;
    @SerializedName("images") public List<String> images;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("user_name") public String userName;
    @SerializedName("user_avatar") public String userAvatar;
}
