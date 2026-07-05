package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Body tạo bài viết cộng đồng (POST /api/community/posts). */
public class CreatePostRequest {
    @SerializedName("user_id") public String userId;
    @SerializedName("content") public String content;
    @SerializedName("images") public List<String> images;

    public CreatePostRequest(String userId, String content, List<String> images) {
        this.userId = userId;
        this.content = content;
        this.images = images;
    }
}
