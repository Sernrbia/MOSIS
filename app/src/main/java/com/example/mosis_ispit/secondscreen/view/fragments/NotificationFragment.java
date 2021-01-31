package com.example.mosis_ispit.secondscreen.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.BluetoothService;
import com.example.mosis_ispit.addon.DiscussionHistoryAdapter;
import com.example.mosis_ispit.addon.NotificationsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {
    private Button accept, deny;
    private ListView notifications;
    private int selectedUser;
    private String selectedUserString;
    private DatabaseReference myref;
    private FirebaseAuth auth;
    private ArrayList<String> uids;
    private ArrayList<String> usernames;
    private ValueEventListener notificationListener;
    public NotificationsAdapter mNotificationAdapter;
//    private ArrayList<String> requests;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_layout, container, false);
        accept = view.findViewById(R.id.notification_layout_accept);
        deny = view.findViewById(R.id.notification_layout_deny);
        notifications = view.findViewById(R.id.notification_layout_requests);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        requests = new ArrayList<>();

        String ownUsername = (String) this.getArguments().getString("username");
        uids = new ArrayList<String>();
        usernames = new ArrayList<String>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        auth= FirebaseAuth.getInstance();
        notificationListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ArrayList<String> us = new ArrayList<String>();
                    HashMap<String, HashMap<String, String>> o = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                    uids.clear();
                    usernames.clear();
                    if (o != null) {
                        for (Map.Entry disc : o.entrySet()) {
                            us.add((String) disc.getKey() + ": " + disc.getValue());
                            uids.add((String) disc.getKey());
                            usernames.add((String) disc.getValue());
                        }

                        mNotificationAdapter = new NotificationsAdapter(getContext(), R.layout.notification_layout_view, us);
                        notifications.setAdapter(mNotificationAdapter);
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

        myref.child("users").child(auth.getCurrentUser().getUid()).child("notifications").addValueEventListener(notificationListener);


        notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser = i;
                accept.setEnabled(true);
                deny.setEnabled(true);
            }
        });

        accept.setEnabled(false);
        deny.setEnabled(false);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myref.child("users").child(auth.getCurrentUser().getUid()).child("friends").child(uids.get(selectedUser)).setValue(usernames.get(selectedUser));
                myref.child("users").child(uids.get(selectedUser)).child("friends").child(auth.getCurrentUser().getUid()).setValue(ownUsername);
                myref.child("users").child(auth.getCurrentUser().getUid()).child("notifications").child(uids.get(selectedUser)).removeValue();
                mNotificationAdapter.remove(mNotificationAdapter.getItem(selectedUser));
                mNotificationAdapter.notifyDataSetChanged();
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myref.child("users").child(auth.getCurrentUser().getUid()).child("notifications").child(uids.get(selectedUser)).removeValue();
                mNotificationAdapter.remove(mNotificationAdapter.getItem(selectedUser));
                mNotificationAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
