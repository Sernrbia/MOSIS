package com.example.mosis_ispit.addon;

public class DiscussionPosition {
    public String key;
    public String topic;
    public double longitude;
    public double latitude;

    public DiscussionPosition(){}

    public DiscussionPosition(String topic, double longitude, double latitude) {
        this.topic = topic;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
