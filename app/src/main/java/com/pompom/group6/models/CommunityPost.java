package com.pompom.group6.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunityPost {
    private int postId;
    private int userId;
    private String content;
    private String imageUrl; 
    private List<String> images;
    private int likeCount;
    private int commentCount;
    private String postType;
    private String userName;
    private String userAvatar;

    public CommunityPost(int postId, int userId, String content, String imageUrl, int likeCount, int commentCount, String postType) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.postType = postType;
    }
    
    // Additional constructor for DAO convenience
    public CommunityPost(int postId, int userId, String content, String imageUrl, int likeCount, int commentCount, String postType, String userName, String userAvatar) {
        this(postId, userId, content, imageUrl, likeCount, commentCount, postType);
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public int getPostId() { return postId; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    
    public List<String> getImages() { 
        if (images == null || images.isEmpty()) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                return Arrays.asList(imageUrl.split(","));
            }
            return new ArrayList<>();
        }
        return images; 
    }
    
    public void setImages(List<String> images) { this.images = images; }

    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public String getPostType() { return postType; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}
