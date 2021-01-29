package com.example.mosis_ispit.secondscreen.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.AddFriendsAdapter;
import com.example.mosis_ispit.addon.Discussion;
import com.example.mosis_ispit.addon.InDiscussionAdapter;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.secondscreen.view.fragments.MapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class InDiscussion extends AppCompatActivity {

    private User currUser;
    private TextView topic, description, owner;
    private Button end;
    private ListView users;
    private Discussion discussion;
    public InDiscussionAdapter mInDiscussionAdapter;
    public static InDiscussion mainActivity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_discussion);
        mainActivity = this;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myref = database.getReference();

        topic = findViewById(R.id.in_discussion_topic);
        description = findViewById(R.id.in_discussion_description);
        owner = findViewById(R.id.in_discussion_owner);
        end = findViewById(R.id.in_discussion_end);
        users = findViewById(R.id.in_discussion_users);

        currUser = (User) getIntent().getSerializableExtra("user");
        discussion = (Discussion) getIntent().getSerializableExtra("discussion");
        topic.setText(discussion.getTopic());
        description.setText(discussion.getDescription());
        owner.setText(discussion.getOwner().getUsername());

        if (getIntent().getSerializableExtra("requestCode").equals("CREATE_DISCUSSION")) {
            discussion.getOwner().setPoints(discussion.getOwner().getPoints() + 20);
            myref.child("discussions/" + discussion.key + "/data/owner/points").setValue(discussion.getOwner().getPoints());
            myref.child("discussions/" + discussion.key + "/data/owner/rank").setValue(discussion.getOwner().getRank());
//            discussion.getOwner().discussions.add(discussion);
//            discussion.getOwner().discussionsHistory.add(discussion);
            myref.child("users/" + discussion.getOwner().UID + "/data/points").setValue(discussion.getOwner().getPoints());
            myref.child("users/" + discussion.getOwner().UID + "/data/rank").setValue(discussion.getOwner().getRank());
            Discussion disc = new Discussion(discussion.getTopic(), discussion.getDescription(), discussion.getLongitude(), discussion.getLatitude(), discussion.isOpen());
            myref.child("users/" + discussion.getOwner().UID + "/discussions/" + discussion.key).setValue(discussion.getTopic());
//            myref.child("users/" + discussion.getOwner().UID).addValueEventListener(new ValueEventListener() {
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User user_data = dataSnapshot.getValue(User.class);
//                    if (user_data.discussions != null && user_data.discussionsHistory != null) {
//                        user_data.discussions.add(discussion);
//                        user_data.discussionsHistory.add(discussion);
//                    } else if (user_data.discussionsHistory != null) {
//                        ArrayList<Discussion> discussions = new ArrayList<Discussion>();
//                        discussions.add(discussion);
//                        user_data.discussions = discussions;
//                        user_data.discussionsHistory.add(discussion);
//                    } else if (user_data.discussions != null) {
//                        ArrayList<Discussion> discussions = new ArrayList<Discussion>();
//                        discussions.add(discussion);
//                        user_data.discussions.add(discussion);
//                        user_data.discussionsHistory = discussions;
//                    } else {
//                        ArrayList<Discussion> discussions = new ArrayList<Discussion>();
//                        discussions.add(discussion);
//                        user_data.discussions = discussions;
//                        user_data.discussionsHistory = discussions;
//                    }
//
//                    myref.child("users/" + discussion.getOwner().UID + "/discussions").setValue(discussion);
//                    myref.child("users/" + discussion.getOwner().UID + "/discussionsHistory").setValue(discussion);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
            end.setOnClickListener(v -> {
                discussion.active = false;
                myref.child("activeDiscussions").child(discussion.key).removeValue();
                myref.child("discussions/" + discussion.key + "/data/active").setValue(discussion.active);
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            });
        }

        if (!currUser.getUsername().equals(discussion.getOwner().getUsername())) {
            end.setText("JOIN");
            end.setBackgroundColor(Color.parseColor("#4CAF50"));
            end.setOnClickListener(v -> {
                discussion.getOwner().setPoints(discussion.getOwner().getPoints() + 10);
                myref.child("users/" + discussion.getOwner().UID + "/points").setValue(discussion.getOwner().getPoints());
                myref.child("users/" + discussion.getOwner().UID + "/rank").setValue(discussion.getOwner().getRank());
                currUser.setPoints(discussion.getOwner().getPoints() + 10);
                currUser.discussionsHistory.put(discussion.key, discussion);
                myref.child("users/" + currUser.UID + "/points").setValue(discussion.getOwner().getPoints());
                myref.child("users/" + currUser.UID + "/rank").setValue(discussion.getOwner().getRank());
                myref.child("users/" + currUser.UID + "/discussionsHistory").setValue(currUser.discussionsHistory);
                finish();
            });
        }
        myref.child("discussions").child(discussion.key).child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> us = new ArrayList<String>();
                        for (DataSnapshot u : dataSnapshot.getChildren()) {
                            us.add((String) u.getValue());
                        }

                        us.add("default1");
                        us.add("default2");
                        us.add("default3");
                        for (String user : us) {
                            mInDiscussionAdapter = new InDiscussionAdapter(mainActivity, R.layout.in_discussion_list_view, us);
                            users.setAdapter(mInDiscussionAdapter);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }
}