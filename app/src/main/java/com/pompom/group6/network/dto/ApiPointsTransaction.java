package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Giao dịch điểm (khớp GET /api/users/:id/points). */
public class ApiPointsTransaction {
    @SerializedName("points_change") public int pointsChange;
    @SerializedName("reason") public String reason;
    @SerializedName("created_at") public String createdAt;
}
