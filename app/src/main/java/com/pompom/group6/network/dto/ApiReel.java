package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/** Reel đăng lại từ Instagram/Facebook/TikTok (khớp GET /api/reels). */
public class ApiReel implements Serializable {
    @SerializedName("id") public String id;
    @SerializedName("source") public String source;
    @SerializedName("source_url") public String sourceUrl;
    @SerializedName("video_url") public String videoUrl;
    @SerializedName("thumbnail_url") public String thumbnailUrl;
    @SerializedName("caption") public String caption;
    @SerializedName("hashtags") public List<String> hashtags;
    @SerializedName("author") public Author author;
    @SerializedName("product_tags") public List<ApiProductTag> productTags;
    @SerializedName("duration") public int duration;
    @SerializedName("view_count") public long viewCount;
    @SerializedName("like_count") public long likeCount;
    @SerializedName("comment_count") public long commentCount;
    @SerializedName("share_count") public long shareCount;
    @SerializedName("created_at") public String createdAt;

    /** Trạng thái thích cục bộ trên máy — reel là nội dung sưu tầm lại, chưa có API thích/bỏ thích
     * riêng ở backend nên chỉ cập nhật lạc quan (optimistic) tại chỗ, không đồng bộ server. */
    public transient boolean liked = false;

    public static class Author implements Serializable {
        @SerializedName("name") public String name;
        @SerializedName("handle") public String handle;
        @SerializedName("avatar_url") public String avatarUrl;
        @SerializedName("verified") public boolean verified;
    }
}
