package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Một yêu cầu tư vấn đã gửi (khớp GET /api/consultation-requests?user_id=), kèm tên/ảnh chuyên gia. */
public class ApiConsultationRequest {
    @SerializedName("id") public String id;
    @SerializedName("expert_id") public String expertId;
    @SerializedName("expert_name") public String expertName;
    @SerializedName("expert_title") public String expertTitle;
    @SerializedName("expert_avatar") public String expertAvatar;
    @SerializedName("topic") public String topic;
    @SerializedName("message") public String message;
    @SerializedName("skin_type") public String skinType;
    @SerializedName("preferred_channel") public String preferredChannel;
    @SerializedName("status") public String status;
    @SerializedName("created_at") public String createdAt;
}
