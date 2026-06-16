package com.pompom.group6.models;

public class Comment {
    private int commentId;
    private int userId;
    private String userName;
    private String userAvatar;
    private String userRank;
    private String content;
    private String createdAt;
    private int likes;

    public Comment(int commentId, int userId, String userName, String userAvatar, String content, String createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getCommentId() { return commentId; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getUserRank() { return userRank; }
    public void setUserRank(String userRank) { this.userRank = userRank; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
}
