package com.example.mosis_ispit.secondscreen.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosis_ispit.R;
import com.example.mosis_ispit.addon.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileViewActivity extends AppCompatActivity {
    static int NEW_PLACE;
    public static final long MB=1024*1024;
    StorageReference storage;
    public ImageView avatar;
    public TextView username, email, friends, points;
    public Button close;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info_layout);

        storage= FirebaseStorage.getInstance().getReference();

        close = findViewById(R.id.profile_view_close);
        username = findViewById(R.id.profile_view_username);
        email = findViewById(R.id.profile_view_email);
        points = findViewById(R.id.profile_view_points);
        friends = findViewById(R.id.profile_view_friends_count);
        avatar = findViewById(R.id.profile_view_avatar);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        User user = null;
        try{
            Intent listIntent = getIntent();
            Bundle dataBundle = listIntent.getExtras();
            user = (User) dataBundle.get("user");
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
        if(user != null){
            storage.child("users").child(user.UID).child("profile_img").getBytes(100*MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] data = task.getResult();
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    avatar.setImageBitmap(Bitmap.createScaledBitmap(bmp, avatar.getWidth(),
                            avatar.getHeight(), false));
                }
            });
            username.setText(user.getUsername());
            email.setText(user.getPoints());
            points.setText(user.getPoints());
            friends.setText(user.friends.size()-1 + "");
        }

    }
}
