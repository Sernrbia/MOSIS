package com.example.mosis_ispit.addon;

import android.widget.ImageView;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private ImageView image;
    private String rank;
    private int points;
    public String UID;
    public HashMap<String, User> friends;
    public HashMap<String, Discussion> discussionsHistory;
    public HashMap<String, Discussion> discussions;
    public ArrayList<Notification> notifications;

    public User() {
        this.friends = new HashMap<String, User>();
        this.discussions = new HashMap<String, Discussion>();
        this.discussionsHistory = new HashMap<String, Discussion>();
        this.notifications = new ArrayList<Notification>();
    }

    public User(String user, String em, int points, String rank) {
        this.username = user;
        this.email = em;
        this.rank = rank;
        this.points = points;
    }

    public User(String user, String fn, String ln, String em, String pass, int points, String rank) {
        this.username = user;
        this.firstName = fn;
        this.lastName = ln;
        this.email = em;
        this.password = pass;
        this.rank = rank;
        this.points = points;
        this.friends = new HashMap<String, User>();
        this.discussions = new HashMap<String, Discussion>();
        this.discussionsHistory = new HashMap<String, Discussion>();
        this.notifications = new ArrayList<Notification>();
    }

    public User(String user, String fn, String ln, String em, String pass, ImageView img) {
        this.username = user;
        this.firstName = fn;
        this.lastName = ln;
        this.email = em;
        this.password = pass;
        this.image = img;
        this.rank = Rank.Quiet.toString();
        this.points = 0;
        this.friends = new HashMap<String, User>();
        this.discussions = new HashMap<String, Discussion>();
        this.discussionsHistory = new HashMap<String, Discussion>();
        this.notifications = new ArrayList<Notification>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Exclude
    public ImageView getImage() {
        return image;
    }

    @Exclude
    public void setImage(ImageView image) {
        this.image = image;
    }

    public String FullName(){
        return firstName+" "+lastName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        if (this.points < 100) {
            this.setRank(Rank.Quiet.toString());
        } else if (this.points < 200) {
            this.setRank(Rank.SmallTalker.toString());
        } else if (this.points < 400) {
            this.setRank(Rank.SayingSomething.toString());
        } else if (this.points < 800) {
            this.setRank(Rank.Talker.toString());
        } else if (this.points < 1400) {
            this.setRank(Rank.CantShutUp.toString());
        } else {
            this.setRank(Rank.BenShapiro.toString());
        }
    }
}
