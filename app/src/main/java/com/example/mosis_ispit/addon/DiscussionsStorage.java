package com.example.mosis_ispit.addon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscussionsStorage {
    public static final long MB = 1024*1024;
    public ArrayList<DiscussionPosition> allDiscussions = new ArrayList<DiscussionPosition>();
    public FriendRequest friendRequestsMap = new FriendRequest();
    public ArrayList<User> friendRequests = new ArrayList<User>();
    public ArrayList<User> usersFriends = new ArrayList<User>();
    public HashMap<String,UserPosition> userLocations = new HashMap<String,UserPosition>();
    public HashMap<String, Bitmap> friendImages = new HashMap<>();
    public User currUser = new User();
    public boolean sendMyLocation = true;
    private DatabaseReference database;
    private FirebaseAuth auth;
    StorageReference storage;
    private static final String DISCUSSIONS="discussionsLocations";
    private static final String REQUESTS="requests";
    public DiscussionsStorage(){
        auth=FirebaseAuth.getInstance();
        this.allDiscussions = new ArrayList<DiscussionPosition>();
        database= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance().getReference();
        resetListeners();
    }

    public void resetListeners(){
        database.child(DISCUSSIONS).addChildEventListener(childEventListener);
    }

    ValueEventListener parentEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            DiscussionPosition pos = dataSnapshot.getValue(DiscussionPosition.class);
            if (pos.key != null) {
                boolean found = false;
                if (allDiscussions.size() > 0) {
                    for (DiscussionPosition up : allDiscussions) {
                        if (up.key.equals(pos.key)) {
                            up.setLatitude(pos.getLatitude());
                            up.setLongitude(pos.getLongitude());
                            found = true;
                        }
                    }
                    if (!found) {
                        allDiscussions.add(pos);
                    }
                } else {
                    allDiscussions.add(pos);
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            DiscussionPosition pos = dataSnapshot.getValue(DiscussionPosition.class);
            if (pos.key != null) {
                boolean found = false;
                if (allDiscussions.size() > 0) {
                    for (DiscussionPosition up : allDiscussions) {
                        if (up.key.equals(pos.key)) {
                            up.setLatitude(pos.getLatitude());
                            up.setLongitude(pos.getLongitude());
                            found = true;
                        }
                    }
                    if (!found) {
                        allDiscussions.add(pos);
                    }
                } else {
                    allDiscussions.add(pos);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            DiscussionPosition pos = dataSnapshot.getValue(DiscussionPosition.class);
            if (pos.key != null) {
                boolean found = false;
                if (allDiscussions.size() > 0) {
                    for (DiscussionPosition up : allDiscussions) {
                        if (up.key.equals(pos.key)) {
                            up.setLatitude(pos.getLatitude());
                            up.setLongitude(pos.getLongitude());
                            found = true;
                        }
                    }
                    if (!found) {
                        allDiscussions.add(pos);
                    }
                } else {
                    allDiscussions.add(pos);
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ArrayList<DiscussionPosition> getAllDiscussions() {
        return allDiscussions;
    }
}
