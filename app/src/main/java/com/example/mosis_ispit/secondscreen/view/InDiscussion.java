package com.example.mosis_ispit.secondscreen.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.Discussion;
import com.example.mosis_ispit.addon.InDiscussionAdapter;
import com.example.mosis_ispit.addon.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InDiscussion extends AppCompatActivity {

    private ListView users;
    private Discussion discussion;
    private String discussionKey;
    private int maxUsers;
    public InDiscussionAdapter mInDiscussionAdapter;
    private ValueEventListener usersListListener;
    private DatabaseReference myref;
    private ValueEventListener hostListener;
    private User currUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_discussion);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();

        TextView topic = findViewById(R.id.in_discussion_topic);
        TextView description = findViewById(R.id.in_discussion_description);
        TextView owner = findViewById(R.id.in_discussion_owner);
        Button end = findViewById(R.id.in_discussion_end);
        users = findViewById(R.id.in_discussion_users);

        currUser = (User) getIntent().getSerializableExtra("user");
        discussion = (Discussion) getIntent().getSerializableExtra("discussion");
        discussionKey = discussion.key;
        maxUsers = discussion.maxUsers;
        topic.setText(discussion.getTopic());
        description.setText(discussion.getDescription());
        owner.setText(discussion.ownerUsername);

        myref.child("discussions/" + discussion.key + "/owner/points").setValue(currUser.getPoints());
        myref.child("discussions/" + discussion.key + "/owner/rank").setValue(currUser.getRank());
        myref.child("users/" + currUser.UID + "/data/points").setValue(currUser.getPoints());
        myref.child("users/" + currUser.UID + "/data/rank").setValue(currUser.getRank());
        myref.child("users/" + currUser.UID + "/discussions/" + discussion.key).setValue(discussion.getTopic());
        myref.child("discussionsLocations").child(discussionKey).child("points").setValue(currUser.getPoints());
        end.setOnClickListener(v -> {
            discussion.active = false;
            myref.child("discussions/" + discussion.key + "/owner/active").setValue(discussion.active);
            Intent data = new Intent();
            setResult(12, data);
//            data.putExtra("host", currUser);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        usersListListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ArrayList<String> us = new ArrayList<>();
                    for (DataSnapshot u : dataSnapshot.getChildren()) {
                        us.add((String) u.getValue());
                    }

                    mInDiscussionAdapter = new InDiscussionAdapter(InDiscussion.this, R.layout.in_discussion_list_view, us);
                    users.setAdapter(mInDiscussionAdapter);

                    if (us.size() >= maxUsers) {
                        discussion.active = false;
                        myref.child("discussions/" + discussion.key + "/data/active").setValue(discussion.active);
                        myref.child("discussions/" + discussion.key + "/owner/active").setValue(discussion.active);
                        Intent data = new Intent();
                        setResult(12, data);
                        finish();
                    }
                } catch(Exception e) {
                    Log.e("InDiscussion", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        };
        myref.child("discussions").child(discussionKey).child("users").addValueEventListener(usersListListener);

        hostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User host = snapshot.getValue(User.class);
                if (host != null) {
                    currUser = host;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myref.child("users").child(currUser.UID).child("data").addValueEventListener(hostListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myref.child("discussions").child(discussionKey).child("users").removeEventListener(usersListListener);
        myref.child("users").child(currUser.UID).child("data").removeEventListener(hostListener);
        usersListListener = null;
        hostListener = null;
    }
}