package com.example.mosis_ispit.secondscreen.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    Bundle b;
//    private TextView mTextMessage;
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
//                        String e = "";
//                        String f = "";
//                        String l = "";
//                        String p = "";
//                        long po = 0;
//                        String ra = "";
//                        String us = "";
//                        for (DataSnapshot resultSnapshot: dataSnapshot.getChildren()) {
//                            if (resultSnapshot.getKey().equals("email")) {
//                                e = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("firstName")) {
//                                f = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("lastName")) {
//                                l = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("password")) {
//                                p = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("points")) {
//                                po = (Long) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("rank")) {
//                                ra = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("username")) {
//                                us = (String) resultSnapshot.getValue();
//                            } else if (resultSnapshot.getKey().equals("discussions")) {
//                                ArrayList<User> userss = new ArrayList<User>();
//                                boolean active = false;
//                                String desc;
//                                String top;
//                                double latit;
//                                double longi;
//                                boolean ope;
//                                for (DataSnapshot resultSnapshot2 : dataSnapshot.child("discussions").getChildren()) {
//                                    Log.d("SNAPSHOT", resultSnapshot2.toString());
////                            if (resultSnapshot.getKey().equals("email")) {
////                                email = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("firstName")) {
////                                firstName = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("lastName")) {
////                                lastName = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("password")) {
////                                password = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("points")) {
////                                points = (int) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("rank")) {
////                                rank = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("username")) {
////                                username = (String) resultSnapshot.getValue();
////                            }
//                                }
//                            } else if (resultSnapshot.getKey().equals("discussionsHistory")) {
//
//                            }
//                        }
//                        User user_data = new User(us, f, l, e, p, (int) po, ra);
//                        user_data.UID = auth.getCurrentUser().getUid();
//                        HashMap<String, Discussion> discus = new HashMap<String, Discussion>();
//                        for (DataSnapshot resultSnapshot: dataSnapshot.child("discussions").getChildren()) {
//                            Log.d("SNAPSHOT", resultSnapshot.toString());
////                            if (resultSnapshot.getKey().equals("email")) {
////                                email = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("firstName")) {
////                                firstName = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("lastName")) {
////                                lastName = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("password")) {
////                                password = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("points")) {
////                                points = (int) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("rank")) {
////                                rank = (String) resultSnapshot.getValue();
////                            } else if (resultSnapshot.getKey().equals("username")) {
////                                username = (String) resultSnapshot.getValue();
////                            }
//                        }
//                        username = user_data.getUsername();
//                        fullname = user_data.FullName();
//                        email = user_data.getEmail();
//                        rank = user_data.getRank()+"";
//                        points = user_data.getPoints()+"";
//                        friends = ""+user_data.friends.size();
//                        discussions = ""+user_data.discussions.size();
//                        tokensPlaced.setText(user_data.tokensPlaced+"");
//                        friendsNumber.setText((user_data.friends.size()-1)+"");
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
                            // your fragment code
                            navView.getMenu().getItem(2).setChecked(true);
                            selectedFragment = new MapFragment();
                        }
                        selectedFragment.setArguments(b);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
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
}