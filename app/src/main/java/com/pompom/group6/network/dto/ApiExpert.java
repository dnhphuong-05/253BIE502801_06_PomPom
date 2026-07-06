package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Chuyên gia/bác sĩ tư vấn (khớp GET /api/experts). */
public class ApiExpert {
    @SerializedName("id") public String id;
    @SerializedName("name") public String name;
    @SerializedName("title") public String title;
    @SerializedName("specialty") public String specialty;
    @SerializedName("avatar_url") public String avatarUrl;
    @SerializedName("credentials") public String credentials;
    @SerializedName("bio") public String bio;
    @SerializedName("years_experience") public int yearsExperience;
    @SerializedName("rating") public double rating;
    @SerializedName("consultation_count") public int consultationCount;
    @SerializedName("contact") public Contact contact;
    @SerializedName("is_available") public boolean isAvailable;

    public static class Contact {
        @SerializedName("phone") public String phone;
        @SerializedName("zalo") public String zalo;
        @SerializedName("messenger") public String messenger;
        @SerializedName("email") public String email;
    }
}
