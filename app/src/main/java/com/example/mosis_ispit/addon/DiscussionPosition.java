package com.example.mosis_ispit.addon;

public class DiscussionPosition {
    public String key;
    public String topic;
    private double longitude;
    private double latitude;

    public DiscussionPosition() {

    }

    public DiscussionPosition(String topic, double lo, double la) {
        this.topic = topic;
        this.longitude = lo;
        this.latitude = la;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
