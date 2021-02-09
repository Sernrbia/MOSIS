package com.example.mosis_ispit.addon;

public class DiscussionPosition {
    public String key;
    public String topic;
    public String description;
    public String owner;
    public double longitude;
    public double latitude;
    public int maxUsers;
    public String type;

    public DiscussionPosition(){}

    public DiscussionPosition(String topic, String description, String owner, double longitude, double latitude, int maxUsers, String type) {
        this.topic = topic;
        this.description = description;
        this.owner = owner;
        this.longitude = longitude;
        this.latitude = latitude;
        this.maxUsers = maxUsers;
        this.type = type;
    }
}
