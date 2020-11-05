package com.example.mosis_ispit.addon;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Token implements Serializable {
    public Type tokenType;
    public Intensity tokenIntensity;
    public double longitude;
    public double latitude;
    public String userId;
    public String fullName;

    // private LocalDateTime timestamp;
    @Exclude
    public String key;

    public double getDistance() {
        double ret = distance * 1000;
        ret = Math.round(ret * 100.0) / 100.0;
        return  ret;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Exclude
    public double distance;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Token(){
        tokenIntensity = Intensity.EASY;
        //tokenIntensity="EASY";
        //tokenType="GREEN";
        tokenType = Type.GREEN;
        longitude = 45;
        latitude = 23;
        distance = 0.0;
        //timestamp = LocalDateTime.now();
    }

    public Token(Type tokenType, Intensity tokenIntensity, double longitude, double latitude, String userId/*, LocalDateTime timestamp*/,String fullName) {
        this.tokenType = tokenType;
        this.tokenIntensity = tokenIntensity;
        // this.tokenIntensity=tokenIntensity;
        //this.tokenType=tokenType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
        this.fullName =fullName;
        // this.timestamp = timestamp;
    }


    public Type getTokenType() {
        return tokenType;
    }

    public void setTokenType(Type tokenType) {
        this.tokenType = tokenType;
    }

    public Intensity getTokenIntensity() {
        return tokenIntensity;
    }

    public void setTokenIntensity(Intensity tokenIntensity) {
        this.tokenIntensity = tokenIntensity;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    /*public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }*/

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", tokenIntensity=" + tokenIntensity +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", distance=" + distance +
                '}';
    }

}
