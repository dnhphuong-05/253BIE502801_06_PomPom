package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Kết quả phân tích "vấn đề da quan tâm" từ tương tác Community. */
public class ApiSkinAnalysis {
    @SerializedName("interacted_posts") public int interactedPosts;
    @SerializedName("concerns") public List<Concern> concerns;

    public static class Concern {
        @SerializedName("key") public String key;
        @SerializedName("label") public String label;
        @SerializedName("count") public int count;
    }
}
