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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileHistory extends Fragment {
    private FirebaseAuth auth;
    private TextView discussions;
    private ListView list;
    private DatabaseReference myref;
    public DiscussionHistoryAdapter mInDiscussionAdapter;
    public ArrayAdapter<String> discussionsList;
//    public static ProfileHistory mainActivity;
    public Activity a;
    public ValueEventListener profileHistoryListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_history, container, false);

        discussions = view.findViewById(R.id.profile_history_count);
        list = view.findViewById(R.id.profile_history_list);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        auth=FirebaseAuth.getInstance();
        profileHistoryListener = new ValueEventListener() {
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

                        mInDiscussionAdapter = new DiscussionHistoryAdapter(getContext(), R.layout.profile_history_view, us);
                        list.setAdapter(mInDiscussionAdapter);

                        discussions.setText(String.valueOf(us.size()));
                    } else {
                        discussions.setText("0");
                    }
                } catch(Exception e) {
                    Log.e("Profile history", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        };

        myref.child("users").child(auth.getCurrentUser().getUid()).child("discussions").addValueEventListener(profileHistoryListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            a = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
        myref.child("users").child(auth.getCurrentUser().getUid()).child("discussions").removeEventListener(profileHistoryListener);
        mInDiscussionAdapter = null;
        profileHistoryListener = null;
        list.setAdapter(null);
    }
}
