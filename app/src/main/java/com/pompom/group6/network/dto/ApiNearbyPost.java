package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Story 24h theo bán kính GPS (khớp GET/POST /api/nearby-posts). */
public class ApiNearbyPost implements Serializable {
    @SerializedName("id") public String id;
    @SerializedName("user_id") public String userId;
    @SerializedName("media_url") public String mediaUrl;
    @SerializedName("media_type") public String mediaType; // "image" | "video"
    @SerializedName("caption") public String caption;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("expires_at") public String expiresAt;
    @SerializedName("user_name") public String userName;
    @SerializedName("user_avatar") public String userAvatar;
}
