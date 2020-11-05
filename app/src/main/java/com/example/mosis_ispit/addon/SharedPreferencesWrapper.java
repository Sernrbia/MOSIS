package com.example.mosis_ispit.addon;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesWrapper {
    private static SharedPreferencesWrapper ourInstance;
    private SharedPreferences sharedPreferences;

    public static final String ACCESS_TOKEN_KEY = "token";

    public static SharedPreferencesWrapper getInstance() {
        return ourInstance;
    }

    public static void createInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SharedPreferencesWrapper(context);
        }
    }

    private SharedPreferencesWrapper(Context context) {
        sharedPreferences = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE);
    }

    public void putAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ACCESS_TOKEN_KEY, token);
        editor.apply();

    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, "");


    }

    public void clearPreferences(String file) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(file);
        editor.apply();
    }
}
