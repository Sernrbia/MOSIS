package com.example.mosis_ispit.addon;

public class UserOwner {
    public String UID;
    public String username;
    public int points;
    public String rank;
    public boolean active;
    public String topic;

    public UserOwner(){}

    public UserOwner(String username, int points, String rank, boolean active, String topic) {
        this.username = username;
        this.points = points;
        this.rank = rank;
        this.active = active;
        this.topic = topic;
    }

    public void setPoints(int points) {
        this.points += points;
        if (this.points < 100) {
            this.rank = Rank.Quiet.toString();
        } else if (this.points < 200) {
            this.rank = Rank.SmallTalker.toString();
        } else if (this.points < 400) {
            this.rank = Rank.SayingSomething.toString();
        } else if (this.points < 800) {
            this.rank = Rank.Talker.toString();
        } else if (this.points < 1400) {
            this.rank = Rank.CantShutUp.toString();
        } else {
            this.rank = Rank.BenShapiro.toString();
        }
    }
}
