package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body cập nhật hồ sơ (PUT /api/users/:id). */
public class UserUpdateRequest {
    @SerializedName("full_name") public String fullName;
    @SerializedName("phone_number") public String phoneNumber;
    @SerializedName("bio") public String bio;
    @SerializedName("gender") public String gender;
    @SerializedName("birth_date") public String birthDate;
    @SerializedName("skin_type") public String skinType;

    public UserUpdateRequest(String fullName, String phoneNumber, String bio,
                             String gender, String birthDate, String skinType) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.gender = gender;
        this.birthDate = birthDate;
        this.skinType = skinType;
    }
}
