package com.example.mosis_ispit.addon;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Discussion implements Serializable {
    private String topic;
    private String description;
    private double longitude;
    private double latitude;
    public int maxUsers;
    private String start;
    private String owner;
    public String ownerUsername;
    public ArrayList<User> users;
    public String type;
    public boolean active;
    @Exclude
    public String key;

    public Discussion() {
        users = new ArrayList<User>();
    }

    public Discussion(String t, String d, double lon, double lat) {
        this.topic = t;
        this.description = d;
        this.longitude = lon;
        this.latitude = lat;
    }

    public Discussion(String t, String d, double lon, double lat, String s, int maxUsers, String owner, String ownerUsername, String type) {
        this.topic = t;
        this.description = d;
        this.longitude = lon;
        this.latitude = lat;
        this.start = s;
        this.maxUsers = Math.min(maxUsers, 20);
        this.maxUsers = Math.max(this.maxUsers, 3);
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        users = new ArrayList<>();
        this.active = true;
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void addUser(User u) {
        users.add(u);
        if (users.size() < this.maxUsers) {
            active = true;
        }
    }

    public void removeUser(User u) {
        users.remove(u);
    }
}
