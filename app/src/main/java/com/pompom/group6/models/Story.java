package com.pompom.group6.models;

public class Story {
    private int id;
    private String name;
    private String subtitle;
    private int imageResId;
    private boolean isUserStory;
    private boolean isLive;
    private boolean isCreateRoom;

    public Story(int id, String name, String subtitle, int imageResId, boolean isUserStory, boolean isLive, boolean isCreateRoom) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
        this.isUserStory = isUserStory;
        this.isLive = isLive;
        this.isCreateRoom = isCreateRoom;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public int getImageResId() { return imageResId; }
    public boolean isUserStory() { return isUserStory; }
    public boolean isLive() { return isLive; }
    public boolean isCreateRoom() { return isCreateRoom; }
}
