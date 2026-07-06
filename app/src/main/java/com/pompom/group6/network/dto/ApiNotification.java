package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Thông báo (khớp GET /api/notifications?user_id=). Có 2 dạng dữ liệu thật trong cùng
 * collection: tương tác xã hội (like/comment/follow — actor_*, post_id) và khuyến mãi hệ
 * thống (promotion — title, image_url, action_url). Field nào không thuộc dạng đang có sẽ null.
 */
public class ApiNotification {
    @SerializedName("id") public String id;
    @SerializedName("user_id") public String userId;
    @SerializedName("type") public String type; // "like" | "comment" | "follow" | "promotion" | ...
    @SerializedName("message") public String message;
    @SerializedName("is_read") public boolean isRead;
    @SerializedName("created_at") public String createdAt;

    // Thông báo tương tác xã hội (like/comment/follow)
    @SerializedName("actor_id") public String actorId;
    @SerializedName("actor_name") public String actorName;
    @SerializedName("actor_avatar") public String actorAvatar;
    @SerializedName("post_id") public String postId;

    // Thông báo hệ thống (promotion...)
    @SerializedName("title") public String title;
    @SerializedName("image_url") public String imageUrl;
    @SerializedName("action_url") public String actionUrl;
}
