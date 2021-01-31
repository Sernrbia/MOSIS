package com.example.mosis_ispit.secondscreen.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.DiscussionHistoryAdapter;
import com.example.mosis_ispit.addon.FriendListadapter;
import com.example.mosis_ispit.addon.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFriends extends Fragment {
    FirebaseAuth auth;
    private TextView friends;
    private ListView list;
    private DatabaseReference myref;
    public FriendListadapter mFriendsListAdapter;
    public ArrayAdapter<String> discussionsList;
    //    public static ProfileHistory mainActivity;
    public Activity a;
    public ValueEventListener profileFriendListener;

    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        a = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_friends, container, false);

        friends = view.findViewById(R.id.profile_friends_count);
        list = view.findViewById(R.id.profile_friends_list);
        auth=FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        auth=FirebaseAuth.getInstance();
        profileFriendListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ArrayList<String> us = new ArrayList<String>();
                    HashMap<String, HashMap<String, String>> o = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                    if (o != null) {
                        for (Map.Entry disc : o.entrySet()) {
                            us.add((String) disc.getValue());
                        }

                        mFriendsListAdapter = new FriendListadapter(getContext(), R.layout.profile_friends_view, us);
                        list.setAdapter(mFriendsListAdapter);

                        friends.setText(String.valueOf(us.size()));
                    } else {
                        friends.setText("0");
                    }
                } catch(Exception e) {
                    Log.e("Profile friends", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        };

        myref.child("users").child(auth.getCurrentUser().getUid()).child("friends").addValueEventListener(profileFriendListener);
    }
}
