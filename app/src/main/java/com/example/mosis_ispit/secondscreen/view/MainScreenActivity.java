package com.example.mosis_ispit.secondscreen.view;

import android.content.Intent;
import android.os.Bundle;

//import com.example.discussgo.R;
//import com.example.discussgo.addon.SharedPreferencesWrapper;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.firstscreen.view.LogInActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigationSearch:
                    mTextMessage.setText(R.string.navSearch);
                    return true;
                case R.id.navigationCreate:
                    mTextMessage.setText(R.string.navCreate);
                    return true;
                case R.id.navigationMap:
                    mTextMessage.setText(R.string.navMap);
                    return true;
                case R.id.navigationNotifications:
                    mTextMessage.setText(R.string.navNotifications);
                    return true;
                case R.id.navigationProfile:
                    mTextMessage.setText(R.string.navProfile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.textMsg);
        mTextMessage.setText(R.string.navSearch);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        SharedPreferencesWrapper wrapper = SharedPreferencesWrapper.getInstance();

        Button btnLogOut = findViewById(R.id.dummyLogOut);
        btnLogOut.setOnClickListener(s -> {
//            wrapper.clearPreferences("token");
            Intent intent = new Intent(MainScreenActivity.this, LogInActivity.class);
            startActivity(intent);
            MainScreenActivity.this.finish();
        });
    }

}
