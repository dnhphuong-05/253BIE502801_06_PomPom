package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Bài viết cộng đồng (khớp /api/community/posts). Id là chuỗi ObjectId. */
public class ApiCommunityPost {
    @SerializedName("id") public String id;
    @SerializedName("user_id") public String userId;
    @SerializedName("content") public String content;
    @SerializedName("images") public List<String> images;
    @SerializedName("product_tag") public String productTag;
    @SerializedName("like_count") public int likeCount;
    @SerializedName("comment_count") public int commentCount;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("author_name") public String authorName;
    @SerializedName("author_avatar") public String authorAvatar;
    @SerializedName("is_saved") public boolean isSaved;
    @SerializedName("is_liked") public boolean isLiked;
}
