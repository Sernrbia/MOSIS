package com.example.mosis_ispit.secondscreen.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

//import com.example.discussgo.R;
//import com.example.discussgo.addon.SharedPreferencesWrapper;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.Discussion;
import com.example.mosis_ispit.addon.OnGetDataListener;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.firstscreen.view.LogInActivity;
import com.example.mosis_ispit.firstscreen.view.fragments.LoginFragment;
import com.example.mosis_ispit.firstscreen.view.fragments.RegisterFragment;
import com.example.mosis_ispit.secondscreen.view.fragments.CreateFragment;
import com.example.mosis_ispit.secondscreen.view.fragments.MapFragment;
import com.example.mosis_ispit.secondscreen.view.fragments.NotificationFragment;
import com.example.mosis_ispit.secondscreen.view.fragments.ProfileFragment;
import com.example.mosis_ispit.secondscreen.view.fragments.ProfileInfo;
import com.example.mosis_ispit.secondscreen.view.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainScreenActivity extends AppCompatActivity implements ProfileInfo.ProfileInfoListener, OnGetDataListener {
    private Fragment selectedFragment;
    private BottomNavigationView navView;
    private OnGetDataListener listener;
    private

    static final int LOCATION_PERMISSION = 1;

    Bundle b;
    FirebaseAuth auth;
    DatabaseReference database;
    StorageReference storage;
    public static final long MB=1024*1024;
    Bitmap avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_main_screen);
        navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(2).setChecked(true);
        b = new Bundle();
        listener = (OnGetDataListener) this;
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE},LOCATION_PERMISSION);
        } else {
            storage.child("users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("profile_img").getBytes(100*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] data = task.getResult();
                    listener.imageRetrieved(data);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String permissions[], int[] grantedResults) {
        storage.child("users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("profile_img").getBytes(100*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                byte[] data = task.getResult();
                listener.imageRetrieved(data);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigationSearch:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.navigationCreate:
                    selectedFragment = new CreateFragment();
                    break;
                case R.id.navigationMap:
                    selectedFragment = new MapFragment();
                    break;
                case R.id.navigationNotifications:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.navigationProfile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    selectedFragment.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
            });
            return true;
        }
    };

    @Override
    public void onLogout() {
        if(selectedFragment instanceof ProfileFragment) {
//        wrapper.clearPreferences("token");
            auth.signOut();
            Intent intent = new Intent(MainScreenActivity.this, LogInActivity.class);
            startActivity(intent);
            MainScreenActivity.this.finish();
        }
    }

    @Override
    public void imageRetrieved(byte[] img) {
        avatar = BitmapFactory.decodeByteArray(img, 0, img.length);
//        avatar = Bitmap.createScaledBitmap(avatar, 120, 120, true);
        database.child("users").child(auth.getCurrentUser().getUid()).child("data").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        User user_data = dataSnapshot.getValue(User.class);

                        b.putParcelable("image", avatar);
                        b.putString("username", user_data.getUsername());
                        b.putString("fullname", user_data.FullName());
                        b.putString("firstName", user_data.getFirstName());
                        b.putString("lastName", user_data.getLastName());
                        b.putString("email", user_data.getEmail());
                        b.putString("rank", user_data.getRank()+"");
                        b.putString("points", user_data.getPoints()+"");
                        b.putString("friends", ""+user_data.friends.size());
                        b.putString("discussions", ""+user_data.discussions.size());
                        if (selectedFragment == null) {
                            navView.getMenu().getItem(2).setChecked(true);
                            selectedFragment = new MapFragment();
                        }
                        selectedFragment.setArguments(b);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (selectedFragment != null) {
            selectedFragment.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle InstanceState) {
        super.onSaveInstanceState(InstanceState);
        InstanceState.clear();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d("MapFragmentLifecycle", "Activity: onAttachFragment");
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d("MapFragmentLifecycle", "Activity: onAttachFragment 2");
    }
}