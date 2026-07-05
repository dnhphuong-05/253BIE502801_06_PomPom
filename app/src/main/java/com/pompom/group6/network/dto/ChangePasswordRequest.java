package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Body đổi mật khẩu (POST /api/users/:id/change-password). */
public class ChangePasswordRequest {
    @SerializedName("old_password") public String oldPassword;
    @SerializedName("new_password") public String newPassword;

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
