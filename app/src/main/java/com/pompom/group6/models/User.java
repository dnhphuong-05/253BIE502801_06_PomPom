package com.pompom.group6.models;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String bio;
    private String membershipLevel;
    private int points;
    private int voucherCount;
    private String gender;
    private String birthDate;
    private String skinType;

    public User() {}

    public User(int userId, String fullName, String email, String phoneNumber, String avatarUrl, String bio) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getVoucherCount() { return voucherCount; }
    public void setVoucherCount(int voucherCount) { this.voucherCount = voucherCount; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }
}
