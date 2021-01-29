package com.example.mosis_ispit.addon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mosis_ispit.R;
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
import java.util.Map;

public class UserStorage {
    public static final long MB = 1024*1024;
    public ArrayList<UserPosition> allPositions = new ArrayList<UserPosition>();
    public ArrayList<User> allUsers = new ArrayList<User>();
    public FriendRequest friendRequestsMap = new FriendRequest();
    public ArrayList<User> friendRequests = new ArrayList<User>();
    public ArrayList<User> usersFriends = new ArrayList<User>();
    public HashMap<String,UserPosition> userLocations = new HashMap<String,UserPosition>();
    private DatabaseReference database;
    private FirebaseAuth auth;
    StorageReference storage;
    private static final String USERS="usersLocations";
    private static final String REQUESTS="requests";
    public UserStorage(){
        auth=FirebaseAuth.getInstance();
        this.allUsers = new ArrayList<User>();
        database= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance().getReference();
        resetListeners();
        updateAllUsers();
    }

    public void resetListeners(){
        database.child(USERS).addListenerForSingleValueEvent(parentEventListener);
        database.child(REQUESTS).addChildEventListener(childEventListener1);
    }

    public void updateAllUsers(){
        allUsers.clear();
        Query q = database.child(USERS);
        q.addChildEventListener(childEventListener);
        q.addValueEventListener(valueEventListener);
    }

    public ArrayList<UserPosition> getUsersLocations(){
        return allPositions;
    }

    ValueEventListener parentEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue(UserPosition.class) != null) {
                UserPosition pos = dataSnapshot.getValue(UserPosition.class);
                if (pos.UID != null) {
                    boolean found = false;
                    if (allPositions.size() > 0) {
                        for(UserPosition up : allPositions) {
                            if (up.UID.equals(pos.UID)) {
                                up.setLatitude(pos.getLatitude());
                                up.setLongitude(pos.getLongitude());
                                found = true;
                            }
                        }
                        if (!found) {
                            allPositions.add(pos);
                        }
                    } else {
                        allPositions.add(pos);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue(UserPosition.class) != null) {
                UserPosition pos = snapshot.getValue(UserPosition.class);
                if (pos.UID != null) {
                    boolean found = false;
                    if (allPositions.size() > 0) {
                        for(UserPosition up : allPositions) {
                            if (up.UID.equals(pos.UID)) {
                                up.setLatitude(pos.getLatitude());
                                up.setLongitude(pos.getLongitude());
                                found = true;
                            }
                        }
                        if (!found) {
                            allPositions.add(pos);
                        }
                    } else {
                        allPositions.add(pos);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("Firebase Location", error.getMessage());
        }
    };

    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (dataSnapshot.getValue(UserPosition.class) != null) {
                UserPosition pos = dataSnapshot.getValue(UserPosition.class);
                if (pos.UID != null) {
                    boolean found = false;
                    if (allPositions.size() > 0) {
                        for(UserPosition up : allPositions) {
                            if (up.UID.equals(pos.UID)) {
                                up.setLatitude(pos.getLatitude());
                                up.setLongitude(pos.getLongitude());
                                found = true;
                            }
                        }
                        if (!found) {
                            allPositions.add(pos);
                        }
                    } else {
                        allPositions.add(pos);
                    }
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            UserPosition pos = dataSnapshot.getValue(UserPosition.class);
            if (pos.UID != null) {
                boolean found = false;
                if (allPositions.size() > 0) {
                    for (UserPosition up : allPositions) {
                        if (up.UID.equals(pos.UID)) {
                            up.setLatitude(pos.getLatitude());
                            up.setLongitude(pos.getLongitude());
                            found = true;
                        }
                    }
                    if (!found) {
                        allPositions.add(pos);
                    }
                } else {
                    allPositions.add(pos);
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListener1=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())){
                friendRequestsMap=dataSnapshot.getValue(FriendRequest.class);
                for(int i =0; i < allUsers.size(); i++) {
                    if(friendRequestsMap.requests.containsValue(allUsers.get(i).UID)){
                        friendRequests.add(allUsers.get(i));
                    }
                }
            }

        }


        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())){
                friendRequestsMap=dataSnapshot.getValue(FriendRequest.class);
                for(int i =0; i < allUsers.size(); i++) {
                    if(friendRequestsMap.requests.containsValue(allUsers.get(i).UID)){
                        friendRequests.add(allUsers.get(i));
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private  static class SingletonHolder{
        private static final UserStorage instance = new UserStorage();
    }

    public static UserStorage getInstance(){
        return  UserStorage.SingletonHolder.instance;
    }

}
