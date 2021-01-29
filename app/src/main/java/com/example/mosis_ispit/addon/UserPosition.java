package com.example.mosis_ispit.addon;

public class UserPosition {
    public String UID;
    public String username;
    public double longitude;
    public double latitude;
    public boolean inDiscussion;

    public UserPosition(){
    }

    public UserPosition(String username, double longitude, double latitude) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public UserPosition(String username, double longitude, double latitude, boolean inDiscussion) {
        this.username = username;
        this.longitude = longitude;
        this.latitude = latitude;
        this.inDiscussion = inDiscussion;
    }

    public UserPosition(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double lon) {
        this.longitude = lon;
    }

    @Override
    public String toString() {
        return "Latitude:" + latitude + ", Longitude:" + longitude;
    }
}
