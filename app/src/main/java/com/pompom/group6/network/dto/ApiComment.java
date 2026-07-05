package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Bình luận (khớp /api/community/posts/:id/comments). */
public class ApiComment {
    @SerializedName("id") public String id;
    @SerializedName("content") public String content;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("author_name") public String authorName;
    @SerializedName("author_avatar") public String authorAvatar;
}
