package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Bài viết tips từ bác sĩ tư vấn (khớp GET /api/expert-articles, GET /api/expert-articles/:id). */
public class ApiExpertArticle {
    @SerializedName("id") public String id;
    @SerializedName("expert_id") public String expertId;
    @SerializedName("title") public String title;
    @SerializedName("cover_image") public String coverImage;
    @SerializedName("excerpt") public String excerpt;
    @SerializedName("content") public String content;
    @SerializedName("category") public String category;
    @SerializedName("tags") public List<String> tags;
    @SerializedName("product_tags") public List<ApiProductTag> productTags;
    @SerializedName("read_time") public int readTime;
    @SerializedName("view_count") public long viewCount;
    @SerializedName("like_count") public long likeCount;
    @SerializedName("published_at") public String publishedAt;

    // Chỉ có ở GET /api/expert-articles (list) — tên/chức danh/avatar chuyên gia đã ghép sẵn.
    @SerializedName("expert_name") public String expertName;
    @SerializedName("expert_title") public String expertTitle;
    @SerializedName("expert_avatar") public String expertAvatar;

    // Chỉ có ở GET /api/expert-articles/:id (detail) — hồ sơ chuyên gia đầy đủ.
    @SerializedName("expert") public ApiExpert expert;
}
