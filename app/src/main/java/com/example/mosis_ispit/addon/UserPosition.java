package com.example.mosis_ispit.addon;

public class UserPosition {
    public String UID;
    public String username;
//    public String email;
//    public String rank;
//    public int points;
    public double longitude;
    public double latitude;
    public UserPosition(){
    }

//    public UserPosition(String username, String email, String rank, int points, double longitude, double latitude) {
//        this.username = username;
////        this.email = email;
////        this.rank = rank;
////        this.points = points;
//        this.longitude = longitude;
//        this.latitude = latitude;
//    }

    public UserPosition(String username, double longitude, double latitude) {
        this.username = username;
//        this.email = email;
//        this.rank = rank;
//        this.points = points;
        this.longitude = longitude;
        this.latitude = latitude;
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
