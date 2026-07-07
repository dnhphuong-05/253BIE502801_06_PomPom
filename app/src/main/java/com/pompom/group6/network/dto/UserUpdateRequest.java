package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body cập nhật hồ sơ (PUT /api/users/:id). Field null bị Gson bỏ qua -> backend chỉ cập nhật field gửi lên. */
public class UserUpdateRequest {
    @SerializedName("full_name") public String fullName;
    @SerializedName("phone_number") public String phoneNumber;
    @SerializedName("bio") public String bio;
    @SerializedName("gender") public String gender;
    @SerializedName("birth_date") public String birthDate;
    @SerializedName("skin_type") public String skinType;
    @SerializedName("skin_tone") public String skinTone;
    @SerializedName("avatar_url") public String avatarUrl;
    @SerializedName("avatar_frame") public String avatarFrame;

    public UserUpdateRequest() {}

    /** Body chỉ cập nhật loại da. */
    public static UserUpdateRequest ofSkinType(String code) {
        UserUpdateRequest r = new UserUpdateRequest();
        r.skinType = code;
        return r;
    }

    /** Body chỉ cập nhật tông da. */
    public static UserUpdateRequest ofSkinTone(String code) {
        UserUpdateRequest r = new UserUpdateRequest();
        r.skinTone = code;
        return r;
    }

    public UserUpdateRequest(String fullName, String phoneNumber, String bio,
                             String gender, String birthDate, String skinType) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.gender = gender;
        this.birthDate = birthDate;
        this.skinType = skinType;
    }

    /** Body chỉ cập nhật avatar + khung. */
    public static UserUpdateRequest avatar(String avatarUrl, String avatarFrame) {
        UserUpdateRequest r = new UserUpdateRequest();
        r.avatarUrl = avatarUrl;
        r.avatarFrame = avatarFrame;
        return r;
    }
}
