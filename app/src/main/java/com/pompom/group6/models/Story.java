package com.pompom.group6.models;

import com.pompom.group6.network.dto.ApiNearbyPost;

/** Item trong hàng Story — hoặc là ô "Đăng story", hoặc bọc 1 story 24h thật từ server. */
public class Story {
    private final ApiNearbyPost post;
    private final boolean addButton;

    /** Ô "Đăng story" (luôn là item đầu tiên). */
    public Story() {
        this.post = null;
        this.addButton = true;
    }

    public Story(ApiNearbyPost post) {
        this.post = post;
        this.addButton = false;
    }

    public boolean isAddButton() { return addButton; }
    public ApiNearbyPost getPost() { return post; }
}
