package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Blog thương hiệu (khớp GET /api/blogs, GET /api/blogs/:id). */
public class ApiBlog {
    @SerializedName("id") public String id;
    @SerializedName("title") public String title;
    @SerializedName("slug") public String slug;
    @SerializedName("cover_image") public String coverImage;
    @SerializedName("excerpt") public String excerpt;
    @SerializedName("content") public String content;
    @SerializedName("author") public Author author;
    @SerializedName("category") public String category;
    @SerializedName("tags") public List<String> tags;
    @SerializedName("read_time") public int readTime;
    @SerializedName("view_count") public long viewCount;
    @SerializedName("like_count") public long likeCount;
    @SerializedName("published_at") public String publishedAt;

    public static class Author {
        @SerializedName("name") public String name;
        @SerializedName("avatar_url") public String avatarUrl;
        @SerializedName("role") public String role;
    }
}
