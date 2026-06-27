package com.pompom.group6.models;

public class Review {
    private int id;
    private int userId;
    private String userName;
    private String userAvatar;
    private int productId;
    private int rating;
    private String comment;
    private String imageUrl;
    private String createdAt;

    public Review(int id, int userId, String userName, String userAvatar, int productId, int rating, String comment, String imageUrl, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public int getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatedAt() { return createdAt; }
}
