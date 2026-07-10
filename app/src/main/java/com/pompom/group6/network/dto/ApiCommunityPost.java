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
    @SerializedName("share_count") public int shareCount;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("author_name") public String authorName;
    @SerializedName("author_avatar") public String authorAvatar;
    @SerializedName("is_saved") public boolean isSaved;
    @SerializedName("is_liked") public boolean isLiked;
    @SerializedName("is_following") public boolean isFollowing;
    @SerializedName("preview_comments") public List<PreviewComment> previewComments;

    /** Bình luận rút gọn hiển thị ngay trên card feed (tên tác giả + nội dung). */
    public static class PreviewComment {
        @SerializedName("author_name") public String authorName;
        @SerializedName("content") public String content;
    }
}
