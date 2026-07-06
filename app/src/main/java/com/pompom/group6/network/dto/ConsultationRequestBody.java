package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body gửi yêu cầu tư vấn (POST /api/consultation-requests). */
public class ConsultationRequestBody {
    @SerializedName("user_id") public String userId;
    @SerializedName("expert_id") public String expertId;
    @SerializedName("source_article_id") public String sourceArticleId;
    @SerializedName("name") public String name;
    @SerializedName("phone") public String phone;
    @SerializedName("email") public String email;
    @SerializedName("skin_type") public String skinType;
    @SerializedName("topic") public String topic;
    @SerializedName("message") public String message;
    @SerializedName("preferred_channel") public String preferredChannel;

    public ConsultationRequestBody(String userId, String expertId, String sourceArticleId,
                                   String name, String phone, String email, String skinType,
                                   String topic, String message, String preferredChannel) {
        this.userId = userId;
        this.expertId = expertId;
        this.sourceArticleId = sourceArticleId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.skinType = skinType;
        this.topic = topic;
        this.message = message;
        this.preferredChannel = preferredChannel;
    }
}
