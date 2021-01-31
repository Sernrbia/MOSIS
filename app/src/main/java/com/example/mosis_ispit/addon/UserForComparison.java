package com.example.mosis_ispit.addon;

import org.jetbrains.annotations.NotNull;

public class UserForComparison implements Comparable {
    public String username;
    public int points;


    public UserForComparison() {
    }

    public UserForComparison(String username, int points) {
        this.username = username;
        this.points = points;
    }

    @Override
    public int compareTo(Object o) {
        int comparePoints = ((UserForComparison) o).points;
        /* For Ascending order*/
        return comparePoints - this.points;
    }

    @NotNull
    @Override
    public String toString() {
        String retValue = "";
        retValue = username + points;
        if (retValue.length() < 40) {
            int dots = 40 - retValue.length();
            retValue = username;
            for (int i = 0; i < dots; i++) {
                retValue += ".";
            }
            retValue += points;
        }
        return retValue;
    }
}
