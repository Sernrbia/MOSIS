package com.example.mosis_ispit.secondscreen.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.InDiscussionAdapter;
import com.example.mosis_ispit.addon.User;
import com.example.mosis_ispit.addon.UserOwner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InDiscussionJoin extends AppCompatActivity {

    private boolean alreadyJoined;
    private User currUser;
    private Button join;
    private ListView users;
    private String discussionKey;
    private String discussionTopic;
    private int maxUsers;
    private UserOwner userOwner;
    public InDiscussionAdapter mInDiscussionAdapter;
    private ValueEventListener usersListListener;
    private ValueEventListener discusionListener;
    private DatabaseReference myref;
    private boolean full;
    private TextView active;
//    FF8BC34A
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_discussion_join);

        full = false;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReference();

        alreadyJoined = false;
        active = findViewById(R.id.in_discussion_status_join);
        TextView topic = findViewById(R.id.in_discussion_topic_join);
        TextView description = findViewById(R.id.in_discussion_description_join);
        TextView owner = findViewById(R.id.in_discussion_owner_join);
        join = findViewById(R.id.in_discussion_join);
        Button exit = findViewById(R.id.activity_in_discussion_exit_join);
        users = findViewById(R.id.in_discussion_users_join);

        currUser = (User) getIntent().getSerializableExtra("user");
        discussionKey = getIntent().getStringExtra("discussion");
        discussionTopic = getIntent().getStringExtra("topic");
        String discussionDesc = getIntent().getStringExtra("description");
        String discussionOwner = getIntent().getStringExtra("owner");
        maxUsers = getIntent().getIntExtra("maxUsers", 20);
        topic.setText(discussionTopic);
        description.setText(discussionDesc);
        owner.setText(discussionOwner);

        discusionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    UserOwner ownerUser = snapshot.getValue(UserOwner.class);
                    if (ownerUser != null) {
                        userOwner = ownerUser;
                        if (userOwner.active) {
                            active.setTextColor(Color.parseColor("#8BC34A"));
                            active.setText("Active");
                            join.setEnabled(!alreadyJoined);
                        } else {
                            active.setTextColor(Color.parseColor("#F44336"));
                            active.setText("Closed");
                            join.setEnabled(false);
                        }
                    }
                } catch(Exception e) {
                    Log.e("InDiscussion", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myref.child("discussions").child(discussionKey).child("owner").addValueEventListener(discusionListener);
        join.setOnClickListener(v -> {
            myref.child("discussions").child(discussionKey).child("users").child(currUser.UID).setValue(currUser.getUsername());
            userOwner.setPoints(20);

            myref.child("users/" + userOwner.UID + "/data/points").setValue(userOwner.points);
            myref.child("users/" + userOwner.UID + "/data/rank").setValue(userOwner.rank);
            myref.child("discussions/" + discussionKey + "/owner/points").setValue(userOwner.points);
            myref.child("discussions/" + discussionKey + "/owner/rank").setValue(userOwner.rank);
            int points = currUser.getPoints() + 10;
            currUser.setPoints(points);
            myref.child("users/" + currUser.UID + "/data/points").setValue(currUser.getPoints());
            myref.child("users/" + currUser.UID + "/data/rank").setValue(currUser.getRank());
            myref.child("users/" + currUser.UID + "/discussions/" + discussionKey).setValue(discussionTopic);
            finish();
        });

        exit.setOnClickListener(v -> {
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

                    mInDiscussionAdapter = new InDiscussionAdapter(InDiscussionJoin.this, R.layout.in_discussion_list_view, us);
                    users.setAdapter(mInDiscussionAdapter);
                    if (us.contains(currUser.getUsername())) {
                        Toast.makeText(InDiscussionJoin.this, "You already got points on this discussion!", Toast.LENGTH_LONG).show();
                        alreadyJoined = true;
                        join.setEnabled(false);
                    } else if (us.size() >= maxUsers) {
                        alreadyJoined = false;
                        full = true;
                        join.setEnabled(false);
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

    }

    @Override
    protected void onStop() {
        super.onStop();
        myref.child("discussions").child(discussionKey).child("users").removeEventListener(usersListListener);
        myref.child("discussions").child(discussionKey).child("owner").removeEventListener(discusionListener);
        discusionListener = null;
        usersListListener = null;
    }
}