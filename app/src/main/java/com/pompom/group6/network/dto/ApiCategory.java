package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Danh mục sản phẩm (khớp GET /api/categories). */
public class ApiCategory {
    @SerializedName("id") public String id;
    @SerializedName("category_name") public String categoryName;
    @SerializedName("image_url") public String imageUrl;
}
