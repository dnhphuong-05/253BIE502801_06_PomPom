package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body đăng story 24h (POST /api/nearby-posts). */
public class NearbyPostRequest {
    @SerializedName("user_id") public String userId;
    @SerializedName("media_url") public String mediaUrl;
    @SerializedName("media_type") public String mediaType;
    @SerializedName("caption") public String caption;
    @SerializedName("lat") public double lat;
    @SerializedName("lng") public double lng;

    public NearbyPostRequest(String userId, String mediaUrl, String mediaType, String caption, double lat, double lng) {
        this.userId = userId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.caption = caption;
        this.lat = lat;
        this.lng = lng;
    }
}
