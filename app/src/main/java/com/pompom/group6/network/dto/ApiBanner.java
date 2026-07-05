package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Banner trang chủ (khớp GET /api/banners). */
public class ApiBanner {
    @SerializedName("id") public String id;
    @SerializedName("title") public String title;
    @SerializedName("image_url") public String imageUrl;
}
