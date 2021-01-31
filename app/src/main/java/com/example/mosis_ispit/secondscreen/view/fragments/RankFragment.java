package com.example.mosis_ispit.secondscreen.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.DiscussionHistoryAdapter;
import com.example.mosis_ispit.addon.RankingAdapter;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserForComparison;
import com.example.mosis_ispit.addon.UserPosition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RankFragment extends Fragment {
    private Activity activity;
    private FirebaseAuth auth;
    private DatabaseReference myref;

    private ListView usersRanking;
    private ArrayList<UserForComparison> users;
    private RankingAdapter mRankAdapter;
    private ValueEventListener rankingListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ranking_layout, container, false);

        usersRanking = view.findViewById(R.id.ranking_layout_users);

//        mRankAdapter = new DiscussionHistoryAdapter(getContext(), R.layout.profile_history_view, us);
//        usersRanking.setAdapter(mRankAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        auth= FirebaseAuth.getInstance();
        users = new ArrayList<>();
        rankingListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot us : dataSnapshot.getChildren()) {
                        for (DataSnapshot u : us.getChildren()) {
                            if (u.getKey().equals("data")) {
                                User user = u.getValue(User.class);
                                UserForComparison userF = new UserForComparison(user.getUsername(), user.getPoints());
                                users.add(userF);
                            }
                        }
                    }

                    Collections.sort(users);

                    mRankAdapter = new RankingAdapter(getContext(), R.layout.profile_history_view, users);
                    usersRanking.setAdapter(mRankAdapter);
                } catch(Exception e) {
                    Log.e("Rank listener", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        };

        myref.child("users").addValueEventListener(rankingListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        myref.child("users").child(auth.getCurrentUser().getUid()).child("discussions").removeEventListener(rankingListener);
        rankingListener = null;
        usersRanking.setAdapter(null);
    }
}
