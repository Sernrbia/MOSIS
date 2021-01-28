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

public class UserStorage {
    public static final long MB = 1024*1024;
    public ArrayList<User> allUsers = new ArrayList<User>();
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
    private static final String USERS="users";
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
    }

    public ArrayList<User> findUsersFriends(){
        ArrayList<User> lista = new ArrayList<>();
        for(int i=0;i<allUsers.size();i++){
            if(currUser.friends.containsValue(allUsers.get(i).UID)){
                lista.add(allUsers.get(i));
            }
        }
        usersFriends = lista;
        database.child("locations").addChildEventListener(childEventListener2);
        for(int i=0;i<usersFriends.size();i++){
            final String s = usersFriends.get(i).UID;
            storage.child("users").child(s).child("profile").getBytes(5*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] data = task.getResult();
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap scaledBmp = bmp.createScaledBitmap(bmp,100,100,false);
                    friendImages.put(s,scaledBmp);
                }
            });
        }

        return lista;
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
            Object o = dataSnapshot.getValue();
//            String userId = dataSnapshot.getKey();
//            User user = dataSnapshot.getValue(User.class);
//            user.UID = userId;
//            if(userId.equals(auth.getCurrentUser().getUid())){
//                currUser = user;
//            }
//            allUsers.add(0,user);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Object o = dataSnapshot.getValue();
//            String userId = dataSnapshot.getKey();
//            User user = dataSnapshot.getValue(User.class);
//            user.UID = userId;
//            for(int i=0;i<allUsers.size();i++){
//                if(allUsers.get(i).UID.equals(userId)) {
//                    allUsers.set(i,user);
//                }
//            }
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

    ChildEventListener childEventListener2=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(!dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())){
                userLocations.put(dataSnapshot.getKey(),dataSnapshot.getValue(UserPosition.class));
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

    public ArrayList<User> filterSuggestions(){
        ArrayList<User> suggestions=new ArrayList<>();
        for (int i=0;i<allUsers.size();i++){
            if(!auth.getCurrentUser().getUid().equals(allUsers.get(i).UID)){
                boolean flag = false;
                boolean flag1 = false;
                for(int j=0; j<friendRequests.size(); j++){
                    if(allUsers.get(i).UID.equals(friendRequests.get(j).UID)){
                        flag = true;
                    }
                }
                for(int z=0; z<usersFriends.size(); z++){
                    if(allUsers.get(i).UID.equals(usersFriends.get(z).UID)){
                        flag1 = true;
                    }
                }
                if(!flag && !flag1){
                    suggestions.add(allUsers.get(i));
                }
            }
        }
        return suggestions;
    }

    public HashMap<String,UserPosition> getFriendsLocations(){
        HashMap<String,UserPosition> retMap = new HashMap<>();
        for(int i=0;i<usersFriends.size();i++){
            retMap.put(usersFriends.get(i).UID,userLocations.get(usersFriends.get(i).UID));
        }
        return retMap;
    }

    public User getFriend(String friendId) {
        for(int i=0;i<usersFriends.size();i++){
            if(usersFriends.get(i).UID.equals(friendId))
                return usersFriends.get(i);
        }
        return null;
    }

    private  static class SingletonHolder{
        private static final UserStorage instance = new UserStorage();
    }

    public static UserStorage getInstance(){
        return  UserStorage.SingletonHolder.instance;
    }

}
