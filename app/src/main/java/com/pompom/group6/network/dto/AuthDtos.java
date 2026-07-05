package com.pompom.group6.network.dto;

import com.google.gson.annotations.SerializedName;

/** Các body request/response cho đăng nhập & đăng ký. */
public final class AuthDtos {
    private AuthDtos() {}

    public static class LoginRequest {
        @SerializedName("email") public String email;
        @SerializedName("password") public String password;
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterRequest {
        @SerializedName("full_name") public String fullName;
        @SerializedName("email") public String email;
        @SerializedName("password") public String password;
        @SerializedName("phone_number") public String phoneNumber;
        public RegisterRequest(String fullName, String email, String password, String phoneNumber) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
        }
    }
}
