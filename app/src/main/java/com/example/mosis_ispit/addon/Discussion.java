package com.example.mosis_ispit.addon;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Discussion implements Serializable {
    private String topic;
    private String description;
    private double longitude;
    private double latitude;
    private int maxUsers;
    private String start;
    private boolean open;
    private User owner;
    public ArrayList<User> users;
    public boolean active;
    @Exclude
    public String key;

    public Discussion() {
        users = new ArrayList<User>();
    }

    public Discussion(String t, String d, double lon, double lat, boolean o) {
        this.topic = t;
        this.description = d;
        this.longitude = lon;
        this.latitude = lat;
        this.open = o;
    }

    public Discussion(String t, String d, double lon, double lat, String s, boolean o, int maxUsers, User owner) {
        this.topic = t;
        this.description = d;
        this.longitude = lon;
        this.latitude = lat;
        this.start = s;
        this.open = o;
        this.owner = owner;
        this.maxUsers = Math.min(maxUsers, 20);
        this.maxUsers = Math.max(this.maxUsers, 3);
        users = new ArrayList<User>();
//        users.add(owner);
        this.active = true;
    }

    public Discussion(boolean active, String d, double lat, double lon, boolean o, User owner, String s, String t, int maxUsers, ArrayList<User> users) {
        this.topic = t;
        this.description = d;
        this.longitude = lon;
        this.latitude = lat;
        this.start = s;
        this.open = o;
        this.owner = owner;
        this.maxUsers = maxUsers;
        this.users = users;
        this.active = active;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
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
