package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Đánh giá do chính user viết (khớp GET /api/users/:id/reviews), kèm tên & ảnh sản phẩm. */
public class ApiMyReview {
    @SerializedName("product_id") public String productId;
    @SerializedName("product_name") public String productName;
    @SerializedName("product_thumbnail") public String productThumbnail;
    @SerializedName("rating") public int rating;
    @SerializedName("comment") public String comment;
    @SerializedName("images") public List<String> images;
    @SerializedName("created_at") public String createdAt;
}
